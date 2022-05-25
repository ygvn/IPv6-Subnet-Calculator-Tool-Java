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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 *
 * @author Yucel Guven
 */
public final class ListDnsReverses extends javax.swing.JFrame {

    //<editor-fold defaultstate="collapsed" desc="special initials/constants -Yücel">
    public final int ID = 2; // ID of this Form.
    public int incomingID;
    SEaddress StartEnd = new SEaddress();
    SEaddress subnets = new SEaddress();
    SEaddress page = new SEaddress();
    public int upto = 128;
    public final String arpa = "ip6.arpa.";
    BigInteger NumberOfZones = BigInteger.ZERO;
    public BigInteger zmaxval = BigInteger.ZERO;
    public Boolean is128Checked;
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    SaveAsTxt saveas = null;
    private java.awt.Point lastPos = new java.awt.Point();
    //</editor-fold>

    /**
     * Creates new form ListDnsReverses
     *
     * @param input
     * @param is128Checked
     */
    public ListDnsReverses(SEaddress input, Boolean is128Checked) {
        initComponents();
        //
        this.StartEnd.ID = ID;
        this.is128Checked = is128Checked;
        //
        SetNewValues(input, is128Checked);

        IPv6SubnetCalculator.AddWindowItem(this.getTitle(), (JFrame) SwingUtilities.getRoot(this), this.hashCode());

        EscapeKey();
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

    public void SetNewValues(SEaddress input, Boolean is128Checked) {

        this.jList1.setModel(new DefaultListModel<String>());

        this.jTextFieldTotal.setText("");
        this.jTextFieldGoto.setText("");
        this.incomingID = input.ID;
        this.is128Checked = is128Checked;
        this.jButtonListBack.setEnabled(false);
        this.jButtonListForward.setEnabled(false);
        this.jButtonListLast.setEnabled(false);
        //
        if (input.subnetslash % 4 != 0) {
            this.jLabelNibble.setVisible(true);
        } else {
            this.jLabelNibble.setVisible(false);
        }
        //
        StartEnd.Start = input.Start;
        StartEnd.End = input.End;
        StartEnd.Resultv6 = input.Resultv6;
        StartEnd.LowerLimitAddress = input.LowerLimitAddress;
        StartEnd.UpperLimitAddress = input.UpperLimitAddress;
        StartEnd.upto = upto;
        StartEnd.slash = input.slash;
        StartEnd.subnetslash = input.subnetslash;
        StartEnd.subnetidx = input.subnetidx;

        subnets.Start = input.Start;
        subnets.End = input.End;
        subnets.slash = input.slash;
        subnets.subnetslash = input.subnetslash;
        subnets.LowerLimitAddress = input.LowerLimitAddress;
        subnets.UpperLimitAddress = input.UpperLimitAddress;

        BigInteger max = NumberOfZones
                = BigInteger.ONE.shiftLeft(input.subnetslash - input.slash);
        zmaxval = max.subtract(BigInteger.ONE);
        this.jTextFieldTotal.setText(String.valueOf(NumberOfZones));
        if (!is128Checked) {
            //DefaultStage();
            String s = v6ST.Kolonlar(StartEnd.Start);
            s = s.substring(0, 19) + "::";
            s = v6ST.CompressAddress(s);
            this.jLabelStart.setText("s> " + s + "/" + String.valueOf(StartEnd.subnetslash));
            //
            s = v6ST.Kolonlar(StartEnd.End);
            s = s.substring(0, 19) + "::";
            s = v6ST.CompressAddress(s);
            this.jLabelEnd.setText("e> " + s + "/" + String.valueOf(StartEnd.subnetslash));
        } else if (is128Checked) {
            //ExpandStage();
            String s = v6ST.CompressAddress(v6ST.Kolonlar(StartEnd.Start));
            this.jLabelStart.setText("s> " + s + "/" + String.valueOf(StartEnd.subnetslash));
            s = v6ST.CompressAddress(v6ST.Kolonlar(StartEnd.End));
            this.jLabelEnd.setText("e> " + s + "/" + String.valueOf(StartEnd.subnetslash));
        }
        this.jButtonListFirstPageActionPerformed(null);
    }

    public void UpdateCount() {
        if (this.jList1.getModel().getSize() == 0) {
            this.jLabelListCount.setVisible(false);
            return;
        } else {
            if (StartEnd.subnetslash % 4 == 0) {
                this.jLabelListCount.setText("[" + String.valueOf(this.jList1.getModel().getSize()) + "]");
            } else {
                this.jLabelListCount.setVisible(true);
                int remainder = StartEnd.subnetslash % 4;
                int nzones = (1 << (4 - remainder));
                this.jLabelListCount.setText("[" + String.valueOf(this.jList1.getModel().getSize() / nzones) + "]");
            }
        }
    }

    public void DefaultStage() {
        /*
        stage.setWidth(425);
        stage.setHeight(450);
        prefixlist.setPrefWidth(400);
        prefixlist.setPrefHeight(260);
        hblistcount.setPrefWidth(173);
         */
    }

    public void ExpandStage() {
        /*
        stage.setWidth(625);
        stage.setHeight(450);
        prefixlist.setPrefWidth(600);
        prefixlist.setPrefHeight(260);
        hblistcount.setPrefWidth(373);
         */
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuListDNSRevs = new javax.swing.JPopupMenu();
        contextSelectAll = new javax.swing.JMenuItem();
        contextCopy = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        contextSaveAsText = new javax.swing.JMenuItem();
        jButtonListFirstPage = new javax.swing.JButton();
        jButtonGo = new javax.swing.JButton();
        jButtonListBack = new javax.swing.JButton();
        jButtonListForward = new javax.swing.JButton();
        jButtonListLast = new javax.swing.JButton();
        jLabelListCount = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabelRange = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabelStart = new javax.swing.JLabel();
        jTextFieldTotal = new javax.swing.JTextField();
        jLabelEnd = new javax.swing.JLabel();
        jTextFieldGoto = new javax.swing.JTextField();
        jLabelNibble = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();

        contextSelectAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        contextSelectAll.setText("Select All");
        contextSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextSelectAllActionPerformed(evt);
            }
        });
        jPopupMenuListDNSRevs.add(contextSelectAll);

        contextCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        contextCopy.setText("Copy");
        contextCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextCopyActionPerformed(evt);
            }
        });
        jPopupMenuListDNSRevs.add(contextCopy);
        jPopupMenuListDNSRevs.add(jSeparator1);

        contextSaveAsText.setText("Save As Text...");
        contextSaveAsText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextSaveAsTextActionPerformed(evt);
            }
        });
        jPopupMenuListDNSRevs.add(contextSaveAsText);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("List Dns Reverses");
        setName("ListDnsReverses"); // NOI18N
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

        jButtonListFirstPage.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jButtonListFirstPage.setText("|< FirstPage");
        jButtonListFirstPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonListFirstPageActionPerformed(evt);
            }
        });

        jButtonGo.setText("Go");
        jButtonGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGoActionPerformed(evt);
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

        jLabelListCount.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabelListCount.setForeground(new java.awt.Color(9, 160, 254));
        jLabelListCount.setText("[ ]");

        jLabel4.setText("Total Zone Groups:");

        jLabelRange.setText("Range:");

        jLabel5.setText("Goto Zone Group:");

        jLabelStart.setText("s>");

        jTextFieldTotal.setEditable(false);
        jTextFieldTotal.setText("0");

        jLabelEnd.setText("e>");

        jTextFieldGoto.setText("0");

        jLabelNibble.setForeground(new java.awt.Color(255, 0, 0));
        jLabelNibble.setText("Non-Nibble boundary");

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setComponentPopupMenu(jPopupMenuListDNSRevs);
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButtonListFirstPage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonListBack)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonListForward)
                        .addGap(12, 12, 12)
                        .addComponent(jButtonListLast)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelNibble)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelListCount))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldGoto, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonGo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextFieldTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(jLabelRange)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelEnd)
                        .addComponent(jLabelStart))
                    .addContainerGap(480, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelListCount)
                            .addComponent(jLabelNibble)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonListFirstPage)
                            .addComponent(jButtonListBack)
                            .addComponent(jButtonListForward)
                            .addComponent(jButtonListLast))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldGoto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonGo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(17, 17, 17)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelRange)
                        .addComponent(jLabelStart))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabelEnd)
                    .addContainerGap(431, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonListFirstPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListFirstPageActionPerformed
        subnets.Start = page.Start = StartEnd.Start;
        subnets.End = page.End = BigInteger.ZERO;
        subnets.subnetidx = BigInteger.ZERO;
        subnets.slash = StartEnd.slash;
        subnets.subnetslash = StartEnd.subnetslash;

        subnets.upto = upto;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;
        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;

        if (subnets.End.equals(StartEnd.End)) {
            UpdateCount();
            return;
        }

        this.jList1.setModel(new DefaultListModel<String>());

        if ((!is128Checked && StartEnd.slash == 64)
                || (is128Checked && StartEnd.slash == 128)) {

            subnets.Start = StartEnd.Resultv6;
            String[] sa = v6ST.DnsRev(subnets.Start, subnets.subnetslash, is128Checked);

            DefaultListModel<String> model = new DefaultListModel<String>();
            this.jList1.setModel(model);
            model.addElement("p0> " + sa[0]);
            UpdateCount();
            return;
        }

        subnets.liste.clear();

        subnets = v6ST.ListDnsRevFirstPage(subnets, is128Checked);
        this.jList1.setListData(subnets.liste.toArray(String[]::new));
        page.End = subnets.End;

        if (NumberOfZones.compareTo(BigInteger.valueOf(upto)) <= 0) {
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

    private void jButtonGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGoActionPerformed
        String[] sa;
        ArrayList<String> liste = new ArrayList<String>();

        int count = 0;
        int spaces = 0;

        if (this.jTextFieldGoto.getText().trim().equals("")) {
            return;
        }

        BigInteger newidx = new BigInteger(this.jTextFieldGoto.getText().trim());
        if (newidx.compareTo(zmaxval) > 0) {
            this.jTextFieldGoto.setText(String.valueOf(zmaxval));
            return;
        } else if (zmaxval.equals(BigInteger.ZERO)) {
            return;
        }

        subnets.subnetidx = newidx;
        subnets.slash = StartEnd.slash;
        subnets.subnetslash = StartEnd.subnetslash;
        subnets.Start = StartEnd.Start;
        subnets.Resultv6 = StartEnd.Resultv6;

        subnets = v6ST.GoToSubnet(subnets, is128Checked);

        page.Start = subnets.Start;
        page.End = BigInteger.ZERO;

        if (subnets.End.equals(StartEnd.End)) {
            this.jButtonListForward.setEnabled(false);
        }

        this.jList1.setModel(new DefaultListModel<String>());

        for (count = 0; count < upto; count++) {
            subnets = v6ST.Subnetting(subnets, is128Checked);

            sa = v6ST.DnsRev(subnets.Start, subnets.subnetslash, is128Checked);
            String sf = "p" + subnets.subnetidx + "> " + sa[0];
            liste.add(sf);

            String[] sr = sf.split(" ");
            spaces = sr[0].length() + 1;

            for (int i = 1; i < 8; i++) {
                if (sa[i] == null) {
                    break;
                }
                sa[i] = String.format("%1$" 
                        + Integer.valueOf(sa[i].length() + spaces) + "s", sa[i]);
                
                liste.add(sa[i]);
            }

            if (subnets.End.equals(StartEnd.End)) {
                this.jButtonListForward.setEnabled(false);
                break;
            } else {
                if (is128Checked) {
                    subnets.Start = subnets.End.add(BigInteger.ONE);
                } else if (!is128Checked) {
                    subnets.Start = subnets.End.add(BigInteger.ONE.shiftLeft(64));
                }
            }
        }
        this.jList1.setListData(liste.toArray(String[]::new));

        page.End = subnets.End;
        if (newidx.equals(BigInteger.ZERO)) {
            this.jButtonListBack.setEnabled(false);
        } else {
            this.jButtonListBack.setEnabled(true);
        }
        if (subnets.subnetidx.equals(zmaxval)) {
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);
        } else {
            this.jButtonListForward.setEnabled(true);
            this.jButtonListLast.setEnabled(true);
        }
        UpdateCount();
    }//GEN-LAST:event_jButtonGoActionPerformed

    private void jButtonListBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListBackActionPerformed
        subnets.upto = upto;
        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;

        this.jList1.setModel(new DefaultListModel<String>());

        subnets.liste.clear();
        subnets.End = page.End = page.Start.subtract(BigInteger.ONE);
        subnets = v6ST.ListDnsRevPageBackward(subnets, is128Checked);
        page.Start = subnets.Start;

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        if (subnets.Start.equals(StartEnd.Start)) {
            this.jButtonListBack.setEnabled(false);
            this.jButtonListForward.setEnabled(true);
            this.jButtonListLast.setEnabled(true);

            UpdateCount();
            return;
        } else {
            this.jButtonListForward.setEnabled(true);
            this.jButtonListLast.setEnabled(true);
        }
        UpdateCount();
    }//GEN-LAST:event_jButtonListBackActionPerformed

    private void jButtonListForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListForwardActionPerformed
        subnets.upto = upto;
        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;

        subnets.Start = page.Start = page.End.add(BigInteger.ONE);

        this.jList1.setModel(new DefaultListModel<String>());

        subnets.liste.clear();
        subnets = v6ST.ListDnsRevPageForward(subnets, is128Checked);
        page.End = subnets.End;

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        if (subnets.End.equals(StartEnd.End)) {
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
        subnets.upto = upto;
        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;

        this.jList1.setModel(new DefaultListModel<String>());

        subnets.liste.clear();
        subnets.subnetidx = BigInteger.ZERO;
        subnets.End = page.End = StartEnd.End;
        subnets = v6ST.ListDnsRevLastPage(subnets, is128Checked);
        page.Start = subnets.Start;

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        if (subnets.subnetidx.equals(BigInteger.ZERO)) {
            this.jButtonListBack.setEnabled(false);
        } else {
            this.jButtonListBack.setEnabled(true);
        }

        this.jButtonListForward.setEnabled(false);
        this.jButtonListLast.setEnabled(false);
        UpdateCount();
    }//GEN-LAST:event_jButtonListLastActionPerformed

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

    private void contextSaveAsTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextSaveAsTextActionPerformed
        if (this.jList1.getModel().getSize() > 0) {
            SaveAsTxt saveTxt = new SaveAsTxt(StartEnd, this.is128Checked, false);
            saveTxt.setVisible(true);
        }
    }//GEN-LAST:event_contextSaveAsTextActionPerformed

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
    private javax.swing.JMenuItem contextSaveAsText;
    private javax.swing.JMenuItem contextSelectAll;
    private javax.swing.JButton jButtonGo;
    private javax.swing.JButton jButtonListBack;
    private javax.swing.JButton jButtonListFirstPage;
    private javax.swing.JButton jButtonListForward;
    private javax.swing.JButton jButtonListLast;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelEnd;
    private javax.swing.JLabel jLabelListCount;
    private javax.swing.JLabel jLabelNibble;
    private javax.swing.JLabel jLabelRange;
    private javax.swing.JLabel jLabelStart;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPopupMenu jPopupMenuListDNSRevs;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTextField jTextFieldGoto;
    private javax.swing.JTextField jTextFieldTotal;
    // End of variables declaration//GEN-END:variables
}
