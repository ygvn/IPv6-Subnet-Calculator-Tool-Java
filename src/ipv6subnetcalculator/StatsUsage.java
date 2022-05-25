/*
Copyright (c) 2010-2022, Yucel Guven
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ipv6subnetcalculator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 *
 * @author Yucel Guven
 */
public final class StatsUsage extends javax.swing.JFrame {

//<editor-fold defaultstate="collapsed" desc="special initials/constants -yucel">
    ListAssignedfromDB listassigned = null;
    private int gcWidth;
    private int gcHeight;
    private double angle = 0;
    private String assigned;
    private String available;
    private String prefix;
    private String end;
    private short parentpflen;
    private short pflen;
    private int result = 0;
    private Statement statement = null;
    private ResultSet resultSet = null;
    //
    Boolean chks = false;
    static Connection MySQLconnection = null;
    public static DBServerInfo dbserverInfo = new DBServerInfo();
    private BigInteger rangetotal = BigInteger.ZERO;
    private double percent = 0;
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private java.awt.Point lastPos = new java.awt.Point();
    MyPanel mypanel;
//</editor-fold>

    private void EscapeKey() {
        int hc = this.hashCode();
        // For ESCAPE key:
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        SwingUtilities.getUIInputMap(this.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, hc);
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, hc);
        SwingUtilities.getUIActionMap(this.getRootPane()).put(hc, new Action() {
            @Override
            public void addPropertyChangeListener(PropertyChangeListener pl) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void actionPerformed(ActionEvent ae) {
                IPv6SubnetCalculator.RemoveWindowItem(hc);
                dispose();
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public Object getValue(String string) {
                return null;
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void putValue(String string, Object o) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void setEnabled(boolean bln) {
                bln = true;
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public boolean isEnabled() {
                return true;
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void removePropertyChangeListener(PropertyChangeListener pl) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

        });
    }

    public int MySQLquery() {

        if (StatsUsage.MySQLconnection == null) {
            return -1;
        }

        int r = 0;

        String MySQLcmd = "SELECT COUNT(*) FROM "
                + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                + " WHERE ( prefix BETWEEN inet6_aton('" + prefix.split("/")[0] + "')"
                + " AND inet6_aton('" + end.split("/")[0] + "')"
                + " AND parentpflen= " + parentpflen + " AND pflen= " + pflen + " );";

        try {
            IPv6SubnetCalculator.UpdateDbStatus();
            statement = StatsUsage.MySQLconnection.createStatement();
            resultSet = statement.executeQuery(MySQLcmd);

            resultSet.next();
            r = Integer.parseInt(resultSet.getString(1));
            return r;
        } catch (NumberFormatException | SQLException ex) {
            MsgBox.Show(null, ex.toString(), "Error");
            IPv6SubnetCalculator.UpdateDbStatus();
            return -1;
        }
    }

    public void setConnection(Connection con) {
        MySQLconnection = con;
    }

    public void Calculate() {

        result = MySQLquery();
        if (result < 0) {
            return;
        }

        this.rangetotal = BigInteger.ONE.shiftLeft(pflen - parentpflen);
        double ratio = ((double) result / rangetotal.doubleValue());
        this.percent = ratio * 100;
        this.angle = (ratio * 360);

        this.assigned = "";
        this.available = "";
        if (ratio == 0 || ratio > 0.01) {
            assigned = String.format(Locale.ROOT, "%.2f", percent);
            available = String.format(Locale.ROOT, "%1$.2f", (100 - Double.parseDouble(assigned)));
        } else {
            assigned = String.format(Locale.ROOT, "%.5f", percent);
            available = String.format(Locale.ROOT, "%1$.5f", (100 - Double.parseDouble(assigned)));
        }

        assigned += "% Assigned";
        available += "% Available";

        UpdateTextBox();
    }

    public void UpdateTextBox() {

        jTextArea1.setText("\r\n " + jLabelRange.getText() + "\r\n\r\n");
        jTextArea1.setText(jTextArea1.getText() + " Total Prefixes: \t"
                + String.valueOf(rangetotal) + "\r\n\r\n");
        jTextArea1.setText(jTextArea1.getText() + " Assigned Prefixes: \t"
                + String.valueOf(result) + "\r\n");
        jTextArea1.setText(jTextArea1.getText() + " Available Prefixes: \t"
                + String.valueOf(rangetotal.subtract(BigInteger.valueOf(result))) + "\r\n");
        jTextArea1.setText(jTextArea1.getText() + " Assigned Percentage: \t"
                + String.valueOf(assigned.split("%")[0] + "%" + "\r\n"));
        jTextArea1.setText(jTextArea1.getText() + " Available Percentage: \t"
                + String.valueOf(available.split("%")[0] + "%"));
    }

    /**
     * Creates new form StatsUsage
     *
     * @param prefix
     * @param end
     * @param parentpflen
     * @param pflen
     * @param chks
     * @param sqlcon
     * @param servinfo
     */
    public StatsUsage(String prefix, String end, short parentpflen, short pflen,
            Boolean chks, Connection sqlcon, DBServerInfo servinfo) {

        initComponents();
        //
        EscapeKey();
        IPv6SubnetCalculator.AddWindowItem(this.getTitle(), (JFrame) SwingUtilities.getRoot(this), this.hashCode());
        this.prefix = prefix;
        this.end = end;
        this.parentpflen = parentpflen;
        this.pflen = pflen;
        this.chks = chks;
        StatsUsage.MySQLconnection = sqlcon;
        StatsUsage.dbserverInfo = servinfo;

        jLabelRange.setText("Range> " + prefix + "-" + pflen);

        //this.jTextArea1.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        this.gcWidth = jTextArea1.getWidth();
        this.gcHeight = jTextArea1.getHeight();
        //
        Calculate();
        UpdateTextBox();
        mypanel = new MyPanel(gcWidth, gcHeight, this.angle, this.assigned, this.available);
        this.add(mypanel);
        this.pack();
        mypanel.setLocation(12, 50);
        mypanel.setSize(jTextArea1.getSize());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItemSelectAll = new javax.swing.JMenuItem();
        jMenuItemCopy = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabelRange = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButtonListPrefixes = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jLabeldbstatus = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuStatTools = new javax.swing.JMenu();
        jMenuItemListAssignedPrefixes = new javax.swing.JMenuItem();
        jMenuItemListParentNets = new javax.swing.JMenuItem();

        jMenuItemSelectAll.setText("Select All");
        jMenuItemSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSelectAllActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemSelectAll);

        jMenuItemCopy.setText("Copy");
        jMenuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemCopy);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stats Usage");
        setName("StatsUsage"); // NOI18N
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setPreferredSize(new java.awt.Dimension(99, 18));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        jLabelRange.setForeground(new java.awt.Color(9, 160, 254));
        jLabelRange.setText("Range>");

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setBorder(null);
        jTextArea1.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(jTextArea1);

        jButtonListPrefixes.setText("List Assigned Prefixes");
        jButtonListPrefixes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonListPrefixesActionPerformed(evt);
            }
        });

        jButtonRefresh.setText("Refresh");
        jButtonRefresh.setToolTipText("Refresh from DatabaseServer");
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jLabeldbstatus.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabeldbstatus.setForeground(new java.awt.Color(9, 160, 254));
        jLabeldbstatus.setText("db=Down");

        jMenuFile.setMnemonic('F');
        jMenuFile.setText("File");

        jMenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItemExit.setMnemonic('x');
        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar1.add(jMenuFile);

        jMenuStatTools.setMnemonic('T');
        jMenuStatTools.setText("Tools");

        jMenuItemListAssignedPrefixes.setText("List Assigned Prefixes");
        jMenuItemListAssignedPrefixes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemListAssignedPrefixesActionPerformed(evt);
            }
        });
        jMenuStatTools.add(jMenuItemListAssignedPrefixes);

        jMenuItemListParentNets.setText("List All ParentNets");
        jMenuItemListParentNets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemListParentNetsActionPerformed(evt);
            }
        });
        jMenuStatTools.add(jMenuItemListParentNets);

        jMenuBar1.add(jMenuStatTools);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelRange)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonListPrefixes)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonRefresh)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addGap(53, 53, 53)
                .addComponent(jLabeldbstatus)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelRange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 192, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonListPrefixes)
                    .addComponent(jButtonRefresh)
                    .addComponent(jButtonExit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabeldbstatus)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonListPrefixesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListPrefixesActionPerformed

        IPv6SubnetCalculator.UpdateDbStatus();

        if (MySQLconnection != null) {
            listassigned = new ListAssignedfromDB(prefix, end,
                    parentpflen, pflen, chks, MySQLconnection, dbserverInfo);

            listassigned.SetNewValues(prefix, end, parentpflen, pflen, chks);
            IPv6SubnetCalculator.listAssignedfromdb.add(listassigned);
            listassigned.setVisible(true);
            listassigned.jLabeldbstatus.setText("db=Up ");
        }
    }//GEN-LAST:event_jButtonListPrefixesActionPerformed

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed

        IPv6SubnetCalculator.UpdateDbStatus();
        Calculate();
        mypanel.Refresh(this.angle, this.assigned, this.available);
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed

        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
        dispose();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSelectAllActionPerformed
        this.jTextArea1.requestFocus();
        this.jTextArea1.selectAll();
    }//GEN-LAST:event_jMenuItemSelectAllActionPerformed

    private void jMenuItemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopyActionPerformed
        this.jTextArea1.getTransferHandler().exportToClipboard(this.jTextArea1, this.clipboard, TransferHandler.COPY);
    }//GEN-LAST:event_jMenuItemCopyActionPerformed

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        this.lastPos = new java.awt.Point(evt.getX(), evt.getY());
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if (SwingUtilities.isRightMouseButton(evt)) {
            this.setLocation(this.getLocation().x + evt.getX() - this.lastPos.x,
                    this.getLocation().y + evt.getY() - this.lastPos.y);
        }
    }//GEN-LAST:event_formMouseDragged

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
        dispose();
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemListAssignedPrefixesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemListAssignedPrefixesActionPerformed
        this.jButtonListPrefixesActionPerformed(null);
    }//GEN-LAST:event_jMenuItemListAssignedPrefixesActionPerformed

    private void jMenuItemListParentNetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemListParentNetsActionPerformed

        IPv6SubnetCalculator.UpdateDbStatus();

        if (MySQLconnection != null) {
            ListParentNets listparents = new ListParentNets(MySQLconnection, dbserverInfo);

            IPv6SubnetCalculator.listParentNets.add(listparents);
            listparents.setVisible(true);
            listparents.jLabeldbstatus.setText("db=Up ");
        }
    }//GEN-LAST:event_jMenuItemListParentNetsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonListPrefixes;
    private javax.swing.JButton jButtonRefresh;
    javax.swing.JLabel jLabelRange;
    javax.swing.JLabel jLabeldbstatus;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemListAssignedPrefixes;
    private javax.swing.JMenuItem jMenuItemListParentNets;
    private javax.swing.JMenuItem jMenuItemSelectAll;
    private javax.swing.JMenu jMenuStatTools;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}

final class MyPanel extends JPanel {

    private final int gcWidth;
    private final int gcHeight;
    private double angle = 0;
    private String assigned;
    private String available;
    private BasicStroke s1;
    private BasicStroke s2;
    private BasicStroke s3;

    public MyPanel(int gcWidth, int gcHeight, double angle,
            String assigned, String available) {

        this.gcWidth = gcWidth;
        this.gcHeight = gcHeight;
        this.angle = angle;
        this.assigned = assigned;
        this.available = available;

        setBackground(Color.WHITE);
        s1 = new BasicStroke(1);
        s2 = new BasicStroke(2);
    }

    /*
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(450, 400);
    }
     */
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //
        Graphics2D gc = (Graphics2D) g;

        gc.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //gc.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        //gg.setFont(new Font("Verdana", Font.BOLD, 14));
        gc.clearRect(0, 0, this.gcWidth, this.gcHeight);
        gc.setColor(Color.WHITE);
        gc.fillRect(0, 0, this.gcWidth, this.gcHeight);

        //gc.setColor(Color.RED);
        gc.setColor(Color.decode("#f8606a"));
        gc.setStroke(s2);
        gc.drawOval(90, 25, 120, 120);

        gc.setFont(new Font("Cantarell", Font.BOLD, 14));
        if (this.angle == 0.00) {
            gc.setColor(Color.decode("#f8606a"));
            gc.setStroke(s2);
            gc.drawString("EMPTY", 130, 90);
        } else if (this.angle == 360.00) {
            gc.setColor(Color.decode("#f8606a"));
            gc.setStroke(s2);
            gc.drawLine(194, 46, 108, 128);
            gc.drawLine(106, 46, 192, 128);
            gc.drawString("FULL", 134, 50);
        } else {
            gc.fillArc(90, 25, 120, 120, 0, (int) this.angle);
            gc.setColor(Color.BLACK);
            gc.setStroke(s1);
            gc.drawLine(151, 85, 209, 85);
        }

        gc.setColor(Color.decode("#f8606a"));
        gc.drawString(this.assigned, 220, 75);

        gc.setColor(Color.BLACK);
        gc.drawString(this.available, 220, 105);

    }

    public void Refresh(double angle, String assigned, String available) {
        this.angle = angle;
        this.assigned = assigned;
        this.available = available;
        repaint();
    }
}
