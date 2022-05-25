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
import java.math.BigInteger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author Yucel Guven
 */
public class Goto extends javax.swing.JDialog {

    int ID, idx;

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
     * Creates new form Goto
     *
     * @param parent
     * @param modal
     * @param ID
     * @param idx
     */
    public Goto(java.awt.Frame parent, boolean modal, int ID, int idx) {
        super(parent, modal);
        initComponents();

        this.ID = ID;
        this.idx = idx;

        EscapeKey();

        if (ID == 0 && idx == 0) {
            this.jLabelinput.setText("Address Space (Global Routing Prefix) Number:");
            this.jTextFieldMax.setText(String.valueOf(IPv6SubnetCalculator.asnmax.subtract(BigInteger.ONE)));
        } else if (ID == 0 && idx == 1) {
            this.jLabelinput.setText("Prefix Number:");
            this.jTextFieldMax.setText(String.valueOf(IPv6SubnetCalculator.prefixmax.subtract(BigInteger.ONE)));
        } else if (ID == 0 && idx == 2) {
            this.jLabelinput.setText("Search Prefix: (Please enter the prefix without slash '/')");
            this.jTextFieldMax.setText("");
        } else if (ID == 1 && idx == 0) {

        }
        if (ID == 0) {
            if (idx == 0) {
                this.jTextFieldGotoAddr.setText(String.valueOf(IPv6SubnetCalculator.gotoasnValue));
            } else if (idx == 1) {
                this.jTextFieldGotoAddr.setText(String.valueOf(IPv6SubnetCalculator.gotopfxValue));
            } else if (idx == 2) {
                this.jTextFieldGotoAddr.setText(IPv6SubnetCalculator.findpfx);
            }
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

        jLabelinput = new javax.swing.JLabel();
        jTextFieldGotoAddr = new javax.swing.JTextField();
        jTextFieldMax = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButtonGo = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Go to");
        setName("GoTo"); // NOI18N
        setResizable(false);

        jLabelinput.setForeground(new java.awt.Color(9, 160, 254));
        jLabelinput.setText("Address Space (Global Routing Prefix) Number:");

        jTextFieldGotoAddr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldGotoAddrKeyPressed(evt);
            }
        });

        jTextFieldMax.setEditable(false);
        jTextFieldMax.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        jTextFieldMax.setForeground(new java.awt.Color(9, 160, 254));
        jTextFieldMax.setText("max");
        jTextFieldMax.setName("Go to"); // NOI18N

        jLabel1.setForeground(new java.awt.Color(9, 160, 254));
        jLabel1.setText("Max:");

        jButtonGo.setText("Go");
        jButtonGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGoActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabelinput)
                        .addComponent(jTextFieldGotoAddr)
                        .addComponent(jTextFieldMax, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jButtonGo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelinput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldGotoAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonGo)
                    .addComponent(jButtonCancel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGoActionPerformed
        if (this.jTextFieldGotoAddr.getText().trim().equals("")) {
            return;
        }
        this.jLabelinput.setText("");
        if (ID == 0) {
            if (idx == 0) {
                IPv6SubnetCalculator.gotoasnValue
                        = new BigInteger(this.jTextFieldGotoAddr.getText().trim());

                if (IPv6SubnetCalculator.gotoasnValue.compareTo(IPv6SubnetCalculator.asnmax.subtract(BigInteger.ONE)) > 0) {
                    this.jLabelinput.setText("Max. exceeded!");
                    return;
                }
            } else if (idx == 1) {
                IPv6SubnetCalculator.gotopfxValue
                        = new BigInteger(this.jTextFieldGotoAddr.getText().trim());
                if (IPv6SubnetCalculator.gotopfxValue.compareTo(IPv6SubnetCalculator.prefixmax.subtract(BigInteger.ONE)) > 0) {
                    this.jLabelinput.setText("Max. exceeded!");
                    return;
                }
            } else if (idx == 2) {
                if (v6ST.IsAddressCorrect(this.jTextFieldGotoAddr.getText().trim())) {
                    IPv6SubnetCalculator.findpfx = this.jTextFieldGotoAddr.getText().trim();
                } else {
                    this.jLabelinput.setText(v6ST.errmsg);
                    return;
                }
            }
            dispose();
        }
    }//GEN-LAST:event_jButtonGoActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        if (ID == 0 && idx == 2) {
            IPv6SubnetCalculator.findpfx = "";
        }
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jTextFieldGotoAddrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldGotoAddrKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.jButtonGoActionPerformed(null);
        }
    }//GEN-LAST:event_jTextFieldGotoAddrKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonGo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelinput;
    private javax.swing.JTextField jTextFieldGotoAddr;
    private javax.swing.JTextField jTextFieldMax;
    // End of variables declaration//GEN-END:variables
}
