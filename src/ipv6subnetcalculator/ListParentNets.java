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

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 *
 * @author Yucel Guven
 */
public final class ListParentNets extends javax.swing.JFrame {

//<editor-fold defaultstate="collapsed" desc="special initials/constants -Yücel">
    //
    SEaddress seaddr = new SEaddress();
    String prefix = "";
    String parentNet = "";
    String end = "";
    short pflen = 0;
    short parentpflen = 0;
    String db_FirstItem = "";
    String db_LastItem = "";
    int db_TotalRecords = 0;
    int page_records = 0;
    final int records_perpage = 32;
    int currentOffset = 0;

    DatabaseUI dbUI = null;
    static Connection MySQLconnection = null;
    public DBServerInfo dbserverInfo = new DBServerInfo();
    Boolean chks = Boolean.FALSE;
    Statement statement = null;
    ResultSet resultSet = null;
    public ArrayList<String> liste = new ArrayList<String>();
    //
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private java.awt.Point lastPos = new java.awt.Point();
    //
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

    public static class MyCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (index % 12 == 0 && !isSelected) {
                setBackground(new Color(236, 236, 236));
            }

            setOpaque(true);
            return this;
        }
    }

    public void setConnection(Connection con) {
        MySQLconnection = con;
    }

    /*
    public void SetNewValues(String inprefix, String inend, short inparentpflen,
            short inpflen, Boolean inchks) {
        
        this.prefix = v6ST.CompressAddress(inprefix.split("/")[0]);
        this.end = v6ST.CompressAddress(inend.split("/")[0]);
        this.parentpflen = inparentpflen;
        this.pflen = inpflen;
        this.chks = inchks;
        this.page_records = 0;

        
    }*/
    
    public int MySQLquery(Boolean isRemainder) {

        if (MySQLconnection == null) {
            return -1;
        }
        int r = 0;
        String MySQLcmd = "";

        this.jList1.setModel(new DefaultListModel<String>());

        if (!isRemainder) {
            MySQLcmd = "SELECT "
                    + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, organization, "
                    + "`as-num`, phone, email, status, created, `last-updated` FROM "
                    + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                    + " WHERE pflen=parentpflen "
                    + " ORDER BY prefix "
                    + " LIMIT " + String.valueOf(this.records_perpage)
                    + " OFFSET " + String.valueOf(this.currentOffset);
        } else {
            MySQLcmd = "SELECT "
                    + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, organization, "
                    + "`as-num`, phone, email, status, created, `last-updated` FROM "
                    + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                    + " WHERE pflen=parentpflen "
                    + " ORDER BY prefix "
                    + " LIMIT " + String.valueOf(this.currentOffset)
                    + " OFFSET " + String.valueOf(0);
        }

        try {
            IPv6SubnetCalculator.UpdateDbStatus();
            statement = MySQLconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(MySQLcmd);

            if (resultSet.last()) {
                this.page_records = r = resultSet.getRow();
                resultSet.beforeFirst();
            }

            if (r > 0) {
                liste.clear();

                while (resultSet.next()) {
                    liste.add("prefix:\t\t " + resultSet.getString(1)
                            + "/" + resultSet.getString(2));
                    this.parentNet = v6ST.FindParentNet(resultSet.getString(1), Short.valueOf(resultSet.getString(3)), true);
                    liste.add("parent:\t " + this.parentNet);
                    liste.add("netname:\t " + resultSet.getString(4));
                    liste.add("person:\t\t " + resultSet.getString(5));
                    liste.add("organization:\t " + resultSet.getString(6));
                    liste.add("as-num:\t\t " + resultSet.getString(7));
                    liste.add("phone:\t\t " + resultSet.getString(8));
                    liste.add("email:\t\t " + resultSet.getString(9));
                    liste.add("status:\t\t " + resultSet.getString(10));
                    liste.add("created:\t " + resultSet.getString(11));
                    liste.add("last-updated:\t " + resultSet.getString(12));
                    liste.add(" ");
                }

                this.jList1.setListData(liste.toArray(String[]::new));
            }
            return r;

        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error:");
            IPv6SubnetCalculator.UpdateDbStatus();
            return -1;
        }
    }

    public void HowManyRecordsInDB() {

        IPv6SubnetCalculator.UpdateDbStatus();

        if (MySQLconnection == null) {
            return;
        }

        String MySQLcmd = "SELECT COUNT(*) FROM "
                + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                + " WHERE pflen=parentpflen";

        try {
            statement = MySQLconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(MySQLcmd);
            resultSet.next();

            this.db_TotalRecords = Integer.parseInt(resultSet.getString(1));

            this.jLabelstatus.setText(" Total=[" + this.db_TotalRecords + "]");
        } catch (NumberFormatException | SQLException ex) {
            MsgBox.Show(this, ex.toString(), "Error:");
            this.db_TotalRecords = 0;
        }
    }

    private void GetSelected() {
        IPv6SubnetCalculator.UpdateDbStatus();
        if (MySQLconnection != null) {
            if (this.jList1.getSelectedIndex() % 12 == 0) {
                String selected = this.jList1.getSelectedValue().split(" ")[1].trim();
                String snet = selected.split("/")[0].trim();
                short plen = Short.parseShort(selected.split("/")[1]);

                dbUI = new DatabaseUI(snet, plen, plen, MySQLconnection, dbserverInfo);
                IPv6SubnetCalculator.dbUI.add(dbUI);

                dbUI.jLabeldbstatus.setText("db=Up ");
                dbUI.setVisible(true);
            }
        }
    }

    /**
     * Creates new form ListParentNets
     *
     * @param sqlcon
     * @param servinfo
     */
    public ListParentNets(Connection sqlcon, DBServerInfo servinfo) {
        initComponents();
        //
        EscapeKey();
        IPv6SubnetCalculator.AddWindowItem(this.getTitle(), (JFrame) SwingUtilities.getRoot(this), this.hashCode());

        MySQLconnection = sqlcon;
        this.dbserverInfo = servinfo;

        this.jButtonFirstPageActionPerformed(null);
        this.jLabelcount.setText("[" + this.page_records + "]");

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
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemModify = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabelstatus = new javax.swing.JLabel();
        jLabeldbstatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabelcount = new javax.swing.JLabel();
        jButtonFirstPage = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jButtonForward = new javax.swing.JButton();
        jButtonLastPage = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jPopupMenu1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenu1PopupMenuWillBecomeVisible(evt);
            }
        });

        jMenuItemSelectAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemSelectAll.setMnemonic('A');
        jMenuItemSelectAll.setText("Select All");
        jMenuItemSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSelectAllActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemSelectAll);

        jMenuItemCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemCopy.setMnemonic('C');
        jMenuItemCopy.setText("Copy");
        jMenuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemCopy);
        jPopupMenu1.add(jSeparator1);

        jMenuItemModify.setMnemonic('M');
        jMenuItemModify.setText("Modify selected prefix");
        jMenuItemModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemModifyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemModify);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("List Parent Nets");
        setMinimumSize(new java.awt.Dimension(520, 460));
        setName("ListParentNets"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabelstatus.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabelstatus.setForeground(new java.awt.Color(9, 160, 254));
        jLabelstatus.setText(" ");

        jLabeldbstatus.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabeldbstatus.setForeground(new java.awt.Color(9, 160, 254));
        jLabeldbstatus.setText("db=Down");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabeldbstatus)
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelstatus)
                    .addComponent(jLabeldbstatus)))
        );

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { " " };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setCellRenderer(new MyCellRenderer());
        jList1.setComponentPopupMenu(jPopupMenu1);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jList1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jLabelcount.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabelcount.setForeground(new java.awt.Color(9, 160, 254));
        jLabelcount.setText("[ ]");

        jButtonFirstPage.setText("|< First Page");
        jButtonFirstPage.setToolTipText("Click to Refresh from Database");
        jButtonFirstPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFirstPageActionPerformed(evt);
            }
        });

        jButtonBack.setText(" < ");
        jButtonBack.setEnabled(false);
        jButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackActionPerformed(evt);
            }
        });

        jButtonForward.setText(" > ");
        jButtonForward.setEnabled(false);
        jButtonForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonForwardActionPerformed(evt);
            }
        });

        jButtonLastPage.setText(" >|");
        jButtonLastPage.setEnabled(false);
        jButtonLastPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLastPageActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cantarell", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(9, 160, 254));
        jLabel1.setText("List of All Symbolic Parent Prefixes");

        jLabel2.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabel2.setText("SymbolicParent or root prefixes are prefixes from which subnet prefixes are created.");

        jLabel3.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabel3.setText("They point the initial border of the prefix-range that you define using subnet slider.");

        jLabel4.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabel4.setText("Their prefix and parentprefix lengths or values are equal.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonFirstPage)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonBack)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonForward)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonLastPage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
                                .addComponent(jLabelcount))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(95, 95, 95)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel4)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(3, 3, 3)
                .addComponent(jLabel3)
                .addGap(3, 3, 3)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelcount, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonFirstPage)
                        .addComponent(jButtonBack)
                        .addComponent(jButtonForward)
                        .addComponent(jButtonLastPage)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonFirstPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFirstPageActionPerformed

        HowManyRecordsInDB();

        this.currentOffset = 0;

        int r = MySQLquery(false);

        if (r > 0) {
            if (r >= records_perpage) {
                this.jButtonBack.setEnabled(false);
                this.jButtonForward.setEnabled(true);
                this.jButtonLastPage.setEnabled(true);
            } else {
                this.jButtonBack.setEnabled(false);
                this.jButtonForward.setEnabled(false);
                this.jButtonLastPage.setEnabled(false);
            }
        } else {
            this.jButtonBack.setEnabled(false);
            this.jButtonForward.setEnabled(false);
            this.jButtonLastPage.setEnabled(false);
        }
        this.currentOffset += page_records;
        this.jLabelcount.setText("[" + page_records + "]");
    }//GEN-LAST:event_jButtonFirstPageActionPerformed

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed

        Boolean remainder = false;
        int r = 0;

        this.currentOffset = this.currentOffset - page_records - this.records_perpage;

        if (this.currentOffset < 0) {
            remainder = true;
            this.currentOffset += this.records_perpage;
            MySQLquery(remainder);

            this.jButtonBack.setEnabled(false);
            this.jButtonForward.setEnabled(true);
            this.jButtonLastPage.setEnabled(true);

            return;
        }

        r = MySQLquery(remainder);

        if (r > 0) {
            this.jButtonForward.setEnabled(true);
            this.jButtonLastPage.setEnabled(true);

            if (this.currentOffset <= 0) {
                this.jButtonBack.setEnabled(false);
                this.jButtonForward.setEnabled(true);
                this.jLabelcount.setText("[" + page_records + "]");
            }
        } else {
            this.jButtonBack.setEnabled(false);
            this.jButtonForward.setEnabled(true);
            this.jLabelcount.setText("[" + page_records + "]");
        }
        this.currentOffset += page_records;
        this.jLabelcount.setText("[" + page_records + "]");
    }//GEN-LAST:event_jButtonBackActionPerformed

    private void jButtonForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonForwardActionPerformed

        int r = MySQLquery(false);

        if (r > 0) {
            this.currentOffset += page_records;
            this.jButtonBack.setEnabled(true);

            if (this.currentOffset >= this.db_TotalRecords) {
                this.jButtonForward.setEnabled(false);
                this.jButtonLastPage.setEnabled(false);
                this.jLabelcount.setText("[" + page_records + "]");
            }
        } else {
            this.jButtonForward.setEnabled(false);
            this.jButtonLastPage.setEnabled(false);
            this.jLabelcount.setText("[" + page_records + "]");
        }
        this.jLabelcount.setText("[" + page_records + "]");
    }//GEN-LAST:event_jButtonForwardActionPerformed

    private void jButtonLastPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLastPageActionPerformed

        this.currentOffset = this.db_TotalRecords - this.records_perpage;
        int r = MySQLquery(false);

        if (r > 0) {
            this.jButtonBack.setEnabled(true);
            this.jButtonForward.setEnabled(false);
            this.jButtonLastPage.setEnabled(false);
        }
        this.currentOffset = this.db_TotalRecords;
        this.jLabelcount.setText("[" + page_records + "]");
    }//GEN-LAST:event_jButtonLastPageActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSelectAllActionPerformed
        int s = 0;
        int e = jList1.getModel().getSize() - 1;
        if (e >= 0) {
            jList1.setSelectionInterval(s, e);
        }
    }//GEN-LAST:event_jMenuItemSelectAllActionPerformed

    private void jMenuItemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopyActionPerformed
        this.jList1.getTransferHandler().exportToClipboard(this.jList1, this.clipboard, TransferHandler.COPY);
    }//GEN-LAST:event_jMenuItemCopyActionPerformed

    private void jMenuItemModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemModifyActionPerformed
        GetSelected();
    }//GEN-LAST:event_jMenuItemModifyActionPerformed

    private void jPopupMenu1PopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenu1PopupMenuWillBecomeVisible
        if (this.jList1.getSelectedIndex() % 12 == 0) {
            this.jMenuItemModify.setEnabled(true);
        } else {
            this.jMenuItemModify.setEnabled(false);
        }
    }//GEN-LAST:event_jPopupMenu1PopupMenuWillBecomeVisible

    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            GetSelected();
        }
    }//GEN-LAST:event_jList1KeyPressed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
            GetSelected();
        }
    }//GEN-LAST:event_jList1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonFirstPage;
    private javax.swing.JButton jButtonForward;
    private javax.swing.JButton jButtonLastPage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelcount;
    javax.swing.JLabel jLabeldbstatus;
    javax.swing.JLabel jLabelstatus;
    private javax.swing.JList<String> jList1;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemModify;
    private javax.swing.JMenuItem jMenuItemSelectAll;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
