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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Yucel Guven
 */
public final class ServiceNamesPortNumbers extends javax.swing.JFrame {

    public static final String XmlFileName = "service-names-port-numbers.xml"; // default file name from IANA web page
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

    public void FillTable() {
        File xmlFile = new File(XmlFileName);

        if (xmlFile.exists() && !xmlFile.isDirectory()) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(ServiceNamesPortNumbers.class.getName()).log(Level.SEVERE, null, ex);
            }
            Document doc = null;
            try {
                doc = documentBuilder.parse(xmlFile);
            } catch (IOException | SAXException ex) {
                Logger.getLogger(ServiceNamesPortNumbers.class.getName()).log(Level.SEVERE, null, ex);
            }
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("record");
            int total = nodeList.getLength();
            jLabel2.setText(String.valueOf(total));

            DefaultTableModel tmodel = (DefaultTableModel) this.jTable1.getModel();
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            this.jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            this.jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            this.jTable1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            this.jTable1.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            //
            DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
            leftRenderer.setHorizontalAlignment(JLabel.LEFT);
            this.jTable1.getColumnModel().getColumn(4).setCellRenderer(leftRenderer);
            //
            TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(this.jTable1.getModel());
            this.jTable1.setRowSorter(rowSorter);

            this.jTextField1.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent de) {
                    jTextField2.setText("");
                    String text = jTextField1.getText().trim();

                    if (text.trim().length() == 0) {
                        rowSorter.setRowFilter(null);
                        jLabel2.setText(String.valueOf(total));
                    } else {
                        //rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text + "$"));
                        if (jCheckBox1.isSelected()) {
                            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
                        } else {
                            rowSorter.setRowFilter(RowFilter.regexFilter("^" + text + ".*", 1));
                        }
                        jLabel2.setText(String.valueOf(jTable1.getRowSorter().getViewRowCount()));
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    String text = jTextField1.getText().trim();

                    if (text.trim().length() == 0) {
                        rowSorter.setRowFilter(null);
                        jLabel2.setText(String.valueOf(total));
                    } else {
                        if (jCheckBox1.isSelected()) {
                            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
                        } else {
                            rowSorter.setRowFilter(RowFilter.regexFilter("^" + text + ".*", 1));
                        }
                        jLabel2.setText(String.valueOf(jTable1.getRowSorter().getViewRowCount()));
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                }
            });
            //
            this.jTextField2.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent de) {
                    jTextField1.setText("");
                    String text = jTextField2.getText().trim();

                    if (text.trim().length() == 0) {
                        rowSorter.setRowFilter(null);
                        jLabel2.setText(String.valueOf(total));
                    } else {
                        if (jCheckBox1.isSelected()) {
                            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 2));
                        } else {
                            rowSorter.setRowFilter(RowFilter.regexFilter("^" + text + ".*", 2));
                        }
                        jLabel2.setText(String.valueOf(jTable1.getRowSorter().getViewRowCount()));
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    String text = jTextField2.getText().trim();

                    if (text.trim().length() == 0) {
                        rowSorter.setRowFilter(null);
                        jLabel2.setText(String.valueOf(total));
                    } else {
                        if (jCheckBox1.isSelected()) {
                            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 2));
                        } else {
                            rowSorter.setRowFilter(RowFilter.regexFilter("^" + text + ".*", 2));
                        }
                        jLabel2.setText(String.valueOf(jTable1.getRowSorter().getViewRowCount()));
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                }
            });
            //
            this.jCheckBox1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (jTextField1.getText().trim().equals("")) {
                        String text = jTextField2.getText();

                        if (text.trim().length() == 0) {
                            rowSorter.setRowFilter(null);
                            jLabel2.setText(String.valueOf(total));
                        } else {
                            if (jCheckBox1.isSelected()) {
                                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 2));
                            } else {
                                rowSorter.setRowFilter(RowFilter.regexFilter("^" + text + ".*", 2));
                            }
                            jLabel2.setText(String.valueOf(jTable1.getRowSorter().getViewRowCount()));
                        }

                    } else {
                        String text = jTextField1.getText().trim();

                        if (text.trim().length() == 0) {
                            rowSorter.setRowFilter(null);
                            jLabel2.setText(String.valueOf(total));
                        } else {
                            if (jCheckBox1.isSelected()) {
                                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
                            } else {
                                rowSorter.setRowFilter(RowFilter.regexFilter("^" + text + ".*", 1));
                            }
                            jLabel2.setText(String.valueOf(jTable1.getRowSorter().getViewRowCount()));
                        }

                    }
                }
            });
            //
            for (int i = 0; i < total; i++) {
                Node root = nodeList.item(i);
                if (root.getNodeType() == Node.ELEMENT_NODE) {
                    ArrayList<String> tmplist = new ArrayList<>();
                    tmplist.add(String.valueOf(i + 1));

                    Element child = (Element) root;
                    try {
                        if (child.getElementsByTagName("name").item(0) != null) {
                            String name = child.getElementsByTagName("name").item(0).getTextContent().trim();
                            tmplist.add(name);
                        } else {
                            tmplist.add(" ");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(ServiceNamesPortNumbers.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        if (child.getElementsByTagName("number").item(0) != null) {
                            String number = child.getElementsByTagName("number").item(0).getTextContent().trim();
                            tmplist.add(number);
                        } else {
                            tmplist.add(" ");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(ServiceNamesPortNumbers.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        if (child.getElementsByTagName("protocol").item(0) != null) {
                            String protocol = child.getElementsByTagName("protocol").item(0).getTextContent().trim();
                            tmplist.add(protocol);
                        } else {
                            tmplist.add(" ");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(ServiceNamesPortNumbers.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        if (child.getElementsByTagName("description").item(0) != null) {
                            String description = child.getElementsByTagName("description").item(0).getTextContent().trim();
                            tmplist.add(description);
                        } else {
                            tmplist.add(" ");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(ServiceNamesPortNumbers.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tmodel.addRow(tmplist.toArray());
                }
            }
        } else {
            MsgBox.Show(this, "XML file not found.\r\nYou can download IANA XML file from Update menu", "XMLFile not found");
        }
    }

    /**
     * Creates new form ServiceNamesPortNumbers
     */
    public ServiceNamesPortNumbers() {
        initComponents();
        //
        this.jTable1.requestFocus();
        IPv6SubnetCalculator.AddWindowItem(this.getTitle(), (JFrame) SwingUtilities.getRoot(this), this.hashCode());
        EscapeKey();

        FillTable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuUpdate = new javax.swing.JMenu();
        jMenuItemUpdatefromIANA = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Service Names Port Numbers");
        setName("ServiceNamesPortNumbers"); // NOI18N
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

        jCheckBox1.setText("including");

        jLabel1.setText("Search:");

        jTextField1.setToolTipText("Search with Service Name");

        jTextField2.setToolTipText("Search with PortNumber");

        jTable1.setFont(new java.awt.Font("Cantarell", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "ServiceName", "PortNumber", "Protocol", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setDoubleBuffered(true);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(60);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(60);
            jTable1.getColumnModel().getColumn(1).setMinWidth(150);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(1).setMaxWidth(150);
            jTable1.getColumnModel().getColumn(2).setMinWidth(150);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(2).setMaxWidth(150);
            jTable1.getColumnModel().getColumn(3).setMinWidth(90);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(90);
            jTable1.getColumnModel().getColumn(3).setMaxWidth(90);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(9, 160, 254)));
        jPanel1.setDoubleBuffered(false);

        jLabel2.setFont(new java.awt.Font("Cantarell", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(9, 160, 254));
        jLabel2.setText("          ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel2))
        );

        jMenuFile.setMnemonic('F');
        jMenuFile.setText("File");

        jMenuItemExit.setMnemonic('x');
        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar1.add(jMenuFile);

        jMenuUpdate.setMnemonic('U');
        jMenuUpdate.setText("Update");

        jMenuItemUpdatefromIANA.setMnemonic('U');
        jMenuItemUpdatefromIANA.setText("Update from IANA");
        jMenuItemUpdatefromIANA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUpdatefromIANAActionPerformed(evt);
            }
        });
        jMenuUpdate.add(jMenuItemUpdatefromIANA);

        jMenuBar1.add(jMenuUpdate);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
        this.dispose();
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemUpdatefromIANAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUpdatefromIANAActionPerformed
        UpdateServiceNamesPortNumbers update = new UpdateServiceNamesPortNumbers(this, Boolean.TRUE, XmlFileName);
        update.setVisible(true);
    }//GEN-LAST:event_jMenuItemUpdatefromIANAActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
    }//GEN-LAST:event_formWindowClosing

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
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemUpdatefromIANA;
    private javax.swing.JMenu jMenuUpdate;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
