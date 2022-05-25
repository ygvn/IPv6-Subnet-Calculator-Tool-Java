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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
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
public final class ListAssignedfromDB extends javax.swing.JFrame {

//<editor-fold defaultstate="collapsed" desc="special initials/constants -Yücel">
    //
    SEaddress seaddr = new SEaddress();
    String prefix = "";
    String parentNet = "";
    String end = "";
    short pflen = 0;
    short parentpflen = 0;
    String tmp_first = "";
    String tmp_last = "";
    String last_start = "";
    String db_FirstItem = "";
    String db_LastItem = "";
    int db_ItemCount = 0;
    int page_records = 0;
    final int records_perpage = 32;

    DatabaseUI dbUI = null;
    static Connection MySQLconnection = null;
    public DBServerInfo dbserverInfo = new DBServerInfo();
    Boolean chks = Boolean.FALSE;
    Statement statement = null;
    ResultSet resultSet = null;
    public ArrayList<String> liste = new ArrayList<String>();

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

    private void PreCalc() {
        this.seaddr.End = v6ST.FormalizeAddr(this.end);
        this.seaddr.slash = this.parentpflen;
        this.seaddr.subnetslash = this.pflen;
        this.seaddr = v6ST.EndStartAddresses(this.seaddr, this.chks);

        if (chks) {
            this.last_start = v6ST.Kolonlar(this.seaddr.Start);
        } else if (!chks) {
            this.last_start = v6ST.Kolonlar(this.seaddr.Start);
            this.last_start = this.last_start.substring(0, 19);
            this.last_start += "::";
        }
        this.last_start = v6ST.CompressAddress(this.last_start);
    }

    public int FirstAndLastInDB() {

        if (MySQLconnection == null) {
            return -1;
        }
        int r = 0;
        String MySQLcmd = "SELECT COUNT(*) FROM "
                + "`" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                + " WHERE ( prefix >= inet6_aton('" + this.prefix + "')"
                + " AND prefix <= inet6_aton('" + this.end + "')"
                + " AND parentpflen= " + parentpflen + " AND pflen= " + pflen + " ) ";

        try {
            IPv6SubnetCalculator.UpdateDbStatus();
            statement = MySQLconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(MySQLcmd);
            resultSet.next();
            this.db_ItemCount = Integer.parseInt(resultSet.getString(1));
            this.jLabelstatus.setText(" Total=[" + this.db_ItemCount + "]");
        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error:");
            IPv6SubnetCalculator.UpdateDbStatus();
            return -1;
        }

        MySQLcmd = "SELECT "
                + " INET6_NTOA(prefix), pflen, netname, person, organization, "
                + "`as-num`, phone, email, status, created, `last-updated` FROM "
                + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                + " WHERE ( prefix >= inet6_aton('" + this.prefix + "')"
                + " AND prefix <= inet6_aton('" + this.end + "')"
                + " AND parentpflen= " + parentpflen + " AND pflen= " + pflen + " ) "
                + " LIMIT " + this.records_perpage;

        try {
            statement = MySQLconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(MySQLcmd);
            if (resultSet.last()) {
                this.page_records = r = resultSet.getRow();
                resultSet.beforeFirst();
            }
            if (r > 0) {
                resultSet.next();
                this.db_FirstItem = resultSet.getString(1);
            }
        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error:");
            IPv6SubnetCalculator.UpdateDbStatus();
            return -1;
        }

        MySQLcmd = "SELECT "
                + " INET6_NTOA(prefix), pflen, netname, person, organization, "
                + "`as-num`, phone, email, status, created, `last-updated` FROM "
                + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                + " WHERE ( prefix <= inet6_aton('" + this.end + "')"
                + " AND parentpflen= " + parentpflen + " AND pflen= " + pflen + " ) "
                + " ORDER BY prefix DESC LIMIT " + this.records_perpage;

        try {
            statement = MySQLconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(MySQLcmd);
            if (resultSet.last()) {
                this.page_records = r = resultSet.getRow();
                resultSet.beforeFirst();
            }

            if (r > 0) {
                resultSet.next();
                this.db_LastItem = resultSet.getString(1);
            }
        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error:");
            IPv6SubnetCalculator.UpdateDbStatus();
            return -1;
        }

        return r;
    }

    public void SetNewValues(String inprefix, String inend, short inparentpflen,
            short inpflen, Boolean inchks) {

        this.prefix = v6ST.CompressAddress(inprefix.split("/")[0]);
        this.end = v6ST.CompressAddress(inend.split("/")[0]);
        this.parentpflen = inparentpflen;
        this.pflen = inpflen;
        this.chks = inchks;
        this.page_records = 0;

        this.jLabelr0.setText("/" + inpflen + " Prefix Utilization.");
        this.jLabelRange.setText("Range> " + inprefix + "-" + inpflen);

        PreCalc();
        FirstAndLastInDB();
        this.jButtonFirstPageActionPerformed(null);
        this.jLabelcount.setText("[" + this.page_records + "]");
    }

    private void GetSelected() {
        IPv6SubnetCalculator.UpdateDbStatus();
        if (MySQLconnection != null) {
            if ((this.jList1.getSelectedIndex() % 12 == 0) || (this.jList1.getSelectedIndex() % 12 == 1)) {
                String selected = this.jList1.getSelectedValue().split(" ")[1].trim();
                String snet = selected.split("/")[0].trim();
                short plen = Short.parseShort(selected.split("/")[1]);

                dbUI = new DatabaseUI(snet, plen, parentpflen, MySQLconnection, dbserverInfo);
                IPv6SubnetCalculator.dbUI.add(dbUI);

                if (MySQLconnection != null) {
                    dbUI.jLabeldbstatus.setText("db=Up ");
                }
                dbUI.setVisible(true);
            }
        }
    }

    public int MySQLquery(int button) {

        this.page_records = 0;

        if (MySQLconnection == null) {
            return -1;
        }

        if (this.tmp_last.equals("")) {
            this.tmp_last = this.prefix;
        }

        int r = 0;
        String MySQLcmd = "";
        //this.prefixlist.getItems().clear();
        this.jList1.setModel(new DefaultListModel<String>());

        if (button == 1) // // First page
        {
            MySQLcmd = "SELECT "
                    + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, organization, "
                    + "`as-num`, phone, email, status, created, `last-updated` FROM "
                    + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                    + " WHERE ( prefix >= inet6_aton('" + this.tmp_last + "')"
                    + " AND prefix <= inet6_aton('" + this.end + "')"
                    + " AND parentpflen= " + parentpflen + " AND pflen= " + pflen + " ) "
                    + " LIMIT " + this.records_perpage;
        } else if (button == 2) // Backwd page
        {
            MySQLcmd = "SELECT "
                    + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, organization, "
                    + "`as-num`, phone, email, status, created, `last-updated` FROM "
                    + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                    + " WHERE ( prefix < inet6_aton('" + this.tmp_first + "')"
                    + " AND prefix >= inet6_aton('" + this.prefix + "')"
                    + " AND parentpflen= " + parentpflen + " AND pflen= " + pflen + " ) "
                    + " ORDER BY prefix DESC LIMIT " + this.records_perpage;
        } else if (button == 3) // Fwd page
        {
            MySQLcmd = "SELECT "
                    + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, organization, "
                    + "`as-num`, phone, email, status, created, `last-updated` FROM "
                    + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                    + " WHERE ( prefix > inet6_aton('" + this.tmp_last + "')"
                    + " AND prefix <= inet6_aton('" + this.end + "')"
                    + " AND parentpflen= " + parentpflen + " AND pflen= " + pflen + " ) "
                    + " LIMIT " + this.records_perpage;
        } else if (button == 4) // Last page
        {
            MySQLcmd = "SELECT "
                    + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, organization, "
                    + "`as-num`, phone, email, status, created, `last-updated` FROM "
                    + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                    + " WHERE ( prefix <= inet6_aton('" + this.tmp_first + "')"
                    + " AND parentpflen= " + parentpflen + " AND pflen= " + pflen + " ) "
                    + " ORDER BY prefix DESC LIMIT " + this.records_perpage;
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

                if (button == 1 || button == 3) {
                    while (resultSet.next()) {
                        liste.add("prefix:\t\t " + resultSet.getString(1)
                                + "/" + resultSet.getString(2));

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
                } else if (button == 2 || button == 4) {

                    String[] fs = new String[11];
                    while (resultSet.next()) {
                        liste.add(" ");
                        fs[0] = "prefix:\t\t " + resultSet.getString(1)
                                + "/" + resultSet.getString(2);

                        if (isParentNetinDB(resultSet.getString(1), Short.valueOf(resultSet.getString(3)), true)) {
                            fs[1] = "parent:\t " + this.parentNet;
                        } else {
                            String sp = this.parentNet.split("/")[1];
                            fs[1] = "parent:\t " + this.parentNet + " (/" + sp + "-" + sp + " *Not_in_DB*)";
                        }

                        fs[2] = "netname:\t " + resultSet.getString(4);
                        fs[3] = "person:\t\t " + resultSet.getString(5);
                        fs[4] = "organization:\t " + resultSet.getString(6);
                        fs[5] = "as-num:\t\t " + resultSet.getString(7);
                        fs[6] = "phone:\t\t " + resultSet.getString(8);
                        fs[7] = "email:\t\t " + resultSet.getString(9);
                        fs[8] = "status:\t\t " + resultSet.getString(10);
                        fs[9] = "created:\t " + resultSet.getString(11);
                        fs[10] = "last-updated:\t " + resultSet.getString(12);

                        for (int i = 10; i > -1; i--) {
                            liste.add(fs[i]);
                        }
                    }
                    Collections.reverse(liste);
                }
                //prefixlist.setItems(liste);
                this.jList1.setListData(liste.toArray(String[]::new));

                this.tmp_first = liste.get(0).split(" ")[1].split("/")[0];
                this.tmp_last = liste.get(liste.size() - 12).split(" ")[1].split("/")[0];
            }

            return r;
        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error:");
            IPv6SubnetCalculator.UpdateDbStatus();
            return -1;
        }
    }

    public void setConnection(Connection con) {
        MySQLconnection = con;
    }

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
                    //System.out.println("FOUND parentprefix in DB: >> " + s);
                    Found = true;

                } else {
                    //System.out.println("NOT_Found parentprefix in DB: >> " + s);
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
                    //System.out.println("FOUND parentpflength in DB: >> " + ppflen);
                } else {
                    //System.out.println("NOT_Found parentpflength in DB: >> " + ppflen);
                    return false;
                }

                // with ppflen, find ParentNet using v6ST:
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
                    //System.out.println("FOUND parentprefix in DB: >> " + s);
                    Found = true;

                } else {
                    //System.out.println("NOT_Found parentprefix in DB: >> " + s);
                    return false;
                }
            }

        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error");
            return false;
        }

        return Found;
    }

    /**
     * Creates new form ListAssignedfromDB
     *
     * @param inprefix
     * @param inend
     * @param inparentpflen
     * @param inpflen
     * @param inchks
     * @param sqlcon
     * @param servinfo
     */
    public ListAssignedfromDB(String inprefix, String inend, short inparentpflen,
            short inpflen, Boolean inchks, Connection sqlcon, DBServerInfo servinfo) {
        initComponents();
        //
        EscapeKey();
        IPv6SubnetCalculator.AddWindowItem(this.getTitle(), (JFrame) SwingUtilities.getRoot(this), this.hashCode());

        MySQLconnection = sqlcon;
        this.dbserverInfo = servinfo;
        //
        SetNewValues(inprefix, inend, inparentpflen, inpflen, inchks);
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
        jMenuItemGetPrefixInfo = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabeldbstatus = new javax.swing.JLabel();
        jLabelstatus = new javax.swing.JLabel();
        jLabelr0 = new javax.swing.JLabel();
        jLabelRange = new javax.swing.JLabel();
        jButtonFirstPage = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jButtonFwd = new javax.swing.JButton();
        jButtonLastPage = new javax.swing.JButton();
        jLabelcount = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();

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
        jMenuItemCopy.setMnemonic('C');
        jMenuItemCopy.setText("Copy");
        jMenuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemCopy);

        jMenuItemModify.setMnemonic('M');
        jMenuItemModify.setText("Modify selected prefix");
        jMenuItemModify.setEnabled(false);
        jMenuItemModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemModifyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemModify);

        jMenuItemGetPrefixInfo.setMnemonic('G');
        jMenuItemGetPrefixInfo.setText("Get prefix info");
        jMenuItemGetPrefixInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGetPrefixInfoActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemGetPrefixInfo);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("List Assigned from DB");
        setMinimumSize(new java.awt.Dimension(525, 465));
        setName("ListAssignedfromDB"); // NOI18N
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

        jLabeldbstatus.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabeldbstatus.setForeground(new java.awt.Color(9, 160, 254));
        jLabeldbstatus.setText("db=Down");

        jLabelstatus.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabelstatus.setForeground(new java.awt.Color(9, 160, 254));
        jLabelstatus.setText(" ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabelstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabeldbstatus)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabeldbstatus)
                    .addComponent(jLabelstatus)))
        );

        jLabelr0.setForeground(new java.awt.Color(9, 160, 254));
        jLabelr0.setText("/xx Utilization");

        jLabelRange.setForeground(new java.awt.Color(9, 160, 254));
        jLabelRange.setText("Range> ");

        jButtonFirstPage.setText("|< First Page");
        jButtonFirstPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFirstPageActionPerformed(evt);
            }
        });

        jButtonBack.setText(" < ");
        jButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackActionPerformed(evt);
            }
        });

        jButtonFwd.setText(" > ");
        jButtonFwd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFwdActionPerformed(evt);
            }
        });

        jButtonLastPage.setText(" >|");
        jButtonLastPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLastPageActionPerformed(evt);
            }
        });

        jLabelcount.setFont(new java.awt.Font("Cantarell", 0, 13)); // NOI18N
        jLabelcount.setForeground(new java.awt.Color(9, 160, 254));
        jLabelcount.setText("[ ]");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonFirstPage)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonBack)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonFwd)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonLastPage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
                        .addComponent(jLabelcount))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelRange)
                            .addComponent(jLabelr0))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelr0)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelRange)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelcount, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonFirstPage)
                        .addComponent(jButtonBack)
                        .addComponent(jButtonFwd)
                        .addComponent(jButtonLastPage)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonFirstPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFirstPageActionPerformed
        tmp_last = "";
        PreCalc();
        int r = MySQLquery(1);

        if (r > 0) {
            if (db_ItemCount > records_perpage) {
                this.jButtonBack.setEnabled(false);
                this.jButtonFwd.setEnabled(true);
                this.jButtonLastPage.setEnabled(true);
            } else {
                this.jButtonBack.setEnabled(false);
                this.jButtonFwd.setEnabled(false);
                this.jButtonLastPage.setEnabled(false);
            }
        } else {
            this.jButtonBack.setEnabled(false);
            this.jButtonFwd.setEnabled(false);
            this.jButtonLastPage.setEnabled(false);
        }
        this.jLabelcount.setText("[" + page_records + "]");
    }//GEN-LAST:event_jButtonFirstPageActionPerformed

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        int r = MySQLquery(2);
        if (r > 0) {
            this.jButtonFwd.setEnabled(true);
            this.jButtonLastPage.setEnabled(true);

            if (db_FirstItem.equals(this.jList1.getModel().getElementAt(0).split(" ")[1].split("/")[0])) {
                this.jButtonBack.setEnabled(false);
                this.jButtonFwd.setEnabled(true);
                this.jLabelcount.setText("[" + page_records + "]");
                return;
            }
        }
        this.jLabelcount.setText("[" + page_records + "]");
    }//GEN-LAST:event_jButtonBackActionPerformed

    private void jButtonFwdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFwdActionPerformed
        int r = MySQLquery(3);
        if (r > 0) {
            this.jButtonBack.setEnabled(true);

            if (db_LastItem.equals(tmp_last)) {
                this.jButtonFwd.setEnabled(false);
                this.jButtonLastPage.setEnabled(false);
                this.jLabelcount.setText("[" + page_records + "]");
                return;
            }
        }
        this.jLabelcount.setText("[" + page_records + "]");

    }//GEN-LAST:event_jButtonFwdActionPerformed

    private void jButtonLastPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLastPageActionPerformed
        tmp_first = last_start;
        int r = MySQLquery(4);
        if (r > 0) {
            this.jButtonBack.setEnabled(true);
            this.jButtonFwd.setEnabled(false);
            this.jButtonLastPage.setEnabled(false);
        }
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

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
            GetSelected();
        }
    }//GEN-LAST:event_jList1MouseClicked

    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            GetSelected();
        }
    }//GEN-LAST:event_jList1KeyPressed

    private void jMenuItemModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemModifyActionPerformed
        GetSelected();
    }//GEN-LAST:event_jMenuItemModifyActionPerformed

    private void jPopupMenu1PopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenu1PopupMenuWillBecomeVisible

        switch (this.jList1.getSelectedIndex() % 12) {
            case 0 -> {
                // prefix
                this.jMenuItemModify.setEnabled(true);
                this.jMenuItemGetPrefixInfo.setEnabled(false);
            }
            case 1 -> {
                // parentNet
                this.jMenuItemModify.setEnabled(true);
                this.jMenuItemGetPrefixInfo.setEnabled(true);
            }
            default -> {
                this.jMenuItemModify.setEnabled(false);
                this.jMenuItemGetPrefixInfo.setEnabled(false);
            }
        }
    }//GEN-LAST:event_jPopupMenu1PopupMenuWillBecomeVisible

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        this.lastPos = new java.awt.Point(evt.getX(), evt.getY());
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if (SwingUtilities.isRightMouseButton(evt)) {
            this.setLocation(this.getLocation().x + evt.getX() - this.lastPos.x,
                    this.getLocation().y + evt.getY() - this.lastPos.y);
        }
    }//GEN-LAST:event_formMouseDragged

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
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonFirstPage;
    private javax.swing.JButton jButtonFwd;
    private javax.swing.JButton jButtonLastPage;
    private javax.swing.JLabel jLabelRange;
    private javax.swing.JLabel jLabelcount;
    javax.swing.JLabel jLabeldbstatus;
    private javax.swing.JLabel jLabelr0;
    private javax.swing.JLabel jLabelstatus;
    private javax.swing.JList<String> jList1;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemGetPrefixInfo;
    private javax.swing.JMenuItem jMenuItemModify;
    private javax.swing.JMenuItem jMenuItemSelectAll;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
