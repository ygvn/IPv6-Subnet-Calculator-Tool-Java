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

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultListModel;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 *
 * @author Yucel Guven
 */
public final class ListSubnetRange extends javax.swing.JFrame {

    //<editor-fold defaultstate="collapsed" desc="special initials/constants -Yücel">
    public final int ID = 1; // ID of this Form.
    public int incomingID;

    SEaddress StartEnd = new SEaddress();
    short parentpflen = 0;
    SEaddress subnets = new SEaddress();
    SEaddress page = new SEaddress();
    final int upto = 128;

    //BigInteger currentidx = BigInteger.ZERO;
    BigInteger lastindex = BigInteger.ZERO;
    BigInteger pix = BigInteger.ZERO;
    public String findpfx = "";
    public String GotoForm_PrevValue = "";
    public BigInteger NumberOfSubnets = BigInteger.ZERO;
    BigInteger gotovalue = BigInteger.ZERO;
    BigInteger maxvalue = BigInteger.ZERO;
    Boolean is128Checked;
    Boolean selectedRange;
    int maxfontwidth = 0;
    //
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private java.awt.Point lastPos = new java.awt.Point();
    //
    static Connection MySQLconnection = null;
    DBServerInfo dbserverInfo = null;

    SaveAsTxt saveas = null;
    ListDnsReverses dnsr = null;

    DatabaseUI dbUIsend = null;

    //
//</editor-fold>
    public void setConnection(Connection con) {
        MySQLconnection = con;
    }

    /**
     * Creates new form ListSubnetRange
     *
     * @param input
     * @param sin
     * @param is128Checked
     * @param sqlcon
     * @param dbsinfo
     * @param selectedRange
     */
    public ListSubnetRange(SEaddress input, String sin, Boolean is128Checked,
            Connection sqlcon, DBServerInfo dbsinfo, Boolean selectedRange) {

        initComponents();
        
        MySQLconnection = sqlcon;
        dbserverInfo = dbsinfo;
        this.selectedRange = selectedRange;
        this.is128Checked = is128Checked;

        SetNewValues(input, sin, is128Checked);

        IPv6SubnetCalculator.AddWindowItem(this.getTitle(), (JFrame) SwingUtilities.getRoot(this), this.hashCode());
        EscapeKey();

        this.jProgressBar1.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuListSubnetRange = new javax.swing.JPopupMenu();
        contextSelectAll = new javax.swing.JMenuItem();
        contextCopy = new javax.swing.JMenuItem();
        contextListDNSRevs = new javax.swing.JMenuItem();
        contextDBSendPrefix = new javax.swing.JMenuItem();
        contextDBGetPrefix = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        contextSaveAsText = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabeldbstatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelStart = new javax.swing.JLabel();
        jLabelEnd = new javax.swing.JLabel();
        jButtonListFirstPage = new javax.swing.JButton();
        jButtonListBack = new javax.swing.JButton();
        jButtonListForward = new javax.swing.JButton();
        jButtonListLast = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabelListCount = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldTotal = new javax.swing.JTextField();
        jTextFieldGoto = new javax.swing.JTextField();
        jButtonGo = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();

        jPopupMenuListSubnetRange.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenuListSubnetRangePopupMenuWillBecomeVisible(evt);
            }
        });

        contextSelectAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        contextSelectAll.setText("Select All");
        contextSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextSelectAllActionPerformed(evt);
            }
        });
        jPopupMenuListSubnetRange.add(contextSelectAll);

        contextCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        contextCopy.setText("Copy");
        contextCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextCopyActionPerformed(evt);
            }
        });
        jPopupMenuListSubnetRange.add(contextCopy);

        contextListDNSRevs.setText("List All DNS reverse zones");
        contextListDNSRevs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextListDNSRevsActionPerformed(evt);
            }
        });
        jPopupMenuListSubnetRange.add(contextListDNSRevs);

        contextDBSendPrefix.setText("Send prefix to database...");
        contextDBSendPrefix.setEnabled(false);
        contextDBSendPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextDBSendPrefixActionPerformed(evt);
            }
        });
        jPopupMenuListSubnetRange.add(contextDBSendPrefix);

        contextDBGetPrefix.setText("Get prefix info from database...");
        contextDBGetPrefix.setEnabled(false);
        contextDBGetPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextDBGetPrefixActionPerformed(evt);
            }
        });
        jPopupMenuListSubnetRange.add(contextDBGetPrefix);
        jPopupMenuListSubnetRange.add(jSeparator1);

        contextSaveAsText.setText("Save As Text...");
        contextSaveAsText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextSaveAsTextActionPerformed(evt);
            }
        });
        jPopupMenuListSubnetRange.add(contextSaveAsText);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("List Subnet Range");
        setName("ListSubnetRange"); // NOI18N
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

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 207, 229)));
        jPanel1.setPreferredSize(new java.awt.Dimension(450, 18));

        jLabeldbstatus.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabeldbstatus.setForeground(new java.awt.Color(9, 160, 254));
        jLabeldbstatus.setText("db=Down");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabeldbstatus)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabeldbstatus))
        );

        jLabel3.setText("Range:");

        jLabelStart.setText("s>");

        jLabelEnd.setText("e>");

        jButtonListFirstPage.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jButtonListFirstPage.setText("|< FirstPage");
        jButtonListFirstPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonListFirstPageActionPerformed(evt);
            }
        });

        jButtonListBack.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jButtonListBack.setText(" < ");
        jButtonListBack.setEnabled(false);
        jButtonListBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonListBackActionPerformed(evt);
            }
        });

        jButtonListForward.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jButtonListForward.setText(" > ");
        jButtonListForward.setEnabled(false);
        jButtonListForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonListForwardActionPerformed(evt);
            }
        });

        jButtonListLast.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jButtonListLast.setText(" >| ");
        jButtonListLast.setEnabled(false);
        jButtonListLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonListLastActionPerformed(evt);
            }
        });

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setComponentPopupMenu(jPopupMenuListSubnetRange);
        jScrollPane1.setViewportView(jList1);

        jLabelListCount.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabelListCount.setForeground(new java.awt.Color(9, 160, 254));
        jLabelListCount.setText("[ ]");

        jLabel4.setText("Total:");

        jLabel5.setText("Goto:");

        jTextFieldTotal.setEditable(false);
        jTextFieldTotal.setText("0");

        jTextFieldGoto.setText("0");

        jButtonGo.setText("Go");
        jButtonGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGoActionPerformed(evt);
            }
        });

        jProgressBar1.setStringPainted(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonListFirstPage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonListBack)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonListForward)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonListLast)
                        .addGap(18, 18, 18)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelListCount))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4))
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextFieldTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                                    .addComponent(jTextFieldGoto))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonGo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelEnd)
                                    .addComponent(jLabelStart))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabelStart))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelEnd)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonListBack, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelListCount)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonListFirstPage)
                        .addComponent(jButtonListForward)
                        .addComponent(jButtonListLast)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldGoto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jButtonGo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public void Setlbdbstatus(String s) {
        this.jLabeldbstatus.setText(s);
    }

    public void SetNewValues(SEaddress input, String sin, Boolean is128Chked) {

        this.jList1.setModel(new DefaultListModel<String>());

        parentpflen = (short) input.slash;
        this.StartEnd.ID = this.ID;
        this.incomingID = input.ID;
        this.is128Checked = is128Chked;
        this.jButtonListBack.setEnabled(false);
        this.jButtonListForward.setEnabled(false);
        this.jButtonListLast.setEnabled(false);

        String[] sa = sin.split(" ");
        sa = sa[1].split("/");
        StartEnd.Resultv6 = v6ST.FormalizeAddr(sa[0]);
        StartEnd.slash = input.subnetslash;
        StartEnd.subnetslash = input.subnetslash;

        StartEnd = v6ST.StartEndAddresses(StartEnd, this.is128Checked);
        NumberOfSubnets = (StartEnd.End.subtract(StartEnd.Start)).add(BigInteger.ONE);

        String s1 = v6ST.Kolonlar(StartEnd.Start);
        String s2 = v6ST.Kolonlar(StartEnd.End);

        if (!is128Checked) {
            //DefaultStage();
            s1 = s1.substring(0, 19) + "::";
            s1 = v6ST.CompressAddress(s1);
            s2 = s2.substring(0, 19) + "::";
            s2 = v6ST.CompressAddress(s2);
            this.jLabelStart.setText("s> " + s1 + "/" + StartEnd.subnetslash);
            this.jLabelEnd.setText("e> " + s2 + "/" + StartEnd.subnetslash);
            //
            NumberOfSubnets = NumberOfSubnets.shiftRight(64);
            if (NumberOfSubnets.equals(BigInteger.ZERO)) {
                NumberOfSubnets = BigInteger.ONE;
            }
            this.jTextFieldTotal.setText(String.valueOf(NumberOfSubnets));
        } else if (is128Checked) {
            //ExpandStage();
            s1 = v6ST.CompressAddress(s1);
            s2 = v6ST.CompressAddress(s2);
            this.jLabelStart.setText("s> " + s1 + "/" + StartEnd.subnetslash);
            this.jLabelEnd.setText("e> " + s2 + "/" + StartEnd.subnetslash);
            this.jTextFieldTotal.setText(String.valueOf(NumberOfSubnets));
        }
        this.jButtonListFirstPageActionPerformed(null);
    }

    public void UpdateCount() {

        if (this.jList1.getModel().getSize() == 0) {
            jLabelListCount.setVisible(false);
            return;
        } else {
            jLabelListCount.setVisible(true);
            jLabelListCount.setText("[" + String.valueOf(this.jList1.getModel().getSize()) + "]");
        }

        if (this.jList1.getModel().getSize() != 0) {

            BigInteger numberofPage = NumberOfSubnets.shiftRight(7);

            lastindex = new BigInteger(this.jList1.getModel().getElementAt(this.jList1.getModel().getSize() - 1).split(">")[0].replace("p", ""), 10);
            lastindex = lastindex.add(BigInteger.ONE);

            if (numberofPage.compareTo(BigInteger.ONE) <= 0
                    || lastindex.equals(NumberOfSubnets.subtract(BigInteger.ONE))) {
                this.jProgressBar1.setValue(100);
                this.jProgressBar1.setString("100%");
                return;
            } else {
                int percent;
                lastindex = lastindex.multiply(new BigInteger("100", 10));
                lastindex = lastindex.divide(NumberOfSubnets);
                percent = (int) (lastindex).doubleValue(); // lastindex can not exceed 100 and prefixmax

                this.jProgressBar1.setValue(percent);
                this.jProgressBar1.setString(String.valueOf(percent) + "%");
            }
        }

    }

    public void DefaultStage() {
        /*
        stage.setWidth(425);
        stage.setHeight(470);
        prefixlist.setPrefWidth(400);
        prefixlist.setPrefHeight(260);
        hblistcount.setPrefWidth(173);
        hbstatusstr.setPrefWidth(300);
        hbdbstatus.setPrefWidth(110);
         */
    }

    public void ExpandStage() {
        /*
        stage.setWidth(625);
        stage.setHeight(470);
        prefixlist.setPrefWidth(600);
        prefixlist.setPrefHeight(260);
        hblistcount.setPrefWidth(373);
        hbstatusstr.setPrefWidth(500);
        hbdbstatus.setPrefWidth(110);
         */
    }

    private void jButtonListFirstPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListFirstPageActionPerformed
        subnets.Start = page.Start = StartEnd.Start;
        page.End = BigInteger.ZERO;
        subnets.subnetslash = StartEnd.subnetslash;
        subnets.upto = upto;

        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;

        if (subnets.Start.equals(StartEnd.End)) {
            UpdateCount();
            return;
        }

        this.jList1.setModel(new DefaultListModel<String>());

        subnets = v6ST.ListSubRangeFirstPage(subnets, is128Checked, this.selectedRange);
        if (is128Checked) {
            page.End = subnets.Start.subtract(BigInteger.ONE);
        } else if (!is128Checked) {
            page.End = subnets.Start.subtract(BigInteger.ONE.shiftLeft(64));
        }

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        if (NumberOfSubnets.compareTo(BigInteger.valueOf(upto)) <= 0) {
            this.jButtonListBack.setEnabled(false);
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);
        } else {
            this.jButtonListBack.setEnabled(false);
            this.jButtonListForward.setEnabled(true);
            this.jButtonListLast.setEnabled(true);
        }

        UpdateCount();
    }//GEN-LAST:event_jButtonListFirstPageActionPerformed

    private void jButtonListBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListBackActionPerformed

        if (is128Checked) {
            subnets.Start = page.End = page.Start.subtract(BigInteger.ONE);
        } else if (!is128Checked) {
            subnets.Start = page.End = page.Start.subtract(BigInteger.ONE.shiftLeft(64));
        }

        subnets.subnetslash = StartEnd.subnetslash;
        subnets.upto = upto;

        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;

        this.jList1.setModel(new DefaultListModel<String>());

        subnets = v6ST.ListSubRangePageBackward(subnets, is128Checked, this.selectedRange);
        if (is128Checked) {
            page.Start = subnets.Start.add(BigInteger.ONE);
        } else if (!is128Checked) {
            page.Start = subnets.Start.add(BigInteger.ONE.shiftLeft(64));
        }

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        if (subnets.subnetidx.equals(BigInteger.ZERO)) {
            this.jButtonListBack.setEnabled(false);
            this.jButtonListForward.setEnabled(true);
            this.jButtonListLast.setEnabled(true);
        } else {
            this.jButtonListForward.setEnabled(true);
            this.jButtonListLast.setEnabled(true);
        }
        UpdateCount();
    }//GEN-LAST:event_jButtonListBackActionPerformed

    private void jButtonListForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListForwardActionPerformed

        if (is128Checked) {
            subnets.Start = page.Start = page.End.add(BigInteger.ONE);
        } else if (!is128Checked) {
            subnets.Start = page.Start = page.End.add(BigInteger.ONE.shiftLeft(64));
        }

        subnets.subnetslash = StartEnd.subnetslash;
        subnets.upto = upto;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;
        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;

        this.jList1.setModel(new DefaultListModel<String>());

        subnets = v6ST.ListSubRangePageForward(subnets, is128Checked, this.selectedRange);

        if (is128Checked) {
            page.End = subnets.Start.subtract(BigInteger.ONE);
        } else if (!is128Checked) {
            page.End = subnets.Start.subtract(BigInteger.ONE.shiftLeft(64));
        }

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        if (subnets.subnetidx.equals(NumberOfSubnets.subtract(BigInteger.ONE))) {
            this.jButtonListBack.setEnabled(true);
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);

            UpdateCount();
            return;
        } else {
            this.jButtonListBack.setEnabled(true);
            this.jButtonListLast.setEnabled(true);
        }
        UpdateCount();
    }//GEN-LAST:event_jButtonListForwardActionPerformed

    private void jButtonListLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListLastActionPerformed
        subnets.Start = page.End = StartEnd.End;
        subnets.subnetslash = StartEnd.subnetslash;
        subnets.upto = upto;
        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;

        this.jList1.setModel(new DefaultListModel<String>());

        subnets = v6ST.ListSubRangeLastPage(subnets, is128Checked, this.selectedRange);

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        if (is128Checked) {
            page.Start = subnets.Start.add(BigInteger.ONE);
        } else if (!is128Checked) {
            page.Start = subnets.Start.add(BigInteger.ONE.shiftLeft(64));
        }

        if (NumberOfSubnets.compareTo(BigInteger.valueOf(upto)) > 0) {
            this.jButtonListBack.setEnabled(true);
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);
        } else {
            this.jButtonListBack.setEnabled(false);
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);
        }

        UpdateCount();
    }//GEN-LAST:event_jButtonListLastActionPerformed

    private void jButtonGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGoActionPerformed
        this.jTextFieldGoto.setText(this.jTextFieldGoto.getText().trim());

        if (this.jTextFieldGoto.getText().equals("")) {
            this.jTextFieldGoto.setText("0");
            return;
        }

        gotovalue = new BigInteger(this.jTextFieldGoto.getText(), 10);

        if (gotovalue.compareTo(NumberOfSubnets.subtract(BigInteger.ONE)) > 0) {
            UpdateCount();
            this.jTextFieldGoto.setText(String.valueOf(NumberOfSubnets.subtract(BigInteger.ONE)));
            return;
        }

        String ss = "";
        int count = 0;

        ArrayList<String> liste = new ArrayList<String>();

        subnets.slash = StartEnd.slash;
        subnets.subnetslash = StartEnd.subnetslash;
        subnets.Start = StartEnd.Start;

        if (is128Checked) {
            subnets.Start = subnets.Start.add(gotovalue);
        } else if (!is128Checked) {
            subnets.Start = subnets.Start.add(gotovalue.shiftLeft(64));
        }

        if (subnets.Start.equals(StartEnd.Start)) {
            this.jButtonListBack.setEnabled(false);
        } else {
            this.jButtonListBack.setEnabled(true);
        }

        page.Start = subnets.Start;
        page.End = BigInteger.ZERO;

        this.jList1.setModel(new DefaultListModel<String>());

        for (count = 0; count < upto; count++) {
            subnets = v6ST.RangeIndex(subnets, is128Checked);
            if (is128Checked) {
                ss = v6ST.Kolonlar(subnets.Start);
                ss = v6ST.CompressAddress(ss);
                ss = "p" + subnets.subnetidx + "> " + ss + "/128";
            } else if (!is128Checked) {
                ss = v6ST.Kolonlar(subnets.Start);
                ss = ss.substring(0, 19);
                ss += "::";
                ss = v6ST.CompressAddress(ss);
                ss = "p" + subnets.subnetidx + "> " + ss + "/64";
            }
            liste.add(ss);

            if (subnets.subnetidx.equals(NumberOfSubnets.subtract(BigInteger.ONE))
                    || subnets.Start.equals(StartEnd.End)) {
                break;
            } else {
                if (is128Checked) {
                    subnets.Start = subnets.Start.add(BigInteger.ONE);
                } else if (!is128Checked) {
                    subnets.Start = subnets.Start.add(BigInteger.ONE.shiftLeft(64));
                }
            }
        }
        if (is128Checked) {
            page.End = subnets.Start.subtract(BigInteger.ONE);
        } else if (!is128Checked) {
            page.End = subnets.Start.subtract(BigInteger.ONE.shiftLeft(64));
        }

        this.jList1.setListData(liste.toArray(String[]::new));

        if (count > (upto - 1)) {
            this.jButtonListForward.setEnabled(true);
            this.jButtonListLast.setEnabled(true);
        } else {
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);
        }
        UpdateCount();
    }//GEN-LAST:event_jButtonGoActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
    }//GEN-LAST:event_formWindowClosing

    private void contextSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextSelectAllActionPerformed
        int s = 0;
        int e = jList1.getModel().getSize() - 1;
        if (e >= 0) {
            jList1.setSelectionInterval(s, e);
        }
    }//GEN-LAST:event_contextSelectAllActionPerformed

    private void contextCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextCopyActionPerformed

        this.jList1.getTransferHandler().exportToClipboard(this.jList1, this.clipboard, TransferHandler.COPY);
    }//GEN-LAST:event_contextCopyActionPerformed

    private void contextListDNSRevsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextListDNSRevsActionPerformed

        if (this.jList1.getModel().getSize() > 0) {
            ListDnsReverses dnsr = new ListDnsReverses(StartEnd, this.is128Checked);
            dnsr.SetNewValues(StartEnd, this.is128Checked);
            dnsr.setVisible(true);
        }
    }//GEN-LAST:event_contextListDNSRevsActionPerformed

    private void contextDBSendPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextDBSendPrefixActionPerformed

        IPv6SubnetCalculator.UpdateDbStatus();
        if (MySQLconnection != null) {
            if (this.jList1.getSelectedValue() != null) {
                String selected = this.jList1.getSelectedValue().split(" ")[1].trim();
                String snet = selected.split("/")[0].trim();
                short plen = Short.parseShort(selected.split("/")[1].trim());

                dbUIsend = new DatabaseUI(snet, plen, parentpflen, MySQLconnection, dbserverInfo);
                IPv6SubnetCalculator.dbUI.add(dbUIsend);
                dbUIsend.setVisible(true);
                dbUIsend.jLabeldbstatus.setText("db=Up ");
            }
        }
    }//GEN-LAST:event_contextDBSendPrefixActionPerformed

    private void contextDBGetPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextDBGetPrefixActionPerformed

        IPv6SubnetCalculator.UpdateDbStatus();
        if (MySQLconnection != null) {
            if (this.jList1.getSelectedValue() != null) {
                String prefix = this.jList1.getSelectedValue().split(" ")[1];
                GetPrefixInfoFromDB getprefixinfofromdb = new GetPrefixInfoFromDB(prefix, MySQLconnection, dbserverInfo);
                IPv6SubnetCalculator.getPrefixInfo.add(getprefixinfofromdb);
                getprefixinfofromdb.setVisible(true);
            }
        }
    }//GEN-LAST:event_contextDBGetPrefixActionPerformed

    private void contextSaveAsTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextSaveAsTextActionPerformed

        if (this.jList1.getModel().getSize() > 0) {
            SaveAsTxt saveTxt = new SaveAsTxt(StartEnd, this.is128Checked, this.selectedRange);
            saveTxt.setVisible(true);
        }
    }//GEN-LAST:event_contextSaveAsTextActionPerformed

    private void jPopupMenuListSubnetRangePopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenuListSubnetRangePopupMenuWillBecomeVisible

        if (MySQLconnection != null) {
            if (this.jList1.getModel().getSize() > 0) {
                if (this.jList1.getSelectedValue() != null) {
                    this.contextDBGetPrefix.setEnabled(true);
                    this.contextDBSendPrefix.setEnabled(true);
                } else {
                    this.contextDBGetPrefix.setEnabled(false);
                    this.contextDBSendPrefix.setEnabled(false);
                }
            }
        }
    }//GEN-LAST:event_jPopupMenuListSubnetRangePopupMenuWillBecomeVisible

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        this.lastPos = new java.awt.Point(evt.getX(), evt.getY());
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if (SwingUtilities.isRightMouseButton(evt)) {
            this.setLocation(this.getLocation().x + evt.getX() - this.lastPos.x,
                    this.getLocation().y + evt.getY() - this.lastPos.y);
        }
    }//GEN-LAST:event_formMouseDragged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem contextCopy;
    private javax.swing.JMenuItem contextDBGetPrefix;
    private javax.swing.JMenuItem contextDBSendPrefix;
    private javax.swing.JMenuItem contextListDNSRevs;
    private javax.swing.JMenuItem contextSaveAsText;
    private javax.swing.JMenuItem contextSelectAll;
    private javax.swing.JButton jButtonGo;
    private javax.swing.JButton jButtonListBack;
    private javax.swing.JButton jButtonListFirstPage;
    private javax.swing.JButton jButtonListForward;
    private javax.swing.JButton jButtonListLast;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelEnd;
    private javax.swing.JLabel jLabelListCount;
    private javax.swing.JLabel jLabelStart;
    javax.swing.JLabel jLabeldbstatus;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenuListSubnetRange;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTextField jTextFieldGoto;
    private javax.swing.JTextField jTextFieldTotal;
    // End of variables declaration//GEN-END:variables
}
