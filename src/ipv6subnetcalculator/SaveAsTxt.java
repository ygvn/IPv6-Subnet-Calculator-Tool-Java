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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Yucel Guven
 */
public final class SaveAsTxt extends javax.swing.JFrame {

    final int ID = 3; // ID of this Form.
    int incomingID;
    private static MySaveTask task;

    private java.awt.Point lastPos = new java.awt.Point();

    //
    private class MySaveTask extends SwingWorker<Boolean, Integer> {

        Long total = 0L;
        long heads = 0;

        @Override
        protected Boolean doInBackground() throws Exception {

            jButtonSave.setEnabled(false);
            jButtonCancel.setEnabled(true);
            jProgressBar1.setValue(0);
            setProgress(0);
            /////////////////////////////
            long i = 0;
            try ( FileWriter fileWriter = new FileWriter(file)) {

                if (StartEnd.Start.compareTo(StartEnd.UpperLimitAddress) == 0) {
                    return false;
                }

                howmany = ToIndex - FromIndex + 1;
                int perc = 0;
                count = 0;
                TotalBytes = BigInteger.ZERO;
                StartEnd.subnetidx = BigInteger.valueOf(FromIndex);
                String ss = "", se = "";

                if (incomingID == 1) {
                    if (!is128Checked) {
                        StartEnd.subnetslash = 64;
                    } else if (is128Checked) {
                        StartEnd.subnetslash = 128;
                    }
                }
                StartEnd = v6ST.GoToSubnet(StartEnd, is128Checked);
                for (i = 1; i <= howmany; i++) {
                    if (isCancelled()) {
                        count--;
                        publish((int) i);
                        break;
                    } else {
                        StartEnd = v6ST.Subnetting(StartEnd, is128Checked);
                        if (!is128Checked) {
                            if (incomingID == 0 || incomingID == 1) {
                                ss = v6ST.Kolonlar(StartEnd.Start);
                                ss = ss.substring(0, 19);
                                ss += "::";
                                ss = v6ST.CompressAddress(ss);
                                if (selectedRange) {
                                    ss = "p" + StartEnd.subnetidx + "> " + ss + "/"
                                            + input_subnetslash;
                                } else {
                                    ss = "p" + StartEnd.subnetidx + "> " + ss + "/"
                                            + StartEnd.subnetslash;
                                }

                                fileWriter.write(ss + "\r\n");
                                TotalBytes = TotalBytes.add(BigInteger.valueOf(ss.length() + 2));
                                saveState.TotalBytes = TotalBytes;
                                //
                                if (StartEnd.subnetslash != 64) {
                                    if (jCheckBoxEndAddr.isSelected()) {
                                        se = v6ST.Kolonlar(StartEnd.End);
                                        se = se.substring(0, 19);
                                        se += "::";
                                        se = v6ST.CompressAddress(se);
                                        if (selectedRange) {
                                            se = "e" + StartEnd.subnetidx + "> " + se + "/"
                                                    + input_subnetslash;
                                        } else {
                                            se = "e" + StartEnd.subnetidx + "> " + se + "/"
                                                    + StartEnd.subnetslash;
                                        }
                                        fileWriter.write(se + "\r\n");
                                        fileWriter.write("\r\n");
                                        TotalBytes = TotalBytes.add(BigInteger.valueOf(se.length() + 4));
                                        saveState.TotalBytes = TotalBytes;
                                    }
                                }
                            } else if (incomingID == 2) {
                                String[] sa;
                                int spaces = 0;

                                sa = v6ST.DnsRev(StartEnd.Start, StartEnd.subnetslash, is128Checked);
                                sa[0] = "p" + StartEnd.subnetidx + "> " + sa[0];
                                spaces = sa[0].split(" ")[0].length() + 1;

                                for (int n = 0; n < 8; n++) {
                                    if (sa[n] == null) {
                                        break;
                                    }
                                    if (n > 0) {
                                        sa[n] = String.format("%1$" + Integer.valueOf(sa[n].length() + spaces) + "s", sa[n]);
                                    }
                                    TotalBytes = TotalBytes.add(BigInteger.valueOf(sa[n].length() + 2));
                                    saveState.TotalBytes = TotalBytes;
                                    fileWriter.write(sa[n] + "\r\n");
                                }
                            }
                        } else if (is128Checked) {
                            if (incomingID == 0 || incomingID == 1) {
                                ss = v6ST.Kolonlar(StartEnd.Start);
                                ss = v6ST.CompressAddress(ss);
                                if (selectedRange) {
                                    ss = "p" + StartEnd.subnetidx + "> " + ss + "/"
                                            + input_subnetslash;
                                } else {
                                    ss = "p" + StartEnd.subnetidx + "> " + ss + "/"
                                            + StartEnd.subnetslash;
                                }
                                TotalBytes = TotalBytes.add(BigInteger.valueOf(ss.length() + 2));
                                saveState.TotalBytes = TotalBytes;
                                fileWriter.write(ss + "\r\n");
                                //
                                if (StartEnd.subnetslash != 128) {
                                    if (jCheckBoxEndAddr.isSelected()) {
                                        se = v6ST.Kolonlar(StartEnd.End);
                                        se = v6ST.CompressAddress(se);
                                        if (selectedRange) {
                                            se = "e" + StartEnd.subnetidx + "> " + se + "/"
                                                    + input_subnetslash;
                                        } else {
                                            se = "e" + StartEnd.subnetidx + "> " + se + "/"
                                                    + StartEnd.subnetslash;
                                        }
                                        fileWriter.write(se + "\r\n");
                                        fileWriter.write("\r\n");
                                        TotalBytes = TotalBytes.add(BigInteger.valueOf(se.length() + 4));
                                        saveState.TotalBytes = TotalBytes;
                                    }
                                }

                            } else if (incomingID == 2) {
                                String[] sa;
                                int spaces = 0;

                                sa = v6ST.DnsRev(StartEnd.Start, StartEnd.subnetslash, is128Checked);
                                sa[0] = "s" + StartEnd.subnetidx + "> " + sa[0];
                                spaces = sa[0].split(" ")[0].length() + 1;

                                for (int n = 0; n < 8; n++) {
                                    if (sa[n] == null) {
                                        break;
                                    }
                                    if (n > 0) {
                                        sa[n] = String.format("%1$" + Integer.valueOf(sa[n].length() + spaces) + "s", sa[n]);
                                    }

                                    TotalBytes = TotalBytes.add(BigInteger.valueOf(sa[n].length() + 2));
                                    saveState.TotalBytes = TotalBytes;
                                    fileWriter.write(sa[n] + "\r\n");
                                }
                            }
                        }
                        perc = (int) (i * 100 / howmany);
                        saveState.SavedLines = BigInteger.valueOf(count);
                        saveState.percentage = perc;

                        publish(perc);

                        if (StartEnd.Start.equals(StartEnd.UpperLimitAddress)
                                || StartEnd.subnetidx.equals(maxsubnet)) {
                            break;
                        }
                        if (is128Checked) {
                            StartEnd.Start = StartEnd.End.add(BigInteger.ONE);
                        } else if (!is128Checked) {
                            StartEnd.Start = StartEnd.End.add(BigInteger.ONE.shiftLeft(64));
                        }

                    }
                    count++;
                }
                saveState.SavedLines = BigInteger.valueOf(count);
                saveState.percentage = perc;
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(SaveAsTxt.class.getName()).log(Level.SEVERE, null, ex);

                MsgBox.Show(null, ex.getMessage(), "Exception");
            }
            return true;
        }

        @Override
        protected void process(List<Integer> chunks) {

            Integer i = chunks.get(chunks.size() - 1);
            jProgressBar1.setValue(i);

            jLabelSavingNo.setText(saveState.SavedLines.toString());
            jLabelStatus.setText(saveState.TotalBytes.toString() + " Bytes saved.");
        }

        @Override
        protected void done() {
            jButtonSave.setEnabled(true);
            jButtonCancel.setEnabled(false);
        }
    }

    public class CurrentState {

        public BigInteger SavedLines = BigInteger.ZERO;
        public BigInteger TotalBytes = BigInteger.ZERO;
        public int percentage = 0;
    }

    //<editor-fold defaultstate="collapsed" desc="special initials/constants -yucel">
    CurrentState saveState = new CurrentState();

    String FileName = "";
    String ss = "", se = "";
    //
    public BigInteger maxsubnet = BigInteger.ZERO;
    SEaddress StartEnd = new SEaddress();

    long FromIndex = 0;
    long ToIndex = 0;
    BigInteger TotalBytes = BigInteger.ZERO;

    Boolean is128Checked;
    File file;
    Boolean selectedRange = false;
    int input_subnetslash = 0;

    long count = 0;
    long howmany = 0;
    //</editor-fold>

    public void SetLAF(LookAndFeel laf) {
        try {
            javax.swing.UIManager.setLookAndFeel(laf);

            SaveAsTxt.setDefaultLookAndFeelDecorated(true);
            SwingUtilities.updateComponentTreeUI(this);
            this.pack();
            this.validate();

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SaveAsTxt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            MsgBox.Show(this, ex.getMessage(), "Look&Feel Exception");
        }
    }

    public void EscapeKey() {
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
                jButtonCancelActionPerformed(null);
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
     * Creates new form SaveAsTxt
     *
     * @param input
     * @param is128Checked
     * @param selectedRange
     */
    public SaveAsTxt(SEaddress input, Boolean is128Checked, Boolean selectedRange) {
        initComponents();
        //
        this.selectedRange = selectedRange;
        this.input_subnetslash = input.subnetslash;
        this.StartEnd.ID = this.ID;

        IPv6SubnetCalculator.AddWindowItem(this.getTitle(), (JFrame) SwingUtilities.getRoot(this), this.hashCode());
        EscapeKey();
        //
        SetNewValues(input, is128Checked);
        //
        if ((!is128Checked && input.subnetslash == 64)
                || (is128Checked && input.subnetslash == 128)
                || input.ID == 1
                || input.ID == 2) {
            this.jCheckBoxEndAddr.setSelected(false);
            this.jCheckBoxEndAddr.setEnabled(false);
        } else {
            this.jCheckBoxEndAddr.setEnabled(true);
        }

        if (input.ID == 0 || input.ID == 1) {
            this.jLabel2.setText("[Prefixes]");
        } else if (input.ID == 2) {
            this.jLabel2.setText("[Reverse DNS]");
        }
    }

    /**
     *
     * @param input
     * @param is128Checked
     */
    public void SetNewValues(SEaddress input, Boolean is128Checked) {
        this.incomingID = input.ID;
        this.is128Checked = is128Checked;
        this.is128Checked = is128Checked;
        this.StartEnd.LowerLimitAddress = input.LowerLimitAddress;
        this.StartEnd.Resultv6 = input.Resultv6;
        this.StartEnd.slash = input.slash;
        this.StartEnd.Start = input.Start;
        this.StartEnd.End = input.End;
        this.StartEnd.subnetidx = input.subnetidx;
        this.StartEnd.subnetslash = input.subnetslash;
        this.StartEnd.UpperLimitAddress = input.UpperLimitAddress;
        this.StartEnd.upto = input.upto;
        //
        ss = v6ST.Kolonlar(this.StartEnd.Start);
        se = v6ST.Kolonlar(this.StartEnd.End);

        this.jProgressBar1.setValue(0);
        //

        if (!this.is128Checked) {
            this.jLabelStart.setText("s> " + ss.substring(0, 19) + "::" + "/" + this.StartEnd.subnetslash);
            this.jLabelEnd.setText("e> " + se.substring(0, 19) + "::" + "/" + this.StartEnd.subnetslash);
        } else if (this.is128Checked) {
            this.jLabelStart.setText("s> " + ss + "/" + this.StartEnd.subnetslash);
            this.jLabelEnd.setText("e> " + se + "/" + this.StartEnd.subnetslash);
        }

        if (input.ID == 0 || input.ID == 2) {
            this.maxsubnet = BigInteger.ONE.shiftLeft(this.StartEnd.subnetslash - this.StartEnd.slash);
        } else if (input.ID == 1) {
            if (!this.is128Checked) {
                this.maxsubnet = BigInteger.ONE.shiftLeft(64 - this.StartEnd.subnetslash);
            } else if (this.is128Checked) {
                this.maxsubnet = BigInteger.ONE.shiftLeft(128 - this.StartEnd.subnetslash);
            }
        } else {
            return;
        }
        this.maxsubnet = this.maxsubnet.subtract(BigInteger.ONE);
        this.jTextFieldMaxIndex.setText(String.valueOf(this.maxsubnet));
        //
        if ((!is128Checked && input.subnetslash == 64)
                || (is128Checked && input.subnetslash == 128)
                || input.ID == 1
                || input.ID == 2) {
            this.jCheckBoxEndAddr.setSelected(false);
            this.jCheckBoxEndAddr.setEnabled(false);
        } else {
            this.jCheckBoxEndAddr.setEnabled(true);
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

        jCheckBoxEndAddr = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelStart = new javax.swing.JLabel();
        jLabelEnd = new javax.swing.JLabel();
        jTextFieldMaxIndex = new javax.swing.JTextField();
        jTextFieldFromIndex = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldToIndex = new javax.swing.JTextField();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jLabelSavingNo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Save As Text");
        setName("SaveAsText"); // NOI18N
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

        jCheckBoxEndAddr.setText("Save with Prefix End Addresses?");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ipv6subnetcalculator/saveas.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Cantarell", 2, 14)); // NOI18N
        jLabel2.setText("[Prefixes]");

        jLabel3.setText("Range:");

        jLabelStart.setText("s> jLabelStart");

        jLabelEnd.setText("e> jLabelEnd");

        jTextFieldMaxIndex.setEditable(false);
        jTextFieldMaxIndex.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        jTextFieldMaxIndex.setText("maxIndex");
        jTextFieldMaxIndex.setToolTipText("Index number starts from zero");

        jTextFieldFromIndex.setText("0");
        jTextFieldFromIndex.setToolTipText("Index number starts from zero");

        jLabel6.setText("Max.Index#:");

        jLabel7.setText("From Index:");

        jLabel8.setText("To Index:");

        jTextFieldToIndex.setText("0");
        jTextFieldToIndex.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldToIndexKeyPressed(evt);
            }
        });

        jButtonSave.setText("Save");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.setEnabled(false);
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jProgressBar1.setStringPainted(true);

        jLabel9.setText("SavedIndex:");

        jLabel10.setText("Status:");

        jLabelStatus.setText("_");

        jLabelSavingNo.setText("_");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldMaxIndex)
                            .addComponent(jTextFieldFromIndex)
                            .addComponent(jTextFieldToIndex)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabelStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabelStart, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelEnd, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBoxEndAddr, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(130, 130, 130)
                                        .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabelSavingNo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabelStart))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelEnd)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMaxIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldFromIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldToIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxEndAddr)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabelSavingNo))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabelStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSave)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonExit))
                .addGap(20, 20, 20))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonCancelActionPerformed(null);
        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
        this.dispose();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        try {
            FromIndex = Long.valueOf(jTextFieldFromIndex.getText());
            ToIndex = Long.valueOf(jTextFieldToIndex.getText());
        } catch (NumberFormatException ex) {
            MsgBox.Show(this, ex.getMessage() + "\r\n"
                    + "Max.Value is: 9223372036854775806",
                    "Error");
            return;
        }
        long maxsave = 0x7fffffffffffffffL;

        if (FromIndex > maxsave || ToIndex > maxsave) {
            MsgBox.Show(this, "Greater than maximum value!\r\n"
                    + "Try to save part by part.\r\n"
                    + "(Max. value for saving is: 9223372036854775806)",
                    "Error");
            return;
        } else if (FromIndex > ToIndex) {
            MsgBox.Show(this, "FromIndex can not be greater than ToIndex.", "Error");
            return;
        } else if (maxsubnet.compareTo(BigInteger.valueOf(maxsave)) < 0) {
            if (FromIndex > maxsubnet.longValueExact()
                    || ToIndex > maxsubnet.longValueExact()) {
                MsgBox.Show(this, "FromIndex or ToIndex can not be greater than Max.Index#.", "Error");
                return;
            }
        }
        //
        StartEnd.subnetidx = BigInteger.valueOf(FromIndex);
        TotalBytes = BigInteger.ZERO;
        StartEnd.Start = StartEnd.LowerLimitAddress;
        StartEnd.End = StartEnd.UpperLimitAddress;

        BigInteger OnceTotalBytes = BigInteger.ZERO;
        BigInteger OnceDnsTotalBytes = BigInteger.ZERO;
        //
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Text files (*.txt)", "txt");
        fileChooser.setFileFilter(filter);

        String fname = "";
        if (incomingID == 0 || incomingID == 1) {
            fname = (ss.replaceAll(":", "")
                    + "_prefix" + StartEnd.slash
                    + "to" + StartEnd.subnetslash + "_index"
                    + FromIndex + "to" + ToIndex);
        } else if (incomingID == 2) {
            OnceTotalBytes = OnceDnsTotalBytes;
            fname = ("ReverseDNS_"
                    + ss.replaceAll(":", "")
                    + "_prefix" + StartEnd.subnetslash + "_index"
                    + FromIndex + "to" + ToIndex);
        }
        File f = new File(fname + ".txt");
        fileChooser.setSelectedFile(f);

        if (fileChooser.showSaveDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            if (file != null) {
                task = new MySaveTask();
                task.execute();
            }
        }
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        if (task != null) {
            task.cancel(true);
            this.jButtonSave.setEnabled(true);
            this.jButtonCancel.setEnabled(false);
        }
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        jButtonCancelActionPerformed(null);
        IPv6SubnetCalculator.RemoveWindowItem(this.hashCode());
    }//GEN-LAST:event_formWindowClosing

    private void jTextFieldToIndexKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldToIndexKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.jButtonSaveActionPerformed(null);
        }
    }//GEN-LAST:event_jTextFieldToIndexKeyPressed

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
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JCheckBox jCheckBoxEndAddr;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelEnd;
    private javax.swing.JLabel jLabelSavingNo;
    private javax.swing.JLabel jLabelStart;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JTextField jTextFieldFromIndex;
    private javax.swing.JTextField jTextFieldMaxIndex;
    private javax.swing.JTextField jTextFieldToIndex;
    // End of variables declaration//GEN-END:variables
}
