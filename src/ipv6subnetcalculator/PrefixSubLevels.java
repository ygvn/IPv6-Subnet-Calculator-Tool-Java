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
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Yucel Guven
 */
public final class PrefixSubLevels extends javax.swing.JFrame {

//<editor-fold defaultstate="collapsed" desc="special initials/constants -yucel">
    static String prefix = null;
    static short pflen = 0;
    static String parentprefix = null;
    static short parentpflen = 0;
    static int t1 = 0, t2 = 0;
    String end = null;
    Boolean chks = false;
    List<String[]> liste = null;

    //Database
    static Connection MySQLconnection = null;
    DBServerInfo dbserverInfo = null;
    Statement statement = null;
    ResultSet resultSet = null;
    //
    DefaultMutableTreeNode rootItem = null;
    DefaultMutableTreeNode currentSelected = null;
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

    private void F5RefreshKey() {
        int hc = this.hashCode() + 5; // 8-)
        // For F5 key:
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
        SwingUtilities.getUIInputMap(this.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, hc);
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, hc);
        SwingUtilities.getUIActionMap(this.getRootPane()).put(hc, new Action() {
            @Override
            public void addPropertyChangeListener(PropertyChangeListener pl) {
                //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void actionPerformed(ActionEvent ae) {
                jMenuItemRefreshActionPerformed(null);
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

    public int MySQLquery(String inprefix, String end, short pflen) {

        IPv6SubnetCalculator.UpdateDbStatus();

        if (MySQLconnection == null) {
            return -1;
        }

        this.liste.clear();

        int r = 0;
        String MySQLcmd = "";
        String is128bits = "";

        if (!this.chks) {
            is128bits = " AND pflen > " + pflen + " AND pflen <= 64";
        } else if (this.chks) {
            is128bits = " AND pflen > " + pflen;
        }

        MySQLcmd = "SELECT inet6_ntoa(prefix), pflen, netname, status FROM "
                + " `" + dbserverInfo.DBname + "`.`" + dbserverInfo.Tablename + "` "
                + " WHERE ( prefix BETWEEN inet6_aton('" + inprefix + "') "
                + " AND inet6_aton('" + end + "') "
                + is128bits + " AND parentpflen= " + pflen + ") "
                + " LIMIT 32768; ";

        try {
            statement = MySQLconnection.createStatement();
            resultSet = statement.executeQuery(MySQLcmd);
            this.liste.clear();

            while (resultSet.next()) {
                this.liste.add(new String[]{
                    resultSet.getString(1), resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)}
                );
                r++;
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

    private String FindEnd(String snet, short pflen, Boolean chks) {
        SEaddress se = new SEaddress();
        String lend = "";

        if (chks) {
            se.Start = v6ST.FormalizeAddr(snet);
            se.slash = this.t1;
            se.subnetslash = pflen;
            se = v6ST.Subnetting(se, chks);
            lend = v6ST.Kolonlar(se.End);
        } else if (!chks) {
            String start = v6ST.Kolonlar(v6ST.FormalizeAddr(snet));
            start = start.substring(0, 19) + "::";
            se.Start = v6ST.FormalizeAddr(start);
            se.slash = this.t1;
            se.subnetslash = pflen;
            se = v6ST.Subnetting(se, chks);
            lend = v6ST.Kolonlar(se.End);
            lend = lend.substring(0, 19) + "::";
        }
        lend = v6ST.CompressAddress(lend);

        return lend;
    }

    private void AddChildrenOf(DefaultMutableTreeNode selectednode) {

        DefaultTreeModel model = (DefaultTreeModel) this.jTree1.getModel();
        selectednode.removeAllChildren();

        String[] ab = selectednode.getUserObject().toString().split("/");
        String inprefix = ab[0];
        short pfln = Short.parseShort(ab[1]);
        String lend = this.FindEnd(inprefix, pfln, this.chks);

        int r = this.MySQLquery(inprefix, lend, pfln);

        if (r > 0) {
            int i = 0;
            for (String[] s : this.liste) {

                DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(s[0] + "/" + s[1]);
                selectednode.add(newChild);

                if (!this.chks) {
                    if (!s[1].equals("64")) {
                        newChild.add(new DefaultMutableTreeNode(""));
                    }
                } else if (this.chks) {
                    if (!s[1].equals("128")) {
                        newChild.add(new DefaultMutableTreeNode(""));
                    }
                }
                i++;
            }
        } else {
            selectednode.removeAllChildren();
        }
        model.nodeStructureChanged(selectednode);
    }

    private Boolean isParentNetinDB(String parentprefix) {

        Statement mystatement;
        ResultSet myresultSet;
        String[] sa;

        sa = parentprefix.split("/");

        String MySQLcmd = "SELECT inet6_ntoa(prefix), pflen, parentpflen FROM "
                + " `" + this.dbserverInfo.DBname + "`." + "`" + this.dbserverInfo.Tablename + "` "
                + " WHERE prefix=inet6_aton('" + sa[0] + "') "
                + " AND pflen=" + sa[1] + " AND parentpflen=" + sa[1];

        try {
            mystatement = MySQLconnection.createStatement();
            myresultSet = mystatement.executeQuery(MySQLcmd);

            String s = "";

            if (myresultSet.next()) {
                s = myresultSet.getString(2);
            } else {
                return false;
            }
        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error");
            return false;
        }

        return true;
    }

    public void SetSelectedFirst() {

        if (isParentNetinDB(parentprefix)) {
            this.jLabelParentPrefix.setText(parentprefix);
        } else {
            this.jLabelParentPrefix.setText(parentprefix + " *Not_found_in_DB*");
        }

        String s = this.prefix + "/" + this.pflen;
        if (!s.equals(parentprefix)) {
            this.jLabelSelectedPrefix.setText("  └ " + this.prefix + "/" + this.pflen);
        } else {
            this.jLabelSelectedPrefix.setText(this.prefix + "/" + this.pflen);
        }

        this.rootItem = new DefaultMutableTreeNode(this.prefix + "/" + String.valueOf(this.pflen));
        this.end = this.FindEnd(this.prefix, this.pflen, this.chks);
        this.rootItem.add(new DefaultMutableTreeNode(" ")); // dummy
        this.jTree1.setModel(new DefaultTreeModel(rootItem));
        DefaultTreeModel model = (DefaultTreeModel) this.jTree1.getModel();
        model.nodeChanged(rootItem);
        this.jTree1.collapseRow(0);
    }

    public void SetNewValues(String snet, short pf, String inppfx, int int1, int int2, Boolean inchk128) {
        this.prefix = snet;
        this.pflen = pf;
        this.parentprefix = inppfx;
        this.t1 = int1;
        this.t2 = int2;
        this.chks = inchk128;
        SetSelectedFirst();
    }

    /**
     * Creates new form PrefixSubLevels
     *
     * @param inprefix
     * @param inpflen
     * @param inparentprefix
     * @param chks
     * @param int1
     * @param int2
     * @param sqlcon
     * @param servinfo
     */
    public PrefixSubLevels(String inprefix, short inpflen,
            String inparentprefix, Boolean chks, int int1, int int2,
            Connection sqlcon, DBServerInfo servinfo) {

        initComponents();
        IPv6SubnetCalculator.AddWindowItem(this.getTitle(), (JFrame) SwingUtilities.getRoot(this), this.hashCode());
        EscapeKey();
        F5RefreshKey();
        //
        this.liste = new ArrayList<String[]>();
        this.dbserverInfo = new DBServerInfo();
        this.prefix = v6ST.CompressAddress(inprefix);
        this.parentprefix = inparentprefix;
        this.pflen = inpflen;
        this.parentpflen = inpflen;
        this.chks = chks;
        this.t1 = int1;
        this.t2 = int2;
        MySQLconnection = sqlcon;
        this.dbserverInfo = servinfo;
        //
        this.jTree1.setShowsRootHandles(true);
        this.jTree1.removeAll();

        SetSelectedFirst();
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
        jMenuItemRefresh = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemCopy = new javax.swing.JMenuItem();
        jMenuItemGetprefixInfo = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabelParentPrefix = new javax.swing.JLabel();
        jLabelSelectedPrefix = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabeldbstatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();

        jMenuItemRefresh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItemRefresh.setMnemonic('R');
        jMenuItemRefresh.setText("Refresh (Selected Prefix)");
        jMenuItemRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRefreshActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemRefresh);
        jPopupMenu1.add(jSeparator1);

        jMenuItemCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemCopy.setMnemonic('C');
        jMenuItemCopy.setText("Copy");
        jMenuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemCopy);

        jMenuItemGetprefixInfo.setText("Get prefix info from database");
        jMenuItemGetprefixInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGetprefixInfoActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemGetprefixInfo);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Prefix Sub-Levels");
        setName("PrefixSubLevels"); // NOI18N
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

        jLabel1.setText("Parent Prefix:");

        jLabel2.setForeground(new java.awt.Color(9, 160, 254));
        jLabel2.setText("Selected Prefix:");

        jLabelParentPrefix.setText("pprefix");

        jLabelSelectedPrefix.setForeground(new java.awt.Color(9, 160, 254));
        jLabelSelectedPrefix.setText("sprefix");

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

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("JTree");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("colors");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("red");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("green");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.setComponentPopupMenu(jPopupMenu1);
        jTree1.setDoubleBuffered(true);
        jTree1.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                jTree1TreeExpanded(evt);
            }
        });
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelParentPrefix)
                            .addComponent(jLabelSelectedPrefix))
                        .addGap(0, 287, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabelParentPrefix))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabelSelectedPrefix))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTree1TreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_jTree1TreeExpanded

        DefaultMutableTreeNode selectednode = (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();

        selectednode.removeAllChildren();

        AddChildrenOf(selectednode);
    }//GEN-LAST:event_jTree1TreeExpanded

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRefreshActionPerformed

        if (this.currentSelected == null) {
            rootItem.removeAllChildren();
            AddChildrenOf(rootItem);
        } else {
            this.currentSelected.removeAllChildren();
            AddChildrenOf(this.currentSelected);
        }
    }//GEN-LAST:event_jMenuItemRefreshActionPerformed

    private void jMenuItemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopyActionPerformed
        this.jTree1.getTransferHandler().exportToClipboard(this.jTree1, this.clipboard, TransferHandler.COPY);
    }//GEN-LAST:event_jMenuItemCopyActionPerformed

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        if (evt.getPath() != null) {
            this.currentSelected = (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();
        } else {
            this.currentSelected = null;
        }
    }//GEN-LAST:event_jTree1ValueChanged

    private void jMenuItemGetprefixInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGetprefixInfoActionPerformed
        if (this.currentSelected != null) {
            String selected = this.currentSelected.getUserObject().toString();
            GetPrefixInfoFromDB getpfxdbinfo = new GetPrefixInfoFromDB(selected, MySQLconnection, dbserverInfo);
            IPv6SubnetCalculator.getPrefixInfo.add(getpfxdbinfo);
            getpfxdbinfo.setVisible(true);
        }
    }//GEN-LAST:event_jMenuItemGetprefixInfoActionPerformed

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelParentPrefix;
    private javax.swing.JLabel jLabelSelectedPrefix;
    javax.swing.JLabel jLabeldbstatus;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemGetprefixInfo;
    private javax.swing.JMenuItem jMenuItemRefresh;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}
