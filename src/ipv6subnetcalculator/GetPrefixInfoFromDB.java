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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
public class GetPrefixInfoFromDB extends javax.swing.JFrame {

    //<editor-fold defaultstate="collapsed" desc="special initials/constants -yucel">
    Connection MySQLconnection = null;
    DBServerInfo dbserverInfo = new DBServerInfo();
    Statement statement = null;
    ResultSet resultSet = null;
    public ArrayList<String> liste = new ArrayList<String>();

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    String prefix = "";
    int pflen = 0;
    String parentNet = "";
    //</editor-fold>

    public void setConnection(Connection con) {
        MySQLconnection = con;
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
                        + " AND pflen = " + pflen + " ORDER BY parentpflen ASC";

                mystatement = MySQLconnection.createStatement();
                myresultSet = mystatement.executeQuery(MySQLcmd);

                short ppflen = -1;

                if (myresultSet.next()) {
                    ppflen = Short.valueOf(myresultSet.getString(1));
                    //System.out.println("FOUND parentpflength in DB: >> " + ppflen);
                } else {
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
                        + " AND pflen=" + sa[1] + " AND parentpflen=" + sa[1];

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

    private int DBQueryPrefix() {
        if (this.MySQLconnection == null) {
            MsgBox.Show(this, "There is no opened DB connection!", "Error");
            return -1;
        }

        this.jList1.setModel(new DefaultListModel<String>());
        liste.clear();
        int r = 0;

        String MySQLcmd = "SELECT "
                + " INET6_NTOA(prefix), pflen, parentpflen, netname, person, organization, "
                + "`as-num`, phone, email, status, created, `last-updated` FROM "
                + "`" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                + " WHERE prefix = inet6_aton('" + this.prefix + "')"
                + " AND pflen = " + String.valueOf(this.pflen);

        try {
            statement = MySQLconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(MySQLcmd);

            if (resultSet.last()) {
                r = resultSet.getRow();
                resultSet.beforeFirst();
            }

            if (r > 0) {
                Boolean isParentinDB = false;
                while (resultSet.next()) {
                    liste.add("  prefix:\t " + resultSet.getString(1)
                            + "/" + resultSet.getString(2));

                    isParentinDB = isParentNetinDB(resultSet.getString(1), Short.valueOf(resultSet.getString(3)), true);

                    if (isParentinDB) {
                        liste.add("  parent:\t " + parentNet);
                    } else {
                        String sp = parentNet.split("/")[1];
                        liste.add("  parent:\t " + parentNet + " (/" + sp + "-" + sp + " *Not_in_DB*)");
                    }
                    liste.add("  netname:\t " + resultSet.getString(4));
                    liste.add("  person:\t " + resultSet.getString(5));
                    liste.add("  organization:\t " + resultSet.getString(6));
                    liste.add("  as-num:\t " + resultSet.getString(7));
                    liste.add("  phone:\t\t " + resultSet.getString(8));
                    liste.add("  email:\t\t " + resultSet.getString(9));
                    liste.add("  status:\t " + resultSet.getString(10));
                    liste.add("  created:\t " + resultSet.getString(11));
                    liste.add("  last-updated:\t " + resultSet.getString(12));
                    liste.add(" ");
                }

                this.jList1.setListData(liste.toArray(String[]::new));

            } else {
                liste.add(" ");
                liste.add("  > Not found.  Prefix does not exist in the database.");

                this.jList1.setListData(liste.toArray(String[]::new));
            }
        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error");
            return -1;
        }

        return r;
    }

    /**
     * Creates new form GetPrefixInfoFromDB
     *
     * @param pfix
     * @param sqlcon
     * @param servinfo
     */
    public GetPrefixInfoFromDB(String pfix, Connection sqlcon, DBServerInfo servinfo) {
        initComponents();

        EscapeKey();
        MySQLconnection = sqlcon;
        dbserverInfo = servinfo;
        String[] sa = pfix.split("/");
        this.prefix = sa[0];
        this.pflen = Integer.parseInt(sa[1]);

        IPv6SubnetCalculator.AddWindowItem(this.getTitle(),
                (JFrame) SwingUtilities.getRoot(this), this.hashCode());

        IPv6SubnetCalculator.UpdateDbStatus();

        int r = DBQueryPrefix();

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
        jMenuItemRefresh = new javax.swing.JMenuItem();
        jMenuItemModifyPrefix = new javax.swing.JMenuItem();
        jMenuItemGetPrefixInfo = new javax.swing.JMenuItem();
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

        jMenuItemRefresh.setMnemonic('R');
        jMenuItemRefresh.setText("Refresh");
        jMenuItemRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRefreshActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemRefresh);

        jMenuItemModifyPrefix.setMnemonic('M');
        jMenuItemModifyPrefix.setText("Modify prefix");
        jMenuItemModifyPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemModifyPrefixActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemModifyPrefix);

        jMenuItemGetPrefixInfo.setMnemonic('G');
        jMenuItemGetPrefixInfo.setText("Get prefix info");
        jMenuItemGetPrefixInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGetPrefixInfoActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemGetPrefixInfo);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Get Prefix Info from DB");
        setMinimumSize(new java.awt.Dimension(510, 268));
        setName("GetPrefixInfoFromDB"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jList1.setCellRenderer(new DatabaseUI.MyCellRenderer());
        jList1.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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

    private void jMenuItemRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRefreshActionPerformed
        int r = DBQueryPrefix();
    }//GEN-LAST:event_jMenuItemRefreshActionPerformed

    private void jMenuItemGetPrefixInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGetPrefixInfoActionPerformed
        if (this.jList1.getSelectedIndex() % 12 == 1) {
            IPv6SubnetCalculator.UpdateDbStatus();
            if (MySQLconnection != null) {
                if (!this.jList1.getSelectedValue().trim().equals("") && this.jList1.getSelectedValue() != null) {
                    String selected = this.jList1.getSelectedValue().trim().split(" ")[1]; // Bosluklara dikkat/ we've spaces :) careful.

                    GetPrefixInfoFromDB getpfxdbinfo = new GetPrefixInfoFromDB(selected, MySQLconnection, dbserverInfo);
                    IPv6SubnetCalculator.getPrefixInfo.add(getpfxdbinfo);
                    getpfxdbinfo.setVisible(true);
                }
            }
        }
    }//GEN-LAST:event_jMenuItemGetPrefixInfoActionPerformed

    private void jMenuItemModifyPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemModifyPrefixActionPerformed
        IPv6SubnetCalculator.UpdateDbStatus();
        if (MySQLconnection != null && this.jList1.getSelectedIndex() % 12 == 0) {
            String snet;
            short plen;
            short parentpflen;

            if (!this.jList1.getSelectedValue().trim().equals("") && this.jList1.getSelectedValue() != null) {
                String selected = this.jList1.getSelectedValue().trim().split(" ")[1]; // Bosluklara dikkat/ we've spaces :) careful.
                int sIdx = this.jList1.getSelectedIndex();
                snet = selected.split("/")[0].trim();
                plen = Short.valueOf(selected.split("/")[1].trim());

                parentpflen = Short.valueOf(this.jList1.getModel().getElementAt(sIdx + 1).trim().split(" ")[1].trim().split("/")[1]);

                DatabaseUI dbui = new DatabaseUI(snet, plen, parentpflen, MySQLconnection, dbserverInfo);

                IPv6SubnetCalculator.dbUI.add(dbui);

                dbui.jLabeldbstatus.setText("db=Up ");
                dbui.setVisible(true);
            }
        }
    }//GEN-LAST:event_jMenuItemModifyPrefixActionPerformed

    private void jPopupMenu1PopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenu1PopupMenuWillBecomeVisible
        if (this.jList1.getModel().getElementAt(0).contains("prefix")) {

            switch (this.jList1.getSelectedIndex() % 12) {
                case 0 -> {
                    this.jMenuItemGetPrefixInfo.setEnabled(false);
                    this.jMenuItemModifyPrefix.setEnabled(true);
                }
                case 1 -> {
                    this.jMenuItemGetPrefixInfo.setEnabled(true);
                    this.jMenuItemModifyPrefix.setEnabled(false);
                }
                default -> {
                    this.jMenuItemGetPrefixInfo.setEnabled(false);
                    this.jMenuItemModifyPrefix.setEnabled(false);
                }
            }
        } else {
            this.jMenuItemGetPrefixInfo.setEnabled(false);
            this.jMenuItemModifyPrefix.setEnabled(false);
        }
    }//GEN-LAST:event_jPopupMenu1PopupMenuWillBecomeVisible

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> jList1;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemGetPrefixInfo;
    private javax.swing.JMenuItem jMenuItemModifyPrefix;
    private javax.swing.JMenuItem jMenuItemRefresh;
    private javax.swing.JMenuItem jMenuItemSelectAll;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
