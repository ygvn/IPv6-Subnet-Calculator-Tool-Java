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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author Yucel Guven
 */
public class DBConnectInfo extends javax.swing.JDialog {

    public Boolean isCanceled = false;
    public Boolean isLaunchDBUI = false;
    private java.awt.Point lastPos = new java.awt.Point();

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
                isCanceled = true;
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
     * Creates new form DBConnectInfo
     *
     * @param parent
     * @param modal
     */
    public DBConnectInfo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        EscapeKey();
        //
        if (IPv6SubnetCalculator.dbserverInfo.ServerIP != null) {
            this.jTextFieldServerIP.setText(IPv6SubnetCalculator.dbserverInfo.ServerIP.getHostAddress());
        }
        if (String.valueOf(IPv6SubnetCalculator.dbserverInfo.PortNum) != null) {
            this.jTextFieldPort.setText(String.valueOf(IPv6SubnetCalculator.dbserverInfo.PortNum).trim());
        }
        if (!IPv6SubnetCalculator.dbserverInfo.DBname.equals("")) {
            this.jTextFieldDBName.setText(IPv6SubnetCalculator.dbserverInfo.DBname.trim());
        }
        if (!IPv6SubnetCalculator.dbserverInfo.Tablename.equals("")) {
            this.jTextFieldTableName.setText(IPv6SubnetCalculator.dbserverInfo.Tablename.trim());
        }
        if (!IPv6SubnetCalculator.dbserverInfo.Username.equals("")) {
            this.jTextFieldUserName.setText(IPv6SubnetCalculator.dbserverInfo.Username.trim());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jButtonConnect = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButtonCancel = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jCheckBoxLaunchDBUI = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextFieldServerIP = new javax.swing.JTextField();
        jTextFieldPort = new javax.swing.JTextField();
        jTextFieldDBName = new javax.swing.JTextField();
        jTextFieldTableName = new javax.swing.JTextField();
        jTextFieldUserName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Database Connection Information");
        setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        setName("DBConnectInfo"); // NOI18N
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

        jPasswordField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordField1KeyPressed(evt);
            }
        });

        jLabel5.setText("Port:");

        jButtonConnect.setText("Connect");
        jButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectActionPerformed(evt);
            }
        });

        jLabel6.setText("Database Name:");

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jLabel7.setText("Table Name:");

        jCheckBoxLaunchDBUI.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jCheckBoxLaunchDBUI.setText("Launch DB GUI");
        jCheckBoxLaunchDBUI.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxLaunchDBUIStateChanged(evt);
            }
        });

        jLabel8.setText("Username:");

        jLabel9.setText("Password:");

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Cantarell", 0, 13)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("\n    * Please note that the database and table will be created\n    IF they do not exist. Your account must have sufficient\n    privileges on the database and table. (ps: Only MySQL_Server)");
        jTextArea1.setBorder(null);
        jScrollPane1.setViewportView(jTextArea1);

        jTextFieldServerIP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldServerIPKeyPressed(evt);
            }
        });

        jTextFieldPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldPortKeyPressed(evt);
            }
        });

        jTextFieldDBName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldDBNameKeyPressed(evt);
            }
        });

        jTextFieldTableName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldTableNameKeyPressed(evt);
            }
        });

        jTextFieldUserName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldUserNameKeyPressed(evt);
            }
        });

        jLabel4.setText("Server IP:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel5)
                            .addComponent(jCheckBoxLaunchDBUI))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldPort, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldDBName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldTableName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldUserName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonConnect)
                                .addGap(123, 123, 123)
                                .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextFieldServerIP))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldServerIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldDBName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldTableName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextFieldUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonConnect)
                    .addComponent(jButtonCancel)
                    .addComponent(jCheckBoxLaunchDBUI))
                .addGap(14, 14, 14))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectActionPerformed
        try {
            try {
                IPv6SubnetCalculator.dbserverInfo.ServerIP = InetAddress.getByName(this.jTextFieldServerIP.getText().trim());
            } catch (UnknownHostException ex) {
                MsgBox.Show(this, ex.toString(), "Error:");
                this.jTextFieldServerIP.requestFocus();
                return;
            }
            try {
                IPv6SubnetCalculator.dbserverInfo.PortNum = Integer.parseInt(this.jTextFieldPort.getText().trim());
            } catch (NumberFormatException ex) {
                MsgBox.Show(this, "Enter integer for port number\r\n" + ex.toString(), "Error:");
                this.jTextFieldPort.requestFocus();
                return;
            }
            IPv6SubnetCalculator.dbserverInfo.DBname = this.jTextFieldDBName.getText().trim();
            IPv6SubnetCalculator.dbserverInfo.Tablename = this.jTextFieldTableName.getText().trim();
            IPv6SubnetCalculator.dbserverInfo.Username = this.jTextFieldUserName.getText().trim();
            IPv6SubnetCalculator.dbserverInfo.Password = this.jPasswordField1;
        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error:");
            return;
        }

        if (this.jTextFieldServerIP.getText().trim().isEmpty()) {
            MsgBox.Show(this, "Enter MySQL server's IP Address", "Error:");
            this.jTextFieldServerIP.setText("");
            this.jTextFieldServerIP.requestFocus();
            return;
        }

        if (this.jTextFieldPort.getText().trim().isEmpty()) {
            MsgBox.Show(this, "Enter MySQL server's Port Number", "Error:");
            this.jTextFieldPort.setText("");
            this.jTextFieldPort.requestFocus();
            return;
        }
        if (this.jTextFieldDBName.getText().trim().isEmpty()) {
            MsgBox.Show(this, "Enter Database Name", "Error:");
            this.jTextFieldDBName.setText("");
            this.jTextFieldDBName.requestFocus();
            return;
        }
        if (this.jTextFieldTableName.getText().trim().isEmpty()) {
            MsgBox.Show(this, "Enter Database Table Name", "Error:");
            this.jTextFieldTableName.setText("");
            this.jTextFieldTableName.requestFocus();
            return;
        }
        if (this.jTextFieldUserName.getText().trim().isEmpty()) {
            MsgBox.Show(this, "Enter Database Username", "Error:");
            this.jTextFieldUserName.setText("");
            this.jTextFieldUserName.requestFocus();
            return;
        }

        this.isCanceled = false;
        this.dispose();
    }//GEN-LAST:event_jButtonConnectActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.isCanceled = true;
        /*IPv6SubnetCalculator.dbserverInfo.Initialize();*/
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jPasswordField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButtonConnectActionPerformed(null);
        }
    }//GEN-LAST:event_jPasswordField1KeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.isCanceled = true;
    }//GEN-LAST:event_formWindowClosing

    private void jCheckBoxLaunchDBUIStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBoxLaunchDBUIStateChanged
        if (this.jCheckBoxLaunchDBUI.isSelected())
            this.isLaunchDBUI = true;
        else
            this.isLaunchDBUI = false;
    }//GEN-LAST:event_jCheckBoxLaunchDBUIStateChanged

    private void jTextFieldUserNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldUserNameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButtonConnectActionPerformed(null);
        }
    }//GEN-LAST:event_jTextFieldUserNameKeyPressed

    private void jTextFieldTableNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldTableNameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButtonConnectActionPerformed(null);
        }
    }//GEN-LAST:event_jTextFieldTableNameKeyPressed

    private void jTextFieldDBNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldDBNameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButtonConnectActionPerformed(null);
        }
    }//GEN-LAST:event_jTextFieldDBNameKeyPressed

    private void jTextFieldPortKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPortKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButtonConnectActionPerformed(null);
        }
    }//GEN-LAST:event_jTextFieldPortKeyPressed

    private void jTextFieldServerIPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldServerIPKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButtonConnectActionPerformed(null);
        }
    }//GEN-LAST:event_jTextFieldServerIPKeyPressed

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
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonConnect;
    public javax.swing.JCheckBox jCheckBoxLaunchDBUI;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextFieldDBName;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTextField jTextFieldServerIP;
    private javax.swing.JTextField jTextFieldTableName;
    private javax.swing.JTextField jTextFieldUserName;
    // End of variables declaration//GEN-END:variables
}
