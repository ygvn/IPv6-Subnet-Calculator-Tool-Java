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
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 *
 * @author Yucel Guven
 */
public final class DatabaseUI extends javax.swing.JFrame {

    //<editor-fold defaultstate="collapsed" desc="special initials/constants -yucel">
    String[] statustypes = {"ASSIGNED", "ALLOCATED", "RESERVED"};

    public ArrayList<String> liste = new ArrayList<String>();

    public String prefix = null;
    public short pflen = 0;
    public short parentpflen = 0;
    public String selectedparentpflen = "";
    public String parentNet = "";
    NetInfo netinfo = new NetInfo();

    public static Connection MySQLconnection;
    DBServerInfo dbserverInfo;
    Statement statement = null;
    ResultSet resultSet = null;
    private final int RecordDisplayLimit = 100;

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private java.awt.Point lastPos = new java.awt.Point();

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
    
    public String[] CheckAll(short n) {
        String[] sa = new String[2];
        String qprefix = "", qpflen = "";

        this.jList1.setModel(new DefaultListModel<String>());

        this.jTextFieldPrefix.setText(this.jTextFieldPrefix.getText().trim());
        this.netinfo.netname = this.jTextFieldNetname.getText().trim();
        this.jTextFieldNetname.setText(netinfo.netname);
        this.netinfo.person = this.jTextFieldPerson.getText().trim();
        this.jTextFieldPerson.setText(netinfo.person);
        this.netinfo.organization = this.jTextFieldOrg.getText().trim();
        this.jTextFieldOrg.setText(netinfo.organization);
        this.netinfo.phone = this.jTextFieldPhone.getText().trim();
        this.jTextFieldPhone.setText(netinfo.phone);
        this.netinfo.email = this.jTextFieldEmail.getText().trim();
        this.jTextFieldEmail.setText(netinfo.email);

        this.jLabelcount.setText("[ ]");
        this.netinfo.status = this.jComboBox1.getSelectedItem().toString();
        //
        if (!this.jTextFieldPrefix.getText().equals("")) {
            int k = 0;
            char[] ca = this.jTextFieldPrefix.getText().toCharArray();
            for (int i = 0; i < this.jTextFieldPrefix.getText().length(); i++) {
                if (ca[i] == '/') {
                    k++;
                }
            }
            if (k != 1) {
                this.jTextFieldPrefix.requestFocus();
                return null;
            }

            qprefix = this.jTextFieldPrefix.getText().split("/")[0].trim();

            if (!v6ST.IsAddressCorrect(qprefix)) {
                this.jTextFieldPrefix.requestFocus();
                return null;
            }
            qprefix = v6ST.CompressAddress(qprefix);
            if (qprefix.equals("::")) {
                this.jTextFieldPrefix.setText(qprefix + "/" + qpflen);
                this.jTextFieldPrefix.requestFocus();
                return null;
            }

            qpflen = this.jTextFieldPrefix.getText().split("/")[1].trim();

            try {
                short ui = Short.parseShort(qpflen);
                if (ui > 128) {
                    this.jTextFieldPrefix.requestFocus();
                    return null;
                }
            } catch (NumberFormatException ex) {
                MsgBox.Show(this, ex.toString(), "Error:");
                this.jTextFieldPrefix.requestFocus();
                return null;
            }

            this.jTextFieldAsplain.setText(this.jTextFieldAsplain.getText().trim());
            if (!this.jTextFieldAsplain.getText().equals("")) {
                try {
                    this.netinfo.asnum = Long.parseLong(this.jTextFieldAsplain.getText());
                    if (this.netinfo.asnum > 4294967295.) {
                        this.jTextFieldAsplain.requestFocus();
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    MsgBox.Show(this, ex.toString(), "Error:");
                    this.jTextFieldAsplain.requestFocus();
                    return null;
                }
            }
        }
        //btn2 (update/insert)
        if (n == 2) {
            if (qprefix.equals("")
                    || netinfo.netname.equals("")
                    || netinfo.person.equals("")) {
                if (qprefix.equals("")) {
                    this.jTextFieldPrefix.setText("");
                    this.jTextFieldPrefix.requestFocus();
                    return null;
                }
                if (netinfo.netname.equals("")) {
                    this.jTextFieldNetname.setText("");
                    this.jTextFieldNetname.requestFocus();
                    return null;
                }
                if (netinfo.person.equals("")) {
                    this.jTextFieldPerson.setText("");
                    this.jTextFieldPerson.requestFocus();
                    return null;
                }
            }
        }
        //
        //btn3: delete ---REMOVED--NOT-IN-USE
        // DeleteButton has its own handler.
        //if (n == 3) {
        //    if (qprefix.equals("")) {
        //        this.jTextFieldPrefix.setText("");
        //        this.jTextFieldPrefix.requestFocus();
        //        return null;
        //    }
        //}

        if (!qprefix.equals("")) {
            this.jTextFieldPrefix.setText(qprefix + "/" + qpflen);
            sa[0] = qprefix;
            sa[1] = qpflen;
        }

        return sa;
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

    public void GetSelected() {
        if ((this.jList1.getSelectedIndex() % 12 == 0) || (this.jList1.getSelectedIndex() % 12 == 1)) {

            String s = this.jList1.getSelectedValue().split(" ")[1];
            String[] sa = s.split("/");

            String scmd = "";
            if (this.jList1.getSelectedIndex() % 12 == 0) {  // clicked on prefix:

                this.selectedparentpflen = this.jList1.getModel().getElementAt(this.jList1.getSelectedIndex() + 1);
                this.selectedparentpflen = this.selectedparentpflen.split(" ")[1].split("/")[1];

                this.jLabelSelectedParent.setText("/" + this.selectedparentpflen);
                scmd = " AND pflen=" + sa[1] + " AND parentpflen=" + this.selectedparentpflen;

            }
            if (this.jList1.getSelectedIndex() % 12 == 1) {  // clicked on parentNet:
                String pfx = this.jList1.getModel().getElementAt(this.jList1.getSelectedIndex() - 1);
                pfx = pfx.split(" ")[1].split("/")[1];

                if (pfx.equals(sa[1])) {
                    this.selectedparentpflen = sa[1];
                    this.jLabelSelectedParent.setText("/" + this.selectedparentpflen);
                    scmd = " AND pflen=" + sa[1] + " AND parentpflen=" + sa[1];
                } else {
                    this.selectedparentpflen = "";
                    this.jLabelSelectedParent.setText("No parent - using initial /" + this.parentpflen);
                    scmd = " AND pflen=" + sa[1]; // Normal Query. Yeni prefix gibi bakmaliyiz.Cunku onun altindakileri henuz bilmiyoruz.
                }
            }

            String MySQLcmd = "SELECT "
                    + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, organization, `as-num`, phone, email, status, created, `last-updated` FROM "
                    + "`" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                    + " WHERE ("
                    + " INET6_NTOA(prefix)='" + sa[0] + "' "
                    + scmd
                    + " ) LIMIT " + this.RecordDisplayLimit;

            if (sa[0] != null && sa[1] != null) {
                try {
                    IPv6SubnetCalculator.UpdateDbStatus();
                    if (MySQLconnection != null) {
                        statement = MySQLconnection.createStatement();
                        resultSet = statement.executeQuery(MySQLcmd);
                        liste.clear();

                        Boolean isFirstEntryDisplayed = false;

                        while (resultSet.next()) {
                            if (!isFirstEntryDisplayed && sa[0] != null) {
                                this.jTextFieldPrefix.setText(resultSet.getString(1) + "/" + resultSet.getString(2));
                                this.jTextFieldNetname.setText(resultSet.getString(4));
                                this.jTextFieldPerson.setText(resultSet.getString(5));
                                this.jTextFieldOrg.setText(resultSet.getString(6));
                                this.jTextFieldAsplain.setText(resultSet.getString(7));
                                this.jTextFieldPhone.setText(resultSet.getString(8));
                                this.jTextFieldEmail.setText(resultSet.getString(9));
                                this.jComboBox1.setSelectedItem(resultSet.getString(10));
                                isFirstEntryDisplayed = true;
                                if (this.selectedparentpflen.equals("")) {
                                    this.selectedparentpflen = resultSet.getString(3);
                                    this.jLabelSelectedParent.setText("/" + this.selectedparentpflen);
                                }
                            }
                            //
                            liste.add("prefix:\t\t " + resultSet.getString(1) + "/" + resultSet.getString(2));

                            if (isParentNetinDB(resultSet.getString(1), Short.valueOf(resultSet.getString(3)), true)) {
                                liste.add("parent:\t " + this.parentNet);
                            } else {
                                String sp = this.parentNet.split("/")[1];
                                liste.add("parent:\t " + this.parentNet + " (/" + sp + "-" + sp + " *Not_in_DB*)");
                            }

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

                        this.jLabelcount.setText("[" + String.valueOf(liste.size() / 12) + "]");

                        if (!liste.isEmpty()) {
                            this.jLabelstatus.setText(" [ Record Selected ]");
                        } else {
                            this.jLabelstatus.setText(" [ Record Not Found ]");
                        }
                    }
                } catch (Exception ex) {
                    MsgBox.Show(this, ex.toString(), "Exception:");
                    IPv6SubnetCalculator.UpdateDbStatus();
                }
            }
        }
    }

    public void setConnection(Connection con) {
        MySQLconnection = con;
    }

    /**
     * Creates new form DBConnectInfo
     *
     * @param inprefix
     * @param inpflen
     * @param inparentpflen
     * @param sqlcon
     * @param dbsrvInfo
     */
    public DatabaseUI(String inprefix, short inpflen, short inparentpflen,
            Connection sqlcon, DBServerInfo dbsrvInfo) {
        initComponents();
        //
        EscapeKey();
        IPv6SubnetCalculator.AddWindowItem(this.getTitle(), (JFrame) SwingUtilities.getRoot(this), this.hashCode());

        this.jComboBox1.setModel(new DefaultComboBoxModel<>(statustypes));
        this.prefix = inprefix;
        this.pflen = inpflen;
        this.parentpflen = inparentpflen;

        DatabaseUI.MySQLconnection = sqlcon;
        this.dbserverInfo = dbsrvInfo;

        if (!this.prefix.trim().equals("")) {
            this.jLabelSelectedParent.setText("/" + String.valueOf(this.parentpflen));
            this.jTextFieldPrefix.setText(this.prefix + "/" + String.valueOf(this.pflen));
        }

        IPv6SubnetCalculator.UpdateDbStatus();
        jButtonQueryActionPerformed(null);
        this.jButtonQuery.requestFocus();
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
        jMenuItemModify = new javax.swing.JMenuItem();
        jMenuItemDelete = new javax.swing.JMenuItem();
        jMenuItemGetPrefixInfo = new javax.swing.JMenuItem();
        jLabelSelected = new javax.swing.JLabel();
        jLabelSelectedParent = new javax.swing.JLabel();
        jLabelPrefix = new javax.swing.JLabel();
        jLabelNetname = new javax.swing.JLabel();
        jLabelPerson = new javax.swing.JLabel();
        jLabelNetstatus = new javax.swing.JLabel();
        jTextFieldPrefix = new javax.swing.JTextField();
        jTextFieldNetname = new javax.swing.JTextField();
        jTextFieldPerson = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButtonClear = new javax.swing.JButton();
        jLabelOrg = new javax.swing.JLabel();
        jTextFieldOrg = new javax.swing.JTextField();
        jLabelasplain = new javax.swing.JLabel();
        jTextFieldAsplain = new javax.swing.JTextField();
        jLabelPhone = new javax.swing.JLabel();
        jTextFieldPhone = new javax.swing.JTextField();
        jLabelemail = new javax.swing.JLabel();
        jTextFieldEmail = new javax.swing.JTextField();
        jLabelcount = new javax.swing.JLabel();
        jButtonQuery = new javax.swing.JButton();
        jButtonInsertUpdate = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabeldbstatus = new javax.swing.JLabel();
        jLabelstatus = new javax.swing.JLabel();
        jLabelHelp = new javax.swing.JLabel();

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
        jMenuItemSelectAll.setText("Select All");
        jMenuItemSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSelectAllActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemSelectAll);

        jMenuItemCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemCopy.setText("Copy");
        jMenuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemCopy);

        jMenuItemModify.setMnemonic('M');
        jMenuItemModify.setText("Modify prefix");
        jMenuItemModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemModifyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemModify);

        jMenuItemDelete.setMnemonic('D');
        jMenuItemDelete.setText("Delete prefix");
        jMenuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemDelete);

        jMenuItemGetPrefixInfo.setMnemonic('G');
        jMenuItemGetPrefixInfo.setText("Get prefix info");
        jMenuItemGetPrefixInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGetPrefixInfoActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemGetPrefixInfo);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Database UI");
        setName("DatabaseUI"); // NOI18N
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

        jLabelSelected.setFont(new java.awt.Font("Cantarell", 1, 13)); // NOI18N
        jLabelSelected.setForeground(new java.awt.Color(9, 160, 254));
        jLabelSelected.setText("parent:");

        jLabelSelectedParent.setFont(new java.awt.Font("Cantarell", 1, 13)); // NOI18N
        jLabelSelectedParent.setForeground(new java.awt.Color(9, 160, 254));
        jLabelSelectedParent.setText("/");

        jLabelPrefix.setFont(new java.awt.Font("Cantarell", 3, 13)); // NOI18N
        jLabelPrefix.setText("prefix:");

        jLabelNetname.setFont(new java.awt.Font("Cantarell", 3, 13)); // NOI18N
        jLabelNetname.setText("netname:");

        jLabelPerson.setFont(new java.awt.Font("Cantarell", 3, 13)); // NOI18N
        jLabelPerson.setText("person:");

        jLabelNetstatus.setText("status:");

        jTextFieldPrefix.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldPrefixKeyPressed(evt);
            }
        });

        jTextFieldNetname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldNetnameKeyPressed(evt);
            }
        });

        jTextFieldPerson.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldPersonKeyPressed(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("Cantarell", 0, 13)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ASSIGNED", "ALLOCATED", "RESERVED" }));

        jButtonClear.setText("Clear All");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });

        jLabelOrg.setText("organization:");

        jLabelasplain.setText("asplain-num:");

        jLabelPhone.setText("phone:");

        jLabelemail.setText("e-mail:");

        jLabelcount.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabelcount.setForeground(new java.awt.Color(9, 160, 254));
        jLabelcount.setText("[0]");

        jButtonQuery.setText("Query");
        jButtonQuery.setToolTipText("Query is based on: \n prefix, netname, person or all.");
        jButtonQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQueryActionPerformed(evt);
            }
        });

        jButtonInsertUpdate.setText("Insert/Update");
        jButtonInsertUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInsertUpdateActionPerformed(evt);
            }
        });

        jButtonDelete.setText("Delete");
        jButtonDelete.setToolTipText("Delete is based on:  prefix");
        jButtonDelete.setEnabled(false);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jList1.setToolTipText("Database Output List");
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
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jLabeldbstatus.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabeldbstatus.setForeground(new java.awt.Color(9, 160, 254));
        jLabeldbstatus.setText("db=Down");

        jLabelstatus.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        jLabelstatus.setForeground(new java.awt.Color(9, 160, 254));
        jLabelstatus.setText(" ");

        jLabelHelp.setFont(new java.awt.Font("Cantarell", 1, 13)); // NOI18N
        jLabelHelp.setForeground(new java.awt.Color(9, 160, 254));
        jLabelHelp.setText("Help");
        jLabelHelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelHelpMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabelHelpMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelHelpMouseExited(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelOrg)
                            .addComponent(jLabelasplain)
                            .addComponent(jLabelPhone, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelNetstatus, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelPerson, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelNetname, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelemail, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelPrefix, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelcount, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelSelected, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonQuery, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonInsertUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jTextFieldPrefix)
                            .addComponent(jTextFieldNetname)
                            .addComponent(jTextFieldPerson)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextFieldOrg)
                            .addComponent(jTextFieldAsplain)
                            .addComponent(jTextFieldPhone)
                            .addComponent(jTextFieldEmail)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelSelectedParent, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabelHelp))
                            .addComponent(jScrollPane1)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabeldbstatus)))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSelected)
                    .addComponent(jLabelSelectedParent)
                    .addComponent(jLabelHelp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPrefix)
                    .addComponent(jTextFieldPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelNetname)
                    .addComponent(jTextFieldNetname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPerson))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClear)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelNetstatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldOrg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelOrg))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelasplain)
                    .addComponent(jTextFieldAsplain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPhone)
                    .addComponent(jTextFieldPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelemail)
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonQuery)
                    .addComponent(jButtonInsertUpdate)
                    .addComponent(jButtonDelete)
                    .addComponent(jButtonExit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelcount)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabeldbstatus)
                            .addComponent(jLabelstatus)))))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public Boolean isParentNetinDB(String prefix, short len, Boolean withParentLength) {
        Statement mystatement;
        ResultSet myresultSet;
        String MySQLcmd;
        Boolean Found = false;

        try {
            if (withParentLength) {

                String[] sa;
                this.parentNet = v6ST.FindParentNet(prefix, len, true);
                sa = this.parentNet.split("/");

                // is ParentPrefix in Database?:
                MySQLcmd = "SELECT inet6_ntoa(prefix), pflen, parentpflen FROM "
                        + "`" + this.dbserverInfo.DBname + "`." + "`" + this.dbserverInfo.Tablename + "`"
                        + " WHERE prefix=inet6_aton('" + sa[0] + "')"
                        + " AND pflen=" + sa[1] + " AND parentpflen=" + String.valueOf(len);

                mystatement = MySQLconnection.createStatement();
                myresultSet = mystatement.executeQuery(MySQLcmd);

                String s = "";

                if (myresultSet.next()) {
                    s = myresultSet.getString(2);
                    Found = true;

                } else {
                    return false;
                }

            } else {  // We don't have parentpflength. First get parentpflen of the input prefix from DB:

                MySQLcmd = "SELECT parentpflen FROM "
                        + "`" + this.dbserverInfo.DBname + "`." + "`" + this.dbserverInfo.Tablename + "`"
                        + " WHERE prefix = inet6_aton('" + prefix + "')"
                        + " AND pflen = " + pflen;

                mystatement = MySQLconnection.createStatement();
                myresultSet = mystatement.executeQuery(MySQLcmd);

                short ppflen = -1;

                if (myresultSet.next()) {
                    ppflen = Short.valueOf(myresultSet.getString(1));
                } else {
                    return false;
                }

                // with ppflen, find ParentNet using v6ST library:
                String[] sa;
                this.parentNet = v6ST.FindParentNet(prefix, ppflen, true);
                sa = this.parentNet.split("/");

                // is ParentPrefix in Database?:
                MySQLcmd = "SELECT inet6_ntoa(prefix), pflen, parentpflen FROM "
                        + "`" + this.dbserverInfo.DBname + "`." + "`" + this.dbserverInfo.Tablename + "`"
                        + " WHERE prefix=inet6_aton('" + sa[0] + "')"
                        + " AND pflen=" + sa[1] + " AND parentpflen=" + sa[1]; //String.valueOf(ppflen);

                mystatement = MySQLconnection.createStatement();
                myresultSet = mystatement.executeQuery(MySQLcmd);

                String s = "";

                if (myresultSet.next()) {
                    s = myresultSet.getString(2);
                    Found = true;

                } else {
                    return false;
                }
            }

        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error");
            return false;
        }

        return Found;
    }

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        this.jLabelSelectedParent.setText("");
        this.jTextFieldPrefix.setText("");
        this.jTextFieldNetname.setText("");
        this.jTextFieldPerson.setText("");
        this.jComboBox1.setSelectedIndex(0);
        this.jTextFieldOrg.setText("");
        this.jTextFieldAsplain.setText("");
        this.jTextFieldPhone.setText("");
        this.jTextFieldEmail.setText("");
        this.jList1.setModel(new DefaultListModel<String>());
        this.jLabelcount.setText("[ ]");
        this.jLabelstatus.setText("");
        this.selectedparentpflen = "";
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jButtonQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQueryActionPerformed
        String[] sa = CheckAll((short) 1);
        String MySQLcmd = "";
        String scmd = "";
        this.jLabelstatus.setText("");

        if (sa != null) {
            if (sa[0] != null) {
                String mycmd = "";

                if (this.selectedparentpflen.equals("")) {
                    mycmd = " AND pflen=" + sa[1] + " AND parentpflen=" + String.valueOf(this.parentpflen);
                } else {
                    mycmd = " AND pflen=" + sa[1] + " AND parentpflen=" + String.valueOf(this.selectedparentpflen);
                }

                MySQLcmd = "SELECT "
                        + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, organization, "
                        + "`as-num`, phone, email, status, created, `last-updated` FROM "
                        + "`" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                        + " WHERE ("
                        + " INET6_NTOA(prefix)='" + sa[0] + "' "
                        + mycmd
                        + " ) LIMIT " + this.RecordDisplayLimit;
            } else {
                if (netinfo.netname.equals("") && netinfo.person.equals("")) {
                    scmd = "";
                    return;
                } else if (!netinfo.netname.equals("") && netinfo.person.equals("")) {
                    scmd = " netname LIKE '%" + netinfo.netname + "%'";
                } else if (netinfo.netname.equals("") && !netinfo.person.equals("")) {
                    scmd = " person LIKE '%" + netinfo.person + "%'";
                } else if (!netinfo.netname.equals("") && !netinfo.person.equals("")) {
                    scmd = " netname LIKE '%" + netinfo.netname + "%'"
                            + " AND person LIKE '%" + netinfo.person + "%'";
                }

                MySQLcmd = "SELECT "
                        + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, organization, "
                        + "`as-num`, phone, email, status, created, `last-updated` FROM "
                        + "`" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                        + " WHERE ("
                        + scmd
                        + ") LIMIT " + this.RecordDisplayLimit;
            }
            try {
                IPv6SubnetCalculator.UpdateDbStatus();
                if (MySQLconnection != null) {
                    statement = MySQLconnection.createStatement();
                    resultSet = statement.executeQuery(MySQLcmd);
                    liste.clear();

                    Boolean isFirstEntryDisplayed = false;

                    while (resultSet.next()) {
                        liste.add("prefix:\t\t " + resultSet.getString(1) + "/" + resultSet.getString(2));

                        if (isParentNetinDB(resultSet.getString(1), Short.valueOf(resultSet.getString(3)), true)) {
                            liste.add("parent:\t " + this.parentNet);
                        } else {
                            String sp = this.parentNet.split("/")[1];
                            liste.add("parent:\t " + this.parentNet + " (/" + sp + "-" + sp + " *Not_in_DB*)");
                        }

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

                        if (!isFirstEntryDisplayed && sa[0] != null) {
                            this.jTextFieldPrefix.setText(resultSet.getString(1) + "/" + resultSet.getString(2));
                            this.jTextFieldNetname.setText(resultSet.getString(4));
                            this.jTextFieldPerson.setText(resultSet.getString(5));
                            this.jTextFieldOrg.setText(resultSet.getString(6));
                            this.jTextFieldAsplain.setText(resultSet.getString(7));
                            this.jTextFieldPhone.setText(resultSet.getString(8));
                            this.jTextFieldEmail.setText(resultSet.getString(9));
                            this.jComboBox1.setSelectedItem(resultSet.getString(10));

                            isFirstEntryDisplayed = true;
                        }
                    }

                    this.jList1.setListData(liste.toArray(String[]::new));
                    this.jLabelcount.setText("[" + String.valueOf(liste.size() / 12) + "]");

                    if (!liste.isEmpty()) {
                        this.jLabelstatus.setText(" Record display limit is " + this.RecordDisplayLimit);
                    } else {
                        this.jLabelstatus.setText(" Record Not Found ");
                    }
                }
            } catch (Exception ex) {
                MsgBox.Show(this, ex.toString(), "Error:");
                IPv6SubnetCalculator.UpdateDbStatus();
            }
        }
    }//GEN-LAST:event_jButtonQueryActionPerformed

    private void jButtonInsertUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInsertUpdateActionPerformed
        String[] sa = CheckAll((short) 2);
        int r = 0;
        this.jLabelstatus.setText("");

        if (sa != null && sa[0] != null && sa[1] != null) {
            try {
                // First, we automatically insert Symbolic-ParentPrefix.
                // It's just a symbolic record showing initial boundary of the selected range.
                // ParentPrefix is defined by value of 'jSliderPrefix'. -yucel
                
                Boolean b;
                if (this.selectedparentpflen.equals("")) {
                    b = isParentNetinDB(sa[0], parentpflen, true); //parentpflen is == Slider1value !
                } else {
                    b = isParentNetinDB(sa[0], Short.valueOf(this.selectedparentpflen), true);
                }

                String parent;
                if (this.selectedparentpflen.equals("")) {
                    parent = v6ST.FindParentNet(sa[0], parentpflen, true);
                } else {
                    parent = v6ST.FindParentNet(sa[0], Short.valueOf(this.selectedparentpflen), true);
                }

                if (!b) {   // Insert PARENT as /xx-xx (same xx values)
                    String[] saparent = parent.split("/");
                    String MySQLcmd = "INSERT INTO "
                            + "`" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                            + "(prefix, pflen, parentpflen, netname, person, organization, `as-num`, phone, email, status) "
                            + "VALUES( inet6_aton('" + saparent[0] + "'), " + saparent[1] + ", " + saparent[1] + ", "
                            + "'*SymbolicParent for the Range: /" + saparent[1] + "-" + sa[1] + " *', "
                            + "'*AUTO-GENERATED (can be updated)*', "
                            + "'" + netinfo.organization + "', "
                            + "'" + netinfo.asnum + "', "
                            + "'" + netinfo.phone + "', "
                            + "'" + netinfo.email + "', "
                            + "'" + netinfo.status + "')";

                    IPv6SubnetCalculator.UpdateDbStatus();
                    if (MySQLconnection != null) {
                        statement = MySQLconnection.createStatement();
                        r = statement.executeUpdate(MySQLcmd);
                    }
                }

                // Then, insert the input prefix:
                String ss = "";
                if (this.selectedparentpflen.equals("")) {
                    ss = String.valueOf(this.parentpflen);
                } else {
                    ss = this.selectedparentpflen;
                }

                String MySQLcmd = "INSERT INTO "
                        + "`" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                        + "(prefix, pflen, parentpflen, netname, person, organization, `as-num`, phone, email, status) "
                        + "VALUES( inet6_aton('" + sa[0] + "'), " + sa[1] + ", "
                        + ss + ", "
                        + "'" + netinfo.netname + "', "
                        + "'" + netinfo.person + "', "
                        + "'" + netinfo.organization + "', "
                        + "'" + netinfo.asnum + "', "
                        + "'" + netinfo.phone + "', "
                        + "'" + netinfo.email + "', "
                        + "'" + netinfo.status + "') "
                        + " ON DUPLICATE KEY UPDATE "
                        + " prefix=inet6_aton('" + sa[0] + "'), "
                        + " pflen=" + sa[1] + ", "
                        + " parentpflen=" + ss + ", "
                        + " netname='" + netinfo.netname + "', "
                        + " person='" + netinfo.person + "', "
                        + " organization='" + netinfo.organization + "', "
                        + " `as-num`='" + netinfo.asnum + "', "
                        + " phone='" + netinfo.phone + "', "
                        + " email='" + netinfo.email + "', "
                        + " status='" + netinfo.status + "';";

                IPv6SubnetCalculator.UpdateDbStatus();
                if (MySQLconnection != null) {

                    statement = MySQLconnection.createStatement();
                    r = statement.executeUpdate(MySQLcmd);

                    liste.clear();
                    MySQLcmd = "SELECT "
                            + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, "
                            + "organization, `as-num`, phone, email, status, created, `last-updated` FROM "
                            + "`" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                            + " WHERE "
                            + " (INET6_NTOA(prefix)='" + sa[0]
                            + "' AND pflen=" + sa[1]
                            + "  AND parentpflen=" + ss
                            + " ) LIMIT " + this.RecordDisplayLimit;

                    resultSet = statement.executeQuery(MySQLcmd);

                    Boolean isFirstEntryDisplayed = false;

                    while (resultSet.next()) {
                        liste.add("prefix:\t\t " + resultSet.getString(1) + "/" + resultSet.getString(2));

                        if (isParentNetinDB(resultSet.getString(1), Short.valueOf(resultSet.getString(3)), true)) {
                            liste.add("parent:\t " + this.parentNet);
                        } else {
                            String sp = this.parentNet.split("/")[1];
                            liste.add("parent:\t " + this.parentNet + " (/" + sp + "-" + sp + " *Not_in_DB*)");
                        }

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

                        if (!isFirstEntryDisplayed && sa[0] != null) {
                            this.jTextFieldPrefix.setText(resultSet.getString(1) + "/" + resultSet.getString(2));
                            this.jTextFieldNetname.setText(resultSet.getString(4));
                            this.jTextFieldPerson.setText(resultSet.getString(5));
                            this.jTextFieldOrg.setText(resultSet.getString(6));
                            this.jTextFieldAsplain.setText(resultSet.getString(7));
                            this.jTextFieldPhone.setText(resultSet.getString(8));
                            this.jTextFieldEmail.setText(resultSet.getString(9));
                            this.jComboBox1.setSelectedItem(resultSet.getString(10));

                            isFirstEntryDisplayed = true;
                            this.selectedparentpflen = resultSet.getString(3);
                            this.jLabelSelectedParent.setText("/" + this.selectedparentpflen);
                        }
                    }

                    this.jList1.setListData(liste.toArray(String[]::new));

                    this.jLabelcount.setText("[" + String.valueOf(liste.size() / 12) + "]");
                    this.jLabelstatus.setText(" [ Record inserted/updated ]");

                    MsgBox.Show(this, "Record inserted/updated", "Record inserted/updated");
                }

            } catch (Exception ex) {
                MsgBox.Show(this, ex.toString(), "Error:");
                IPv6SubnetCalculator.UpdateDbStatus();
            }
        } else {
            MsgBox.Show(getRootPane(), "Incorrect address or \r\nprefix, netname, and person can not be blank", "Incorrect or Blank values");
        }
    }//GEN-LAST:event_jButtonInsertUpdateActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        String MySQLcmd;
        String pfx = this.jList1.getSelectedValue().split(" ")[1];
        String[] sa = pfx.split("/");
        Statement mystatement;
        ResultSet myresultSet;

        try {
            MySQLcmd = "SELECT parentpflen FROM "
                    + "`" + this.dbserverInfo.DBname + "`." + "`" + this.dbserverInfo.Tablename + "`"
                    + " WHERE prefix = inet6_aton('" + sa[0] + "')"
                    + " AND pflen = " + sa[1]
                    + " ORDER BY parentpflen ASC";

            IPv6SubnetCalculator.UpdateDbStatus();
            if (MySQLconnection != null) {

                mystatement = MySQLconnection.createStatement();
                myresultSet = mystatement.executeQuery(MySQLcmd);

                short ppflen;

                if (myresultSet.next()) {
                    ppflen = Short.valueOf(myresultSet.getString(1));
                } else {
                    MsgBox.Show(this, "parentpflen(parent prefix length):\r\nNot found in database", "Exception:");
                    return;
                }

                MySQLcmd = "DELETE FROM "
                        + "`" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                        + " WHERE ("
                        + " prefix=inet6_aton('" + sa[0] + "') "
                        + " AND pflen=" + sa[1] + " AND parentpflen=" + ppflen
                        + ");";

                if (JOptionPane.showConfirmDialog(getRootPane(),
                        "Delete prefix?", "Deleting Prefix",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.OK_CANCEL_OPTION) == 0) {

                    statement = MySQLconnection.createStatement();
                    int c = statement.executeUpdate(MySQLcmd);
                    this.jLabelstatus.setText(" " + String.valueOf(c) + " Record Deleted");
                    MsgBox.Show(getRootPane(), "Record Deleted", "Record Deleted");
                    this.jButtonQueryActionPerformed(null);
                } else {
                    this.jLabelstatus.setText(" Delete cancelled");
                }
            }
        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Exception:");
            IPv6SubnetCalculator.UpdateDbStatus();
        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException ex) {
            MsgBox.Show(this, ex.toString(), "Error:");
        }
        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
        dispose();
    }//GEN-LAST:event_jButtonExitActionPerformed

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

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked

        if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
            int index = this.jList1.locationToIndex(evt.getPoint());
            if (index >= 0) {
                GetSelected();
            }
        }
    }//GEN-LAST:event_jList1MouseClicked

    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (this.jList1.getSelectedValue() != null) {
                GetSelected();
            }
        }
    }//GEN-LAST:event_jList1KeyPressed

    private void jTextFieldPrefixKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPrefixKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            this.jButtonQueryActionPerformed(null);
    }//GEN-LAST:event_jTextFieldPrefixKeyPressed

    private void jTextFieldNetnameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNetnameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            this.jButtonQueryActionPerformed(null);
    }//GEN-LAST:event_jTextFieldNetnameKeyPressed

    private void jTextFieldPersonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPersonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            this.jButtonQueryActionPerformed(null);
    }//GEN-LAST:event_jTextFieldPersonKeyPressed

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        this.lastPos = new java.awt.Point(evt.getX(), evt.getY());
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if (SwingUtilities.isRightMouseButton(evt)) {
            this.setLocation(this.getLocation().x + evt.getX() - this.lastPos.x,
                    this.getLocation().y + evt.getY() - this.lastPos.y);
        }
    }//GEN-LAST:event_formMouseDragged

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        if (this.jList1.getSelectedIndex() % 12 == 0) {
            this.jButtonDelete.setEnabled(true);
        } else {
            this.jButtonDelete.setEnabled(false);
        }
    }//GEN-LAST:event_jList1ValueChanged

    private void jMenuItemModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemModifyActionPerformed
        GetSelected();
    }//GEN-LAST:event_jMenuItemModifyActionPerformed

    private void jPopupMenu1PopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenu1PopupMenuWillBecomeVisible
        switch (this.jList1.getSelectedIndex() % 12) {
            case 0 -> {
                // prefix
                this.jMenuItemModify.setEnabled(true);
                this.jMenuItemDelete.setEnabled(true);
                this.jMenuItemGetPrefixInfo.setEnabled(false);
            }
            case 1 -> {
                // parentNet
                this.jMenuItemModify.setEnabled(true);
                this.jMenuItemDelete.setEnabled(false);
                this.jMenuItemGetPrefixInfo.setEnabled(true);
            }
            default -> {
                this.jMenuItemModify.setEnabled(false);
                this.jMenuItemDelete.setEnabled(false);
                this.jMenuItemGetPrefixInfo.setEnabled(false);
            }
        }
    }//GEN-LAST:event_jPopupMenu1PopupMenuWillBecomeVisible

    private void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteActionPerformed
        this.jButtonDeleteActionPerformed(null);
    }//GEN-LAST:event_jMenuItemDeleteActionPerformed

    private void jLabelHelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelHelpMouseClicked
        String helptxt = """
                         When you clicked on Query button, textfields will be
                         populated with the 'first' record of the listbox.
                                                  
                         [To modify a database record:]
                         Select the 'prefix:' from the listbox and Right-click on it,
                         then select Modify prefix from the popup menu.
                         You can also double-click (or press Enter)
                         on the selected 'prefix:' to modify it.
                         Textfields will be populated with selected prefix info.
                         Modify/fill the text fields and click on Insert/Update button.
                                                  
                         When you Insert/Update a database record,
                         "prefix, netname and person" fields can not be blank.
                         
                         [To delete a database record:]
                         Select the 'prefix:' from the listbox, Delete button will be enabled.
                         Either click on Delete button or Right-click for popup menu,
                         then select Delete prefix from the menu.
                         """;
        JOptionPane.showMessageDialog(this.getRootPane(), helptxt, "Help", 1);
    }//GEN-LAST:event_jLabelHelpMouseClicked

    private void jLabelHelpMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelHelpMouseEntered
        this.jLabelHelp.setText("Click for Help");
    }//GEN-LAST:event_jLabelHelpMouseEntered

    private void jLabelHelpMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelHelpMouseExited
        this.jLabelHelp.setText("Help");
    }//GEN-LAST:event_jLabelHelpMouseExited

    private void jMenuItemGetPrefixInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGetPrefixInfoActionPerformed
        if (this.jList1.getSelectedIndex() % 12 == 1) {
            IPv6SubnetCalculator.UpdateDbStatus();
            if (MySQLconnection != null) {
                if (!this.jList1.getSelectedValue().trim().equals("") && this.jList1.getSelectedValue() != null) {
                    String selected = this.jList1.getSelectedValue().split(" ")[1].trim();
                    GetPrefixInfoFromDB getpfxdbinfo = new GetPrefixInfoFromDB(selected, MySQLconnection, dbserverInfo);
                    IPv6SubnetCalculator.getPrefixInfo.add(getpfxdbinfo);
                    getpfxdbinfo.setVisible(true);
                }
            }
        }
    }//GEN-LAST:event_jMenuItemGetPrefixInfoActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonInsertUpdate;
    private javax.swing.JButton jButtonQuery;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabelHelp;
    private javax.swing.JLabel jLabelNetname;
    private javax.swing.JLabel jLabelNetstatus;
    private javax.swing.JLabel jLabelOrg;
    private javax.swing.JLabel jLabelPerson;
    private javax.swing.JLabel jLabelPhone;
    private javax.swing.JLabel jLabelPrefix;
    private javax.swing.JLabel jLabelSelected;
    private javax.swing.JLabel jLabelSelectedParent;
    private javax.swing.JLabel jLabelasplain;
    private javax.swing.JLabel jLabelcount;
    javax.swing.JLabel jLabeldbstatus;
    private javax.swing.JLabel jLabelemail;
    private javax.swing.JLabel jLabelstatus;
    private javax.swing.JList<String> jList1;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemDelete;
    private javax.swing.JMenuItem jMenuItemGetPrefixInfo;
    private javax.swing.JMenuItem jMenuItemModify;
    private javax.swing.JMenuItem jMenuItemSelectAll;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldAsplain;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldNetname;
    private javax.swing.JTextField jTextFieldOrg;
    private javax.swing.JTextField jTextFieldPerson;
    private javax.swing.JTextField jTextFieldPhone;
    private javax.swing.JTextField jTextFieldPrefix;
    // End of variables declaration//GEN-END:variables
}
