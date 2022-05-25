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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Yucel Guven
 */
public final class IPv6SubnetCalculator extends javax.swing.JFrame {

    private java.awt.Dimension dim = null;
    private java.awt.Point lastPos = new java.awt.Point();

    //<editor-fold defaultstate="collapsed" desc="special initials/constants -yucel">

    /*  /64max = 18446744073709551615
        /128max= 340282366920938463463374607431768211455
     */
    final int ID = 0; // ID of this Form.
    int incomingID;

    private static List<ListSubnetRange> listSubnetRange = new ArrayList<ListSubnetRange>();
    ListDnsReverses listDnsr = null;
    SaveAsTxt saveTxt = null;
    WhoisQuery whoisquery = null;
    CompressAddress compressAddr = null;
    ASNumberPlainDot asnpldot = null;
    ServiceNamesPortNumbers snamepnumbers = null;
    //
    public static List<ActiveWindow> windowList = new ArrayList<ActiveWindow>();
    //
    SEaddress StartEnd = new SEaddress();
    SEaddress subnets = new SEaddress();
    SEaddress page = new SEaddress();
    final int upto = 128;
    //
    public static BigInteger gotoasnValue = BigInteger.ZERO;
    public static BigInteger gotopfxValue = BigInteger.ZERO;
    public static String findpfx = "";

    public static BigInteger prefixmax = BigInteger.ZERO;
    public static BigInteger asnmax = BigInteger.ZERO;
    public static int sd1Value = 0, sd2Value = 0;
    //
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    //BigInteger lastindex = BigInteger.ZERO;

    String prefixColor = "#ff0000"; //Default prefixColor: red; 
    String subnetColor = "#00ff00"; //Default subnetColor: green; 

    //DataBase
    public static Connection MySQLconnection = null;
    public static DBServerInfo dbserverInfo = new DBServerInfo();
    private static Statement statement = null;
    private static ResultSet resultSet = null;

    public static List<GetPrefixInfoFromDB> getPrefixInfo = new ArrayList<GetPrefixInfoFromDB>();
    public static List<DatabaseUI> dbUI = new ArrayList<DatabaseUI>();
    public static List<ListAssignedfromDB> listAssignedfromdb = new ArrayList<ListAssignedfromDB>();
    public static List<ListParentNets> listParentNets = new ArrayList<ListParentNets>();
    public static List<PrefixSubLevels> prefixSublevels = new ArrayList<PrefixSubLevels>();
    public static List<StatsUsage> statsUsage = new ArrayList<StatsUsage>();
    //GetPrefixInfoFromDB getpfxdbinfo = null;
    //
    public static final String xmlFilename = "IPv6SubnetCalculatorInfo.xml";
    public XMLinfo xmlinfo = new XMLinfo();
    //
    /**
     * To reach&modify from other classes, jMenuWin defined as static.
     */
    private static javax.swing.JMenu jMenuWindow;
    private static javax.swing.JMenuItem jMenuItemWindowCloseAll;
    //</editor-fold>

    /**
     * Creates new form IPv6SubnetCalculator.
     */
    public IPv6SubnetCalculator() {
        initComponents();
        //
        this.StartEnd.ID = ID;
        this.xmlinfo.Initialize();
        AddJMenuWindow();

        this.lastPos.setLocation(this.getLocation());
        this.dim = this.getSize();
        this.setPreferredSize(this.dim);

        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            this.setMinimumSize(new java.awt.Dimension(640, 591));
        } else {
            this.setMinimumSize(new java.awt.Dimension(586, 591));
        }

        this.jProgressBar1.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        EscapeKey();
        ReadInfoXML();

        this.jButtonCalculateActionPerformed(null);
        this.jButtonCalculate.requestFocus();

    }

    public void ReadInfoXML() {

        /* Read Info from XML file. This file is used like Registry. 
         * Reading and Writing ORDER is IMPORTANT!
         */
        File xmlFile = new File(xmlFilename);
        if (xmlFile.exists() && !xmlFile.isDirectory()) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                MsgBox.Show(this, ex.toString(), "Exception");
            }
            Document doc = null;
            try {
                doc = documentBuilder.parse(xmlFile);
            } catch (SAXException | IOException ex) {
                Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                MsgBox.Show(this, ex.toString(), "Exception");
            }
            doc.getDocumentElement().normalize();

            // UserInteface UI:
            NodeList ui_nodeList = doc.getElementsByTagName("UserInterface");
            for (int i = 0; i < ui_nodeList.getLength(); i++) {
                Node root = ui_nodeList.item(i);
                if (root.getNodeType() == Node.ELEMENT_NODE) {
                    Element child = (Element) root;

                    try {
                        if (child.getElementsByTagName("prefixColor").item(0) != null) {
                            this.xmlinfo.prefixColor = child.getElementsByTagName("prefixColor").item(0).getTextContent().trim();
                            this.prefixColor = this.xmlinfo.prefixColor;
                            if (this.xmlinfo.prefixColor.length() == 0) {
                                this.xmlinfo.prefixColor = "#ff0000";
                                this.prefixColor = this.xmlinfo.prefixColor;
                            }
                        } else {
                            MsgBox.Show(this, "Can not get <prefixColor> from XML file", "XMLReadError");
                            this.xmlinfo.prefixColor = "#ff0000"; // red as default
                            this.prefixColor = this.xmlinfo.prefixColor;
                        }
                        if (child.getElementsByTagName("subnetColor").item(0) != null) {
                            this.xmlinfo.subnetColor = child.getElementsByTagName("subnetColor").item(0).getTextContent().trim();
                            this.subnetColor = this.xmlinfo.subnetColor;
                            if (this.xmlinfo.subnetColor.length() == 0) {
                                this.xmlinfo.subnetColor = "#00ff00";
                                this.subnetColor = this.xmlinfo.subnetColor;
                            }
                        } else {
                            MsgBox.Show(this, "Can not get <subnetColor> from XML file", "XMLReadError");
                            this.xmlinfo.subnetColor = "#00ff00";
                            this.subnetColor = this.xmlinfo.subnetColor;
                        }

                        if (child.getElementsByTagName("address").item(0) != null) {
                            this.xmlinfo.address = child.getElementsByTagName("address").item(0).getTextContent().trim();
                            this.jTextFieldInputAddress.setText(this.xmlinfo.address);
                        } else {
                            MsgBox.Show(this, "Can not get <address> from XML file", "XMLReadError");
                            this.jTextFieldInputAddress.setText("2001:db8:abcd:1234::"); // as default
                        }
                        if (child.getElementsByTagName("prefixValue").item(0) != null) {
                            this.xmlinfo.prefixValue = Short.valueOf(child.getElementsByTagName("prefixValue").item(0).getTextContent().trim());
                            this.jSliderPrefix.setValue(this.xmlinfo.prefixValue);
                        } else {
                            MsgBox.Show(this, "Can not get <prefixValue> from XML file", "XMLReadError");
                        }

                        if (child.getElementsByTagName("checkSubnet").item(0) != null) {
                            this.xmlinfo.checkSubnet = Boolean.valueOf(child.getElementsByTagName("checkSubnet").item(0).getTextContent().trim());
                            if (this.xmlinfo.checkSubnet) {
                                this.jCheckBoxSubnet.setSelected(true);
                                this.jCheckBoxSubnetActionPerformed(null);
                            }
                        } else {
                            MsgBox.Show(this, "Can not get <checkSubnet> from XML file", "XMLReadError");
                        }
                        if (child.getElementsByTagName("check128bit").item(0) != null) {
                            this.xmlinfo.check128bit = Boolean.valueOf(child.getElementsByTagName("check128bit").item(0).getTextContent().trim());
                            if (this.xmlinfo.check128bit) {
                                this.jCheckBox128bits.setSelected(true);
                                this.jCheckBox128bitsActionPerformed(null);
                            }
                        } else {
                            MsgBox.Show(this, "Can not get <check128bit> from XML file", "XMLReadError");
                        }
                        if (child.getElementsByTagName("checkEnd").item(0) != null) {
                            this.xmlinfo.checkEnd = Boolean.valueOf(child.getElementsByTagName("checkEnd").item(0).getTextContent().trim());
                            if (this.xmlinfo.checkEnd) {
                                this.jCheckBoxEnd.setSelected(true);
                                this.jCheckBoxEndActionPerformed(null);
                            }
                        } else {
                            MsgBox.Show(this, "Can not get <checkEnd> from XML file", "XMLReadError");
                        }
                        if (child.getElementsByTagName("subnetValue").item(0) != null) {
                            this.xmlinfo.subnetValue = Short.valueOf(child.getElementsByTagName("subnetValue").item(0).getTextContent().trim());
                            this.jSliderSubnet.setValue((int) this.xmlinfo.subnetValue);
                        } else {
                            MsgBox.Show(this, "Can not get <subnetValue> from XML file", "XMLReadError");
                        }

                    } catch (DOMException ex) {
                        Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                        MsgBox.Show(this, "Can not get <subnetColor> from XML file", "XMLReadError");
                    }
                }
            }

            // DBServer Info:
            NodeList DBnodeList = doc.getElementsByTagName("DBServer");

            for (int i = 0; i < DBnodeList.getLength(); i++) {
                Node root = DBnodeList.item(i);
                if (root.getNodeType() == Node.ELEMENT_NODE) {
                    Element child = (Element) root;
                    try {
                        dbserverInfo.ServerIP
                                = InetAddress.getByName(child.getElementsByTagName("ServerIP").item(0).getTextContent().trim());
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                        MsgBox.Show(this, ex.toString(), "Exception");
                    }
                    try {
                        IPv6SubnetCalculator.dbserverInfo.PortNum
                                = Integer.parseInt(child.getElementsByTagName("PortNumber").item(0).getTextContent().trim());
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                        MsgBox.Show(this, ex.toString(), "Exception");
                    }
                    dbserverInfo.DBname = child.getElementsByTagName("DatabaseName").item(0).getTextContent().trim();
                    dbserverInfo.Tablename = child.getElementsByTagName("TableName").item(0).getTextContent().trim();
                    dbserverInfo.Username = child.getElementsByTagName("UserName").item(0).getTextContent().trim();
                }
            }
        }
    }

    public void WriteInfoXML() {

        /* Write Info into XML file. This file is used like Registry. 
         * Reading and Writing ORDER is IMPORTANT!
         */
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
            MsgBox.Show(this, ex.toString(), "Exception");
        }
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
            MsgBox.Show(this, ex.toString(), "Exception");
        }
        Document document = db.newDocument();

        Element root = document.createElement("IPv6SubnetCalculatorInfo");
        document.appendChild(root);

        Comment comment = document.createComment("Generated XML file.\n"
                + "        Change any value here and restart the application.");
        
        root.appendChild(comment);

        // UserInterface UI_Info:
        Element ui_info = document.createElement("UserInterface");
        root.appendChild(ui_info);

        Element address = document.createElement("address");
        address.appendChild(document.createTextNode(this.jTextFieldInputAddress.getText()));
        ui_info.appendChild(address);

        Element prefixValue = document.createElement("prefixValue");
        prefixValue.appendChild(document.createTextNode(String.valueOf(this.jSliderPrefix.getValue())));
        ui_info.appendChild(prefixValue);

        Element subnetValue = document.createElement("subnetValue");
        subnetValue.appendChild(document.createTextNode(String.valueOf(this.jSliderSubnet.getValue())));
        ui_info.appendChild(subnetValue);

        Element prefixCol = document.createElement("prefixColor");
        if (this.xmlinfo.prefixColor.length() == 0) {
            this.xmlinfo.prefixColor = this.prefixColor;
        }
        prefixCol.appendChild(document.createTextNode(this.xmlinfo.prefixColor));
        ui_info.appendChild(prefixCol);

        Element subnetCol = document.createElement("subnetColor");
        if (this.xmlinfo.subnetColor.length() == 0) {
            this.xmlinfo.subnetColor = this.subnetColor;
        }
        subnetCol.appendChild(document.createTextNode(this.xmlinfo.subnetColor));
        ui_info.appendChild(subnetCol);

        Element checkSubnet = document.createElement("checkSubnet");
        if (this.jCheckBoxSubnet.isSelected()) {
            checkSubnet.appendChild(document.createTextNode("true"));
        } else {
            checkSubnet.appendChild(document.createTextNode("false"));
        }
        ui_info.appendChild(checkSubnet);

        Element check128bit = document.createElement("check128bit");
        if (this.jCheckBox128bits.isSelected()) {
            check128bit.appendChild(document.createTextNode("true"));
        } else {
            check128bit.appendChild(document.createTextNode("false"));
        }
        ui_info.appendChild(check128bit);

        Element checkEnd = document.createElement("checkEnd");
        if (this.jCheckBoxEnd.isSelected()) {
            checkEnd.appendChild(document.createTextNode("true"));
        } else {
            checkEnd.appendChild(document.createTextNode("false"));
        }
        ui_info.appendChild(checkEnd);

        // DBServer Info:
        Element dbServerinfo = document.createElement("DBServer");
        root.appendChild(dbServerinfo);
        //
        Element ServerIP = document.createElement("ServerIP");
        if (dbserverInfo.ServerIP != null) {
            ServerIP.appendChild(document.createTextNode(dbserverInfo.ServerIP.getHostAddress().trim()));
            dbServerinfo.appendChild(ServerIP);
        } else {
            ServerIP.appendChild(document.createTextNode(""));
            dbServerinfo.appendChild(ServerIP);
        }
        //
        Element PortNumber = document.createElement("PortNumber");
        if (String.valueOf(dbserverInfo.PortNum) != "") {
            PortNumber.appendChild(document.createTextNode(String.valueOf(dbserverInfo.PortNum).trim()));
            dbServerinfo.appendChild(PortNumber);
        } else {
            PortNumber.appendChild(document.createTextNode(""));
            dbServerinfo.appendChild(PortNumber);
        }
        //
        Element DatabaseName = document.createElement("DatabaseName");
        if (dbserverInfo.DBname != " ") {
            DatabaseName.appendChild(document.createTextNode(dbserverInfo.DBname.trim()));
            dbServerinfo.appendChild(DatabaseName);
        } else {
            DatabaseName.appendChild(document.createTextNode(""));
            dbServerinfo.appendChild(DatabaseName);
        }
        //
        Element TableName = document.createElement("TableName");
        if (dbserverInfo.Tablename != "") {
            TableName.appendChild(document.createTextNode(dbserverInfo.Tablename.trim()));
            dbServerinfo.appendChild(TableName);
        } else {
            TableName.appendChild(document.createTextNode(""));
            dbServerinfo.appendChild(TableName);
        }
        //
        Element UserName = document.createElement("UserName");
        if (dbserverInfo.Username != "") {
            UserName.appendChild(document.createTextNode(dbserverInfo.Username.trim()));
            dbServerinfo.appendChild(UserName);
        } else {
            UserName.appendChild(document.createTextNode(""));
            dbServerinfo.appendChild(UserName);
        }
        //
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
            MsgBox.Show(this, ex.toString(), "Exception");
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(xmlFilename));

        try {
            transformer.transform(domSource, streamResult);
        } catch (TransformerException ex) {
            Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
            MsgBox.Show(this, ex.toString(), "Exception");
        }
    }

    public void ConnectToDBServer() {
        try {
            // Please Read>> https://docs.oracle.com/javase/8/docs/api/index.html?java/sql/DriverManager.html
            //Class.forName("com.mysql.jdbc.Driver");
            //Class.forName("com.mysql.cj.jdbc.Driver");
            try {
                MySQLconnection = DriverManager.getConnection("jdbc:mysql://"
                        + dbserverInfo.ServerIP.getHostAddress() + ":"
                        + String.valueOf(dbserverInfo.PortNum) + "/?"
                        + "useUnicode=true" + "&"
                        + "useJDBCCompliantTimezoneShift=true" + "&"
                        + "useLegacyDatetimeCode=false" + "&"
                        + "serverTimezone=UTC",
                        dbserverInfo.Username,
                        new String(dbserverInfo.Password.getPassword()));

                UpdateDbStatus();
                MsgBox.Show(this, "Database Connected!", "Connect to DBServer");

            } catch (SQLException ex) {
                Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                MsgBox.Show(this, ex.toString(), "Error: ConnectToDBServer()");
                UpdateDbStatus();
                return;
            }
            dbserverInfo.ConnectionString = "jdbc:mysql://"
                    + dbserverInfo.ServerIP.getHostAddress() + ":"
                    + String.valueOf(dbserverInfo.PortNum)
                    + "/?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
                    + dbserverInfo.DBname;
            //
            // Database exist?
            this.statement = MySQLconnection.createStatement();
            this.resultSet = this.statement.executeQuery("SELECT SCHEMA_NAME FROM "
                    + "INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='"
                    + dbserverInfo.DBname + "';");

            // Create database if not exists:
            if (!this.resultSet.next()) {
                this.statement.executeUpdate("CREATE DATABASE IF NOT EXISTS "
                        + "`" + dbserverInfo.DBname + "`"
                        + " DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;");
                // Select/use database:
                this.statement.execute("USE "
                        + "`" + dbserverInfo.DBname + "`;");
                // Create table if not exists:
                this.statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
                        + "`" + dbserverInfo.DBname + "`."
                        + "`" + dbserverInfo.Tablename + "`"
                        + " ( "
                        + "prefix VARBINARY(16), "
                        + "pflen TINYINT UNSIGNED, "
                        + "parentpflen TINYINT UNSIGNED, "
                        + "netname VARCHAR(60), "
                        + "person  VARCHAR(60), "
                        + "organization VARCHAR(60), "
                        + "`as-num` INT UNSIGNED, "
                        + "phone VARCHAR(60), "
                        + "email VARCHAR(60), "
                        + "status VARCHAR(60), "
                        + "created DATETIME NOT NULL default NOW(), "
                        + "`last-updated` DATETIME NOT NULL default NOW() ON UPDATE NOW(), "
                        + "PRIMARY KEY(prefix, pflen, parentpflen) "
                        + "); ");

                // Triggers for timestamps: Triggers are assoc.with DBs.
                // Note: 
                // java.sql.SQLException: You do not have the SUPER privilege and binary logging is enabled 
                // (you *might* want to use the less safe log_bin_trust_function_creators variable)
                //
                // If you get exception above, you *might* want to configure your server (consult your admin):
                //  > mysql -u USERNAME -p
                //  > set global log_bin_trust_function_creators=1;
                
                this.resultSet = this.statement.executeQuery("SELECT "
                        + "TRIGGER_NAME FROM information_schema.triggers "
                        + "where TRIGGER_NAME='trig_insert' AND TRIGGER_SCHEMA='"
                        + dbserverInfo.DBname + "';");
                if (!this.resultSet.next()) {
                    this.statement.executeUpdate(
                            "CREATE TRIGGER trig_insert BEFORE INSERT ON "
                            + "`" + dbserverInfo.DBname + "`."
                            + "`" + dbserverInfo.Tablename + "`"
                            + " FOR EACH ROW BEGIN SET NEW.`created`=IF(ISNULL(NEW.`created`) OR "
                            + "NEW.`created`='1970-01-01 01:01:01', CURRENT_TIMESTAMP, "
                            + "IF(NEW.`created` < CURRENT_TIMESTAMP, NEW.`created`, "
                            + "CURRENT_TIMESTAMP));SET NEW.`last-updated`=NEW.`created`; END;"
                    );
                }
                this.resultSet = this.statement.executeQuery("SELECT "
                        + "TRIGGER_NAME FROM information_schema.triggers "
                        + "where TRIGGER_NAME='trig_update' AND TRIGGER_SCHEMA='"
                        + dbserverInfo.DBname + "';");
                if (!this.resultSet.next()) {
                    this.statement.executeUpdate(
                            "CREATE trigger trig_update BEFORE UPDATE ON "
                            + "`" + dbserverInfo.DBname + "`."
                            + "`" + dbserverInfo.Tablename + "`"
                            + " FOR EACH ROW "
                            + "SET NEW.`last-updated` = IF(NEW.`last-updated` < OLD.`last-updated`, "
                            + "OLD.`last-updated`, CURRENT_TIMESTAMP);"
                    );
                }
                // Create index:                    
                this.resultSet = this.statement.executeQuery(
                        "SHOW INDEX from " + dbserverInfo.DBname
                        + " WHERE Key_name = 'idx_index';");
                if (!this.resultSet.next()) {
                    this.statement.executeUpdate(
                            " CREATE INDEX idx_index ON "
                            + "`" + dbserverInfo.DBname + "`."
                            + "`" + dbserverInfo.Tablename + "`"
                            + " (prefix, pflen, parentpflen) USING BTREE;"
                    );
                }
                //
            } else { // DB exists:
                this.statement.execute(
                        "USE " + "`" + dbserverInfo.DBname + "`;");
                // create table if not exists:
                this.statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS "
                        + "`" + dbserverInfo.DBname + "`."
                        + "`" + dbserverInfo.Tablename + "`"
                        + " ( "
                        + "prefix VARBINARY(16), "
                        + "pflen TINYINT UNSIGNED, "
                        + "parentpflen TINYINT UNSIGNED, "
                        + "netname VARCHAR(60), "
                        + "person  VARCHAR(60), "
                        + "organization VARCHAR(60), "
                        + "`as-num` INT UNSIGNED, "
                        + "phone VARCHAR(60), "
                        + "email VARCHAR(60), "
                        + "status VARCHAR(60), "
                        + "created DATETIME NOT NULL default NOW(), "
                        + "`last-updated` DATETIME NOT NULL default NOW() ON UPDATE NOW(), "
                        + "PRIMARY KEY(prefix, pflen, parentpflen) "
                        + "); "
                );
                // Triggers for datetime/timestamps:
                this.resultSet = this.statement.executeQuery("SELECT "
                        + "TRIGGER_NAME FROM information_schema.triggers "
                        + "where TRIGGER_NAME='trig_insert' AND TRIGGER_SCHEMA='"
                        + dbserverInfo.DBname + "';");

                if (!this.resultSet.next()) {
                    this.statement.executeUpdate(
                            "CREATE TRIGGER trig_insert BEFORE INSERT ON "
                            + "`" + dbserverInfo.DBname + "`."
                            + "`" + dbserverInfo.Tablename + "`"
                            + " FOR EACH ROW BEGIN SET NEW.`created`=IF(ISNULL(NEW.`created`) OR "
                            + "NEW.`created`='1970-01-01 01:01:01', CURRENT_TIMESTAMP, "
                            + "IF(NEW.`created` < CURRENT_TIMESTAMP, NEW.`created`, "
                            + "CURRENT_TIMESTAMP));SET NEW.`last-updated`=NEW.`created`; END;"
                    );
                }
                this.resultSet = this.statement.executeQuery("SELECT "
                        + "TRIGGER_NAME FROM information_schema.triggers "
                        + "where TRIGGER_NAME='trig_update' AND TRIGGER_SCHEMA='"
                        + dbserverInfo.DBname + "';");
                if (!this.resultSet.next()) {
                    this.statement.executeUpdate(
                            "CREATE trigger trig_update BEFORE UPDATE ON "
                            + "`" + dbserverInfo.DBname + "`."
                            + "`" + dbserverInfo.Tablename + "`"
                            + " FOR EACH ROW "
                            + "SET NEW.`last-updated` = IF(NEW.`last-updated` < OLD.`last-updated`, "
                            + "OLD.`last-updated`, CURRENT_TIMESTAMP);"
                    );
                }
                // and index it if not indexed:
                this.resultSet = this.statement.executeQuery(
                        "SHOW INDEX from "
                        + "`" + dbserverInfo.DBname + "`."
                        + "`" + dbserverInfo.Tablename + "` "
                        + " WHERE Key_name = 'idx_index';");
                if (!this.resultSet.next()) {
                    this.statement.executeUpdate(
                            " CREATE INDEX idx_index ON "
                            + "`" + dbserverInfo.DBname + "`."
                            + "`" + dbserverInfo.Tablename + "`"
                            + " (prefix, pflen, parentpflen) USING BTREE;"
                    );
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
            MsgBox.Show(this, ex.toString(), "Error: ConnectToDBServer()");
            UpdateDbStatus();
        }
    }

    public static void DBClose() {
        try {
            //local
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (MySQLconnection != null) {
                MySQLconnection.close();
                MySQLconnection = null;
            }

            // Close mysqsql connections of all other Forms:
            for (StatsUsage fr : statsUsage) {
                try {
                    if (fr.MySQLconnection != null) {
                        fr.MySQLconnection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                    MsgBox.Show(null, ex.toString(), "DBClose():");
                }
            }
            for (ListAssignedfromDB fr : listAssignedfromdb) {
                try {
                    if (fr.MySQLconnection != null) {
                        fr.MySQLconnection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                    MsgBox.Show(null, ex.toString(), "DBClose():");
                }
            }
            for (ListParentNets fr : listParentNets) {
                try {
                    if (fr.MySQLconnection != null) {
                        fr.MySQLconnection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                    MsgBox.Show(null, ex.toString(), "DBClose():");
                }
            }
            for (DatabaseUI fr : dbUI) {
                try {
                    if (fr.MySQLconnection != null) {
                        fr.MySQLconnection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                    MsgBox.Show(null, ex.toString(), "DBClose():");
                }
            }
            for (PrefixSubLevels fr : prefixSublevels) {
                try {
                    if (fr.MySQLconnection != null) {
                        fr.MySQLconnection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                    MsgBox.Show(null, ex.toString(), "DBClose():");
                }
            }
            for (ListSubnetRange fr : listSubnetRange) {
                try {
                    if (fr.MySQLconnection != null) {
                        fr.MySQLconnection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                    MsgBox.Show(null, ex.toString(), "DBClose():");
                }
            }
            for (GetPrefixInfoFromDB fr : getPrefixInfo) {
                try {
                    if (fr.MySQLconnection != null) {
                        fr.MySQLconnection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                    MsgBox.Show(null, ex.toString(), "DBClose():");
                }
            }

            UpdateDbStatus();

        } catch (Exception ex) {
            MsgBox.Show(null, ex.toString(), "Error:");
        }
    }

    public static void UpdateDbStatus() {

        if (MySQLconnection == null) {
            jLabeldbstatus.setText("db=Down");

            for (StatsUsage fr : statsUsage) {
                fr.jLabeldbstatus.setText("db=Down");
                fr.setConnection(MySQLconnection);
            }
            for (ListAssignedfromDB fr : listAssignedfromdb) {
                fr.jLabeldbstatus.setText("db=Down");
                fr.setConnection(MySQLconnection);
            }
            for (ListParentNets fr : listParentNets) {
                fr.jLabeldbstatus.setText("db=Down");
                fr.setConnection(MySQLconnection);
            }
            for (DatabaseUI fr : dbUI) {
                fr.jLabeldbstatus.setText("db=Down");
                fr.setConnection(MySQLconnection);
            }
            for (PrefixSubLevels fr : prefixSublevels) {
                fr.jLabeldbstatus.setText("db=Down");
                fr.setConnection(MySQLconnection);
            }
            for (ListSubnetRange fr : listSubnetRange) {
                fr.jLabeldbstatus.setText("db=Down");
                fr.setConnection(MySQLconnection);
            }
            for (GetPrefixInfoFromDB fr : getPrefixInfo) {
                //fr.jLabeldbstatus.setText("db=Down");
                fr.setConnection(MySQLconnection);
            }
            MsgBox.Show(null, "Database connection closed.\r\nTry to connect from main menu.", "Info");

        } else { // not null
            try {
                if (MySQLconnection.isValid(0)) {
                    jLabeldbstatus.setText("db=Up ");

                    for (StatsUsage fr : statsUsage) {
                        fr.jLabeldbstatus.setText("db=Up ");
                        fr.setConnection(MySQLconnection);
                    }
                    for (ListAssignedfromDB fr : listAssignedfromdb) {
                        fr.jLabeldbstatus.setText("db=Up ");
                        fr.setConnection(MySQLconnection);
                    }
                    for (ListParentNets fr : listParentNets) {
                        fr.jLabeldbstatus.setText("db=Up");
                        fr.setConnection(MySQLconnection);
                    }
                    for (DatabaseUI fr : dbUI) {
                        fr.jLabeldbstatus.setText("db=Up ");
                        fr.setConnection(MySQLconnection);
                    }
                    for (PrefixSubLevels fr : prefixSublevels) {
                        fr.jLabeldbstatus.setText("db=Up ");
                        fr.setConnection(MySQLconnection);
                    }
                    for (ListSubnetRange fr : listSubnetRange) {
                        fr.jLabeldbstatus.setText("db=Up ");
                        fr.setConnection(MySQLconnection);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                MsgBox.Show(null, ex.toString(), "Error:");
            }
        }
    }

    private short QuerySelectedPrefix(String inprefix, short pflen) {
        UpdateDbStatus();
        if (MySQLconnection == null) {
            return -1;
        }
        String MySQLcmd = "SELECT inet6_ntoa(prefix), pflen, parentpflen "
                + " from "
                + "`" + dbserverInfo.DBname + "`."
                + "`" + dbserverInfo.Tablename + "`"
                + " WHERE ( prefix=inet6_aton('" + inprefix + "') "
                + " AND pflen=" + pflen + " );";
        try {
            statement = MySQLconnection.createStatement();
            resultSet = statement.executeQuery(MySQLcmd);
            short parentpflen = 0;
            while (resultSet.next()) {
                parentpflen = resultSet.getByte(3);
            }
            return parentpflen;
        } catch (Exception ex) {
            MsgBox.Show(this, ex.toString(), "Error:");
            return -1;
        }
    }

    private void EscapeKey() {
        int hc = this.hashCode();
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
                if (JOptionPane.showConfirmDialog(getRootPane(), "Exit Application?", "Exit", 0) == 0) {
                    WriteInfoXML();                                              //^ options. 1 is Yes/No/Cancel.
                    System.exit(0);
                }
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

    private void AddHighlights() {
        int count1 = (int) jSliderPrefix.getValue() + ((int) jSliderPrefix.getValue() / 4);
        int count2 = (int) jSliderSubnet.getValue() + ((int) jSliderSubnet.getValue() / 4);

        try {
            jTextFieldBits.getHighlighter().removeAllHighlights();

            DefaultHighlightPainter h1 = new DefaultHighlighter.DefaultHighlightPainter(Color.decode(this.prefixColor));
            DefaultHighlightPainter h2 = new DefaultHighlighter.DefaultHighlightPainter(Color.decode(this.subnetColor));

            jTextFieldBits.getHighlighter().addHighlight(0, count1, h1);
            jTextFieldBits.getHighlighter().addHighlight(count1, count2, h2);

        } catch (BadLocationException ex) {
            Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
            MsgBox.Show(null, ex.toString(), "Error:");
        }
    }

    private void DefaultSize() {

        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            this.setSize(640, 591);
            this.setPreferredSize(new java.awt.Dimension(640, 591));
        } else {
            this.setSize(586, 591);
            this.setPreferredSize(new java.awt.Dimension(586, 591));
        }
        int current = this.jSliderSubnet.getValue();
        this.jSliderPrefix.setMaximum(64);
        this.jSliderSubnet.setMaximum(64);
        this.jSliderSubnet.setValue(current);

        this.jTextFieldAddrSpaceNo.setSize(140, 13);
        this.jTextFieldAddrSpaceNo.setPreferredSize(new Dimension(140, 13));

    }

    private void ExpandSize() {

        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            this.setSize(1140, 591);
            this.setPreferredSize(new java.awt.Dimension(1140, 591));
        } else {
            this.setSize(1015, 591);
            this.setPreferredSize(new java.awt.Dimension(1015, 591));
        }

        int current = this.jSliderSubnet.getValue();
        this.jSliderPrefix.setMaximum(128);
        this.jSliderSubnet.setMaximum(128);
        this.jSliderSubnet.setValue(current);

        this.jTextFieldAddrSpaceNo.setSize(250, 13);
        this.jTextFieldAddrSpaceNo.setPreferredSize(new Dimension(250, 13));
    }

    private void AddJMenuWindow() {
        jMenuWindow = new javax.swing.JMenu();
        jMenuWindow.setMnemonic('W');
        jMenuWindow.setText("Window");
        jMenuItemWindowCloseAll = new JMenuItem();
        jMenuItemWindowCloseAll.setMnemonic('C');
        jMenuItemWindowCloseAll.setText("Close All Windows (0)");
        jMenuWindow.setText("Window " + "(" + windowList.size() + ")");

        jMenuItemWindowCloseAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                int t = jMenuWindow.getItemCount() - 1;

                ActiveWindow tmp = null;

                for (int k = t; k >= 2; k--) {
                    jMenuWindow.remove(jMenuWindow.getItem(k));
                    tmp = windowList.get(k - 2);
                    tmp.frame.dispose();
                }
                windowList.clear();
                jMenuWindow.setText("Window " + "(" + windowList.size() + ")");
                jMenuItemWindowCloseAll.setText("Close All Windows" + "(" + windowList.size() + ")");
            }
        });

        jMenuWindow.add(jMenuItemWindowCloseAll);
        jMenuWindow.add(new javax.swing.JPopupMenu.Separator());
        this.jMenuBar1.add(jMenuWindow, 4);
    }

    public static void HeaderWindowItems() {

        jMenuWindow.removeAll();

        for (var j : jMenuItemWindowCloseAll.getActionListeners()) {
            jMenuItemWindowCloseAll.removeActionListener(j);
        }

        jMenuItemWindowCloseAll = new JMenuItem("Close All Windows" + "(" + windowList.size() + ")");
        //
        jMenuItemWindowCloseAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                int t = jMenuWindow.getItemCount() - 1;

                ActiveWindow tmp = null;

                for (int k = t; k >= 2; k--) {
                    jMenuWindow.remove(jMenuWindow.getItem(k));
                    tmp = windowList.get(k - 2);
                    tmp.frame.dispose();//.close();
                }
                windowList.clear();
                jMenuWindow.setText("Window " + "(" + windowList.size() + ")");
                jMenuItemWindowCloseAll.setText("Close All Windows" + "(" + windowList.size() + ")");
            }
        });

        jMenuWindow.add(jMenuItemWindowCloseAll);
        jMenuWindow.add(new javax.swing.JPopupMenu.Separator());
    }

    public static void AddWindowItem(String w, JFrame fr, int hc) {

        windowList.add(new ActiveWindow(w, fr, hc));

        JMenuItem witem = new JMenuItem(w);
        jMenuWindow.add(witem);
        jMenuWindow.setText("Window " + "(" + windowList.size() + ")");
        jMenuItemWindowCloseAll.setText("Close All Window " + "(" + windowList.size() + ")");
        jMenuWindow.setVisible(true);

        witem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                for (ActiveWindow i : windowList) {
                    if (i.hashcode == hc) {
                        i.frame.toFront();
                        if (i.frame.getState() == JFrame.ICONIFIED) {
                            i.frame.setState(JFrame.NORMAL);
                        }
                        i.frame.setLocationRelativeTo(null);
                    }
                }
            }
        });
    }

    public static void RemoveWindowItem(int hc) {

        ActiveWindow tmp = new ActiveWindow();
        for (var i : windowList) {
            if (i.hashcode == hc) {
                tmp = i;
            }
        }
        windowList.remove(tmp);

        jMenuWindow.setVisible(false);

        HeaderWindowItems();

        for (ActiveWindow i : windowList) {
            JMenuItem mi = new JMenuItem(i.name);
            jMenuWindow.add(mi);

            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    for (ActiveWindow j : windowList) {
                        if (j.hashcode == i.hashcode) {
                            j.frame.toFront();
                            if (j.frame.getState() == JFrame.ICONIFIED) {
                                j.frame.setState(JFrame.NORMAL);
                            }
                            j.frame.setLocationRelativeTo(null);
                        }
                    }
                }
            });
        }
        jMenuWindow.setText("Window " + "(" + windowList.size() + ")");
        jMenuItemWindowCloseAll.setText("Close All Windows " + "(" + windowList.size() + ")");
        jMenuWindow.setVisible(true);
    }

    public void UpdateCount() {
        if (this.jList1.getModel().getSize() == 0) {
            listcount.setVisible(false);
            return;
        } else {
            listcount.setVisible(true);
            if (this.jCheckBoxEnd.isSelected()) {
                listcount.setText("[" + String.valueOf(this.jList1.getModel().getSize() / 3) + "]");
            } else {
                listcount.setText("[" + String.valueOf(this.jList1.getModel().getSize()) + "]");
            }
        }

        if (this.jList1.getModel().getSize() != 0) {

            BigInteger numberofPage = prefixmax.shiftRight(7);

            BigInteger lastindex = BigInteger.ZERO;

            if (!this.jCheckBoxEnd.isSelected()) {
                lastindex = new BigInteger(this.jList1.getModel().getElementAt(this.jList1.getModel().getSize() - 1).split(">")[0].replace("p", ""), 10);
            } else { // end-checked
                lastindex = new BigInteger(this.jList1.getModel().getElementAt(this.jList1.getModel().getSize() - 3).split(">")[0].replace("p", ""), 10);
            }

            lastindex = lastindex.add(BigInteger.ONE); // since index starts from zero

            if (numberofPage.compareTo(BigInteger.ONE) <= 0
                    || lastindex.equals(prefixmax.subtract(BigInteger.ONE))) {
                this.jProgressBar1.setValue(100);
                this.jProgressBar1.setString("100%");
                return;
            } else {
                int percent;
                lastindex = lastindex.multiply(new BigInteger("100", 10));
                lastindex = lastindex.divide(prefixmax);
                percent = (int) (lastindex).doubleValue(); // lastindex can not exceed 100 and prefixmax

                this.jProgressBar1.setValue(percent);
                this.jProgressBar1.setString(String.valueOf(percent) + "%");

            }
        }
    }

    public void DisplayPrevNextSpace() {

        this.jList1.setModel(new DefaultListModel<String>());

        this.jButtonListBack.setEnabled(false);
        this.jButtonListForward.setEnabled(false);
        this.jButtonListLast.setEnabled(false);

        this.jTextFieldAddrSpaceNo.setText("#" + String.valueOf(StartEnd.subnetidx));

        subnets.Start = StartEnd.Start;
        subnets.End = BigInteger.ZERO;
        StartEnd.Resultv6 = StartEnd.Start;

        String s = v6ST.Kolonlar(StartEnd.Start);

        if (this.jCheckBox128bits.isSelected()) {
            if (s.length() > 32) {
                s = s.substring(0, 39);
                s += "/" + (int) this.jSliderPrefix.getValue();
            }
        } else if (!this.jCheckBox128bits.isSelected()) {
            if (s.length() > 16) {
                s = s.substring(0, 19);
                s += "::/" + (int) this.jSliderPrefix.getValue();
            }
        }

        this.jTextFieldIPv6Addr.setText(v6ST.Kolonlar(StartEnd.Start));
        this.jTextFieldStartAddr.setText(s);

        s = v6ST.Kolonlar(StartEnd.End);
        if (this.jCheckBox128bits.isSelected()) {
            if (s.length() > 32) {
                s = s.substring(0, 39);
                s += "/" + (int) this.jSliderPrefix.getValue();
            }
        }
        if (!this.jCheckBox128bits.isSelected()) {
            if (s.length() > 16) {
                s = s.substring(0, 19);
                s += "::/" + (int) this.jSliderPrefix.getValue();
            }
        }
        this.jTextFieldEndAddr.setText(s);
    }

    public void Calculate(String sin) {

        if (v6ST.IsAddressCorrect(sin)) {
            this.jLabel1.setText(v6ST.errmsg);
            StartEnd.Resultv6 = v6ST.FormalizeAddr(sin);
            String s = v6ST.Kolonlar(StartEnd.Resultv6);
            this.jTextFieldIPv6Addr.setText(s);

            StartEnd.slash = (int) this.jSliderPrefix.getValue();
            StartEnd.subnetslash = (int) this.jSliderSubnet.getValue();

            StartEnd = v6ST.StartEndAddresses(StartEnd, this.jCheckBox128bits.isSelected());
            subnets.Start = StartEnd.Start;
            subnets.End = BigInteger.ZERO;
            subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;
            subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;

            String s0 = v6ST.Kolonlar(StartEnd.Start);
            String s1 = v6ST.Kolonlar(StartEnd.End);
            if (this.jCheckBox128bits.isSelected()) {
                this.jTextFieldStartAddr.setText(s0 + "/" + String.valueOf((int) this.jSliderPrefix.getValue()));
                this.jTextFieldEndAddr.setText(s1 + "/" + String.valueOf((int) this.jSliderSubnet.getValue()));
            } else {
                /* 64 bits*/
                s0 = s0.substring(0, 19);
                this.jTextFieldStartAddr.setText(s0 + "::/"
                        + String.valueOf((int) this.jSliderPrefix.getValue()));
                s1 = s1.substring(0, 19);
                this.jTextFieldEndAddr.setText(s1 + "::/"
                        + String.valueOf((int) this.jSliderPrefix.getValue()));
            }
            UpdatePrintBin();
            UpdateStatus();
            StartEnd = v6ST.NextSpace(StartEnd, this.jCheckBox128bits.isSelected());
            this.jTextFieldAddrSpaceNo.setText("#" + String.valueOf(StartEnd.subnetidx));
            this.jSliderPrefix.setEnabled(true);
            this.jCheckBoxSubnet.setEnabled(true);
            this.jCheckBox128bits.setEnabled(true);
            this.jButtonPrevAddrSpace.setEnabled(true);
            this.jButtonNextAddrSpace.setEnabled(true);

        } else {
            this.jLabel1.setText(v6ST.errmsg);
            this.jCheckBox128bits.setSelected(false);
            this.jSliderPrefix.setValue(1);
            this.jSliderPrefix.setMaximum(64);
            this.jCheckBoxSubnet.setSelected(false);
            this.jSliderSubnet.setValue(1);
            this.jSliderSubnet.setEnabled(false);
            this.jSliderSubnet.setMaximum(64);
            StartEnd.Initialize();

            this.jTextFieldIPv6Addr.setText("");
            this.jTextFieldStartAddr.setText("");
            this.jTextFieldEndAddr.setText("");
        }
        MaskValue();
    }

    public void MaskValue() {
        BigInteger mask = v6ST.PrepareMask((short) this.jSliderSubnet.getValue());
        this.jTextFieldMask.setText(v6ST.CompressAddress(v6ST.Kolonlar(mask)));

        BigInteger wildcmask = v6ST.PrepareWildCardMask(mask);
        this.jTextFieldWildcard.setText(v6ST.CompressAddress(v6ST.Kolonlar(wildcmask)));

    }

    public void UpdateStatus() {
        int diff = (int) this.jSliderSubnet.getValue() - (int) this.jSliderPrefix.getValue();
        prefixmax = BigInteger.ONE.shiftLeft(diff);
        asnmax = BigInteger.ONE.shiftLeft((int) this.jSliderPrefix.getValue());

        this.jLabel10.setText(" Delta=[" + String.valueOf(diff) + "]     "
                + "Prefixes=[" + String.valueOf(prefixmax) + "]     "
                + "Address Spaces=[" + String.valueOf(asnmax) + "]");
    }

    public void UpdatePrintBin() {
        String sbin = v6ST.PrintBin(StartEnd,
                (int) this.jSliderPrefix.getValue(), this.jCheckBox128bits.isSelected());

        this.jTextFieldBits.setText(sbin);
        AddHighlights();
    }

    public static class MyCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (index % 2 != 0 && !isSelected) {
                setBackground(new Color(242, 242, 242));
            }
            // setFont(new Font(java.awt.Font.MONOSPACED, Font.PLAIN, 14));
            setOpaque(true);
            return this;
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

        jPopupMenuMainForm = new javax.swing.JPopupMenu();
        jMenuItemSelectAll = new javax.swing.JMenuItem();
        jMenuItemCopy = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        contextListSelectedRange = new javax.swing.JMenuItem();
        contextList64Prefixes = new javax.swing.JMenuItem();
        contextList128Addresses = new javax.swing.JMenuItem();
        contextListDNSRevZones = new javax.swing.JMenuItem();
        contextWorkWithSelected = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        contextGotoPrefix = new javax.swing.JMenuItem();
        contextGotoSearch = new javax.swing.JMenuItem();
        contextWhoisQuery = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        contextDBSendPrefix = new javax.swing.JMenuItem();
        contextDBGetPrefix = new javax.swing.JMenuItem();
        contextModifyUpdateParent = new javax.swing.JMenuItem();
        contextDBPrefixSubLevels = new javax.swing.JMenuItem();
        contextDBListAllParents = new javax.swing.JMenuItem();
        contextDBStats = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        contextSaveAs = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldInputAddress = new javax.swing.JTextField();
        jButtonCalculate = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldIPv6Addr = new javax.swing.JTextField();
        jButtonReset = new javax.swing.JButton();
        jTextFieldStartAddr = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldEndAddr = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButtonPrevAddrSpace = new javax.swing.JButton();
        jButtonNextAddrSpace = new javax.swing.JButton();
        jCheckBox128bits = new javax.swing.JCheckBox();
        jTextFieldBits = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jSliderPrefix = new javax.swing.JSlider();
        jSliderSubnet = new javax.swing.JSlider();
        jCheckBoxSubnet = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldMask = new javax.swing.JTextField();
        jButtonListPrefixes = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabeldbstatus = new javax.swing.JLabel();
        jButtonListBack = new javax.swing.JButton();
        jButtonListForward = new javax.swing.JButton();
        jButtonListLast = new javax.swing.JButton();
        jCheckBoxEnd = new javax.swing.JCheckBox();
        jTextFieldAddrSpaceNo = new javax.swing.JTextField();
        listcount = new javax.swing.JLabel();
        jTextFieldWildcard = new javax.swing.JTextField();
        jProgressBar1 = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemListSelectedRange = new javax.swing.JMenuItem();
        jMenuItemList64Prefixes = new javax.swing.JMenuItem();
        jMenuItemList128Addresses = new javax.swing.JMenuItem();
        jMenuItemListDNSRevZones = new javax.swing.JMenuItem();
        jMenuItemWorkWithSelected = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemWhoisQuery = new javax.swing.JMenuItem();
        jMenuItemCompress = new javax.swing.JMenuItem();
        jMenuItemASNconvert = new javax.swing.JMenuItem();
        jMenuItemServiceNames = new javax.swing.JMenuItem();
        jMenuGoto = new javax.swing.JMenu();
        jMenuItemGotoAddrSpace = new javax.swing.JMenuItem();
        jMenuItemGotoPrefix = new javax.swing.JMenuItem();
        jMenuItemGotoSearch = new javax.swing.JMenuItem();
        jMenuDatabase = new javax.swing.JMenu();
        jMenuItemDBConnect = new javax.swing.JMenuItem();
        jMenuItemDBClose = new javax.swing.JMenuItem();
        jMenuItemDBStatus = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemDBOpenForm = new javax.swing.JMenuItem();
        jMenuItemDBSendPrefix = new javax.swing.JMenuItem();
        jMenuItemDBGetPrefix = new javax.swing.JMenuItem();
        jMenuItemModifyUpdateParent = new javax.swing.JMenuItem();
        jMenuItemDBPrefixSubLevels = new javax.swing.JMenuItem();
        jMenuItemListAllParents = new javax.swing.JMenuItem();
        jMenuItemDBStats = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemAbout = new javax.swing.JMenuItem();

        jPopupMenuMainForm.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenuMainFormPopupMenuWillBecomeVisible(evt);
            }
        });

        jMenuItemSelectAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemSelectAll.setText("Select All");
        jMenuItemSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSelectAllActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(jMenuItemSelectAll);

        jMenuItemCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemCopy.setText("Copy");
        jMenuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopyActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(jMenuItemCopy);
        jPopupMenuMainForm.add(jSeparator4);

        contextListSelectedRange.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        contextListSelectedRange.setText("List Selected Range");
        contextListSelectedRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextListSelectedRangeActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextListSelectedRange);

        contextList64Prefixes.setText("List /64 Prefixes");
        contextList64Prefixes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextList64PrefixesActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextList64Prefixes);

        contextList128Addresses.setText("List /128 Addresses");
        contextList128Addresses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextList128AddressesActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextList128Addresses);

        contextListDNSRevZones.setText("List All DNS Reverse Zones");
        contextListDNSRevZones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextListDNSRevZonesActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextListDNSRevZones);

        contextWorkWithSelected.setText("work with selected Prefix");
        contextWorkWithSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextWorkWithSelectedActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextWorkWithSelected);
        jPopupMenuMainForm.add(jSeparator5);

        contextGotoPrefix.setText("Go to Prefix Number");
        contextGotoPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextGotoPrefixActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextGotoPrefix);

        contextGotoSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        contextGotoSearch.setText("Search Prefix");
        contextGotoSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextGotoSearchActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextGotoSearch);

        contextWhoisQuery.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        contextWhoisQuery.setText("whois Query...");
        contextWhoisQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextWhoisQueryActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextWhoisQuery);
        jPopupMenuMainForm.add(jSeparator6);

        contextDBSendPrefix.setText("Send(Update) prefix to database");
        contextDBSendPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextDBSendPrefixActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextDBSendPrefix);

        contextDBGetPrefix.setText("Get prefix info from database");
        contextDBGetPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextDBGetPrefixActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextDBGetPrefix);

        contextModifyUpdateParent.setText("Modify(Update) Parent prefix");
        contextModifyUpdateParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextModifyUpdateParentActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextModifyUpdateParent);

        contextDBPrefixSubLevels.setText("Prefix subLevels");
        contextDBPrefixSubLevels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextDBPrefixSubLevelsActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextDBPrefixSubLevels);

        contextDBListAllParents.setMnemonic('L');
        contextDBListAllParents.setText("List All Parent prefixes");
        contextDBListAllParents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextDBListAllParentsActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextDBListAllParents);

        contextDBStats.setText("Statistics/Utilization of this range");
        contextDBStats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextDBStatsActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextDBStats);
        jPopupMenuMainForm.add(jSeparator7);

        contextSaveAs.setText("Save As Text...");
        contextSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contextSaveAsActionPerformed(evt);
            }
        });
        jPopupMenuMainForm.add(contextSaveAs);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IPv6 Subnet Calculator");
        setMinimumSize(new java.awt.Dimension(586, 591));
        setName("IPv6SubnetCalculator"); // NOI18N
        setSize(new java.awt.Dimension(586, 591));
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
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(9, 160, 254));
        jLabel1.setText("> ok");

        jLabel2.setText("Address:");

        jTextFieldInputAddress.setText("2001:db8:abcd:1234::");
        jTextFieldInputAddress.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldInputAddressFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldInputAddressFocusLost(evt);
            }
        });
        jTextFieldInputAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldInputAddressKeyPressed(evt);
            }
        });

        jButtonCalculate.setMnemonic('C');
        jButtonCalculate.setText("Calculate");
        jButtonCalculate.setToolTipText("Calculate using Address field");
        jButtonCalculate.setMaximumSize(new java.awt.Dimension(88, 38));
        jButtonCalculate.setMinimumSize(new java.awt.Dimension(88, 38));
        jButtonCalculate.setPreferredSize(new java.awt.Dimension(88, 38));
        jButtonCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCalculateActionPerformed(evt);
            }
        });

        jLabel3.setText("IPv6:");

        jTextFieldIPv6Addr.setEditable(false);
        jTextFieldIPv6Addr.setText(" ");
        jTextFieldIPv6Addr.setPreferredSize(new java.awt.Dimension(289, 26));

        jButtonReset.setMnemonic('R');
        jButtonReset.setText("Reset");
        jButtonReset.setMaximumSize(new java.awt.Dimension(88, 38));
        jButtonReset.setMinimumSize(new java.awt.Dimension(88, 38));
        jButtonReset.setPreferredSize(new java.awt.Dimension(88, 38));
        jButtonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetActionPerformed(evt);
            }
        });

        jTextFieldStartAddr.setEditable(false);
        jTextFieldStartAddr.setText(" ");

        jLabel4.setText("Start:");

        jTextFieldEndAddr.setEditable(false);
        jTextFieldEndAddr.setText(" ");

        jLabel5.setText("End:");

        jButtonPrevAddrSpace.setText("-");
        jButtonPrevAddrSpace.setToolTipText("Decrease Addr.Space Number");
        jButtonPrevAddrSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrevAddrSpaceActionPerformed(evt);
            }
        });

        jButtonNextAddrSpace.setText("+");
        jButtonNextAddrSpace.setToolTipText("Increase Addr.Space Number");
        jButtonNextAddrSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextAddrSpaceActionPerformed(evt);
            }
        });

        jCheckBox128bits.setText("128bits");
        jCheckBox128bits.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckBox128bits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox128bitsActionPerformed(evt);
            }
        });

        jTextFieldBits.setEditable(false);
        jTextFieldBits.setFont(new java.awt.Font("DejaVu Sans Mono", 1, 9)); // NOI18N
        jTextFieldBits.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldBits.setText("0000 0000 0000 0000:0000 0000 0000 0000:0000 0000 0000 0000:0000 0000 0000 0000");
        jTextFieldBits.setToolTipText("Change colors from  IPv6SubnetCalculatorInfo.xml  file");
        jTextFieldBits.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("List.selectionBackground")));
        jTextFieldBits.setCaretPosition(0);

        jLabel6.setText("/ ");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jSliderPrefix.setMaximum(64);
        jSliderPrefix.setMinimum(1);
        jSliderPrefix.setValue(1);
        jSliderPrefix.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderPrefixStateChanged(evt);
            }
        });

        jSliderSubnet.setMaximum(64);
        jSliderSubnet.setMinimum(1);
        jSliderSubnet.setValue(1);
        jSliderSubnet.setEnabled(false);
        jSliderSubnet.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderSubnetStateChanged(evt);
            }
        });

        jCheckBoxSubnet.setText("Subnet");
        jCheckBoxSubnet.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckBoxSubnet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSubnetActionPerformed(evt);
            }
        });

        jLabel7.setText("/ ");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel8.setText("Prefix:");

        jLabel9.setText("Mask:");

        jTextFieldMask.setEditable(false);
        jTextFieldMask.setText("mask");
        jTextFieldMask.setToolTipText("Mask Value (128bits)");

        jButtonListPrefixes.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jButtonListPrefixes.setText("|<  Prefixes");
        jButtonListPrefixes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonListPrefixesActionPerformed(evt);
            }
        });

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setCellRenderer(new MyCellRenderer());
        jList1.setComponentPopupMenu(jPopupMenuMainForm);
        jList1.setDoubleBuffered(true);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 207, 229)));

        jLabel10.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(9, 160, 254));
        jLabel10.setText("_");

        jLabeldbstatus.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        jLabeldbstatus.setForeground(new java.awt.Color(9, 160, 254));
        jLabeldbstatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabeldbstatus.setText("db=Down");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabeldbstatus)
                .addGap(48, 48, 48))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabeldbstatus)))
        );

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

        jCheckBoxEnd.setText("End?");
        jCheckBoxEnd.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckBoxEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxEndActionPerformed(evt);
            }
        });

        jTextFieldAddrSpaceNo.setEditable(false);
        jTextFieldAddrSpaceNo.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        jTextFieldAddrSpaceNo.setFont(new java.awt.Font("Cantarell", 0, 9)); // NOI18N
        jTextFieldAddrSpaceNo.setText("#18446744073709551616");
        jTextFieldAddrSpaceNo.setToolTipText("Address Space Number (Global Routing Prefix Number)");
        jTextFieldAddrSpaceNo.setBorder(null);
        jTextFieldAddrSpaceNo.setPreferredSize(new java.awt.Dimension(140, 13));

        listcount.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        listcount.setForeground(new java.awt.Color(9, 160, 254));
        listcount.setText("_");

        jTextFieldWildcard.setEditable(false);
        jTextFieldWildcard.setText("wildcardmask");
        jTextFieldWildcard.setToolTipText("WildCard Mask Value (128bits)");

        jProgressBar1.setStringPainted(true);

        jMenuFile.setMnemonic('F');
        jMenuFile.setText("File");

        jMenuItemSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemSaveAs.setMnemonic('S');
        jMenuItemSaveAs.setText("Save As Text");
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.add(jSeparator1);

        jMenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItemExit.setMnemonic('x');
        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar1.add(jMenuFile);

        jMenuTools.setMnemonic('T');
        jMenuTools.setText("Tools");
        jMenuTools.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jMenuToolsStateChanged(evt);
            }
        });

        jMenuItemListSelectedRange.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        jMenuItemListSelectedRange.setText("List Selected Range");
        jMenuItemListSelectedRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemListSelectedRangeActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemListSelectedRange);

        jMenuItemList64Prefixes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        jMenuItemList64Prefixes.setText("List /64 Prefixes");
        jMenuItemList64Prefixes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemList64PrefixesActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemList64Prefixes);

        jMenuItemList128Addresses.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        jMenuItemList128Addresses.setText("List /128 Addresses");
        jMenuItemList128Addresses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemList128AddressesActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemList128Addresses);

        jMenuItemListDNSRevZones.setText("List All DNS Reverse Zones");
        jMenuItemListDNSRevZones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemListDNSRevZonesActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemListDNSRevZones);

        jMenuItemWorkWithSelected.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemWorkWithSelected.setText("work with selected Prefix");
        jMenuItemWorkWithSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemWorkWithSelectedActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemWorkWithSelected);
        jMenuTools.add(jSeparator2);

        jMenuItemWhoisQuery.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItemWhoisQuery.setText("whois Query");
        jMenuItemWhoisQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemWhoisQueryActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemWhoisQuery);

        jMenuItemCompress.setText("Compress/Uncompress address");
        jMenuItemCompress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCompressActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemCompress);

        jMenuItemASNconvert.setText("ASN plain/dot Conversion");
        jMenuItemASNconvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemASNconvertActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemASNconvert);

        jMenuItemServiceNames.setText("Service Names Port Numbers");
        jMenuItemServiceNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemServiceNamesActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemServiceNames);

        jMenuBar1.add(jMenuTools);

        jMenuGoto.setMnemonic('G');
        jMenuGoto.setText("Goto...");

        jMenuItemGotoAddrSpace.setText("Addr.Space Number");
        jMenuItemGotoAddrSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGotoAddrSpaceActionPerformed(evt);
            }
        });
        jMenuGoto.add(jMenuItemGotoAddrSpace);

        jMenuItemGotoPrefix.setText("Prefix Number");
        jMenuItemGotoPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGotoPrefixActionPerformed(evt);
            }
        });
        jMenuGoto.add(jMenuItemGotoPrefix);

        jMenuItemGotoSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItemGotoSearch.setText("Search Prefix");
        jMenuItemGotoSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGotoSearchActionPerformed(evt);
            }
        });
        jMenuGoto.add(jMenuItemGotoSearch);

        jMenuBar1.add(jMenuGoto);

        jMenuDatabase.setMnemonic('D');
        jMenuDatabase.setText("Database");
        jMenuDatabase.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jMenuDatabaseStateChanged(evt);
            }
        });

        jMenuItemDBConnect.setText("Connect");
        jMenuItemDBConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDBConnectActionPerformed(evt);
            }
        });
        jMenuDatabase.add(jMenuItemDBConnect);

        jMenuItemDBClose.setText("Close");
        jMenuItemDBClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDBCloseActionPerformed(evt);
            }
        });
        jMenuDatabase.add(jMenuItemDBClose);

        jMenuItemDBStatus.setText("Status");
        jMenuItemDBStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDBStatusActionPerformed(evt);
            }
        });
        jMenuDatabase.add(jMenuItemDBStatus);
        jMenuDatabase.add(jSeparator3);

        jMenuItemDBOpenForm.setText("Open database form");
        jMenuItemDBOpenForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDBOpenFormActionPerformed(evt);
            }
        });
        jMenuDatabase.add(jMenuItemDBOpenForm);

        jMenuItemDBSendPrefix.setText("Send(Update) prefix to database");
        jMenuItemDBSendPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDBSendPrefixActionPerformed(evt);
            }
        });
        jMenuDatabase.add(jMenuItemDBSendPrefix);

        jMenuItemDBGetPrefix.setText("Get prefix info from database");
        jMenuItemDBGetPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDBGetPrefixActionPerformed(evt);
            }
        });
        jMenuDatabase.add(jMenuItemDBGetPrefix);

        jMenuItemModifyUpdateParent.setText("Modify(Update) Parent prefix");
        jMenuItemModifyUpdateParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemModifyUpdateParentActionPerformed(evt);
            }
        });
        jMenuDatabase.add(jMenuItemModifyUpdateParent);

        jMenuItemDBPrefixSubLevels.setText("Prefix subLevels");
        jMenuItemDBPrefixSubLevels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDBPrefixSubLevelsActionPerformed(evt);
            }
        });
        jMenuDatabase.add(jMenuItemDBPrefixSubLevels);

        jMenuItemListAllParents.setMnemonic('L');
        jMenuItemListAllParents.setText("List All Parent prefixes");
        jMenuItemListAllParents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemListAllParentsActionPerformed(evt);
            }
        });
        jMenuDatabase.add(jMenuItemListAllParents);

        jMenuItemDBStats.setText("Statistics/Utilization of this range");
        jMenuItemDBStats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDBStatsActionPerformed(evt);
            }
        });
        jMenuDatabase.add(jMenuItemDBStats);

        jMenuBar1.add(jMenuDatabase);

        jMenuHelp.setMnemonic('H');
        jMenuHelp.setText("Help");

        jMenuItemAbout.setMnemonic('A');
        jMenuItemAbout.setText("About...");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBar1.add(jMenuHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jCheckBox128bits)
                    .addComponent(jCheckBoxSubnet)
                    .addComponent(jLabel8)
                    .addComponent(jCheckBoxEnd))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel1)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButtonListPrefixes)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonListBack)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonListForward)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonListLast)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextFieldMask, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextFieldWildcard, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(listcount)
                        .addGap(24, 24, 24))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextFieldStartAddr, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldIPv6Addr, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                            .addComponent(jTextFieldInputAddress, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldEndAddr))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jButtonPrevAddrSpace)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonNextAddrSpace))
                                .addComponent(jButtonReset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                                .addComponent(jButtonCalculate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jTextFieldAddrSpaceNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextFieldBits, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSliderPrefix, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSliderSubnet, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6))))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldInputAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jButtonCalculate, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldIPv6Addr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonReset, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldStartAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonNextAddrSpace, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPrevAddrSpace, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldAddrSpaceNo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldEndAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel8)
                                .addComponent(jSliderPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6))
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSliderSubnet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBoxSubnet)))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jCheckBox128bits)
                    .addComponent(jTextFieldBits))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTextFieldWildcard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCheckBoxEnd)
                        .addComponent(jButtonListPrefixes, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonListBack, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonListForward, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonListLast, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(listcount)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        if (JOptionPane.showConfirmDialog(getRootPane(), "Exit Application?", "Exit", 0) == 0) {
            WriteInfoXML();
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
        saveTxt = new SaveAsTxt(StartEnd, this.jCheckBox128bits.isSelected(), false);
        saveTxt.setVisible(true);
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed

    private void jSliderPrefixStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderPrefixStateChanged
        int si = this.jSliderPrefix.getValue();
        this.jSliderSubnet.setValue(si);
        sd1Value = si;
        this.jLabel6.setText("/" + Integer.toString(si));
        this.jButtonListBack.setEnabled(false);
        this.jButtonListForward.setEnabled(false);
        this.jButtonListLast.setEnabled(false);
        this.jCheckBoxEnd.setEnabled(false);
        listcount.setVisible(false);
        this.jList1.setModel(new DefaultListModel<String>());
        this.jProgressBar1.setValue(0);
        this.jProgressBar1.setString("0%");

        Calculate(this.jTextFieldInputAddress.getText());
        UpdateStatus();
        UpdateCount();
        AddHighlights();
        MaskValue();
    }//GEN-LAST:event_jSliderPrefixStateChanged

    private void jSliderSubnetStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderSubnetStateChanged
        jLabel7.setText("/" + Integer.toString(jSliderSubnet.getValue()));
        sd2Value = jSliderSubnet.getValue();
        this.jCheckBoxEnd.setEnabled(false);

        this.jButtonListBack.setEnabled(false);
        this.jButtonListForward.setEnabled(false);
        this.jButtonListLast.setEnabled(false);
        this.jCheckBoxEnd.setEnabled(false);
        listcount.setVisible(false);

        this.jList1.setModel(new DefaultListModel<String>());
        this.jProgressBar1.setValue(0);
        this.jProgressBar1.setString("0%");

        if ((int) jSliderSubnet.getValue() - (int) jSliderPrefix.getValue() < 0) {
            jSliderSubnet.setValue(jSliderPrefix.getValue());
            return;
        }

        Calculate(this.jTextFieldInputAddress.getText());
        UpdateStatus();
        UpdateCount();
        AddHighlights();
        MaskValue();
    }//GEN-LAST:event_jSliderSubnetStateChanged

    private void jCheckBox128bitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox128bitsActionPerformed
        if (this.jCheckBox128bits.isSelected()) {
            ExpandSize();
        } else {
            DefaultSize();
        }
        AddHighlights();
        this.jButtonListPrefixesActionPerformed(null);
    }//GEN-LAST:event_jCheckBox128bitsActionPerformed

    private void jCheckBoxSubnetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSubnetActionPerformed
        if (this.jCheckBoxSubnet.isSelected()) {
            this.jSliderSubnet.setEnabled(true);
            this.jButtonListPrefixes.setEnabled(true);
        } else {
            this.jSliderSubnet.setEnabled(false);
            this.jButtonListPrefixes.setEnabled(false);
            this.jButtonListBack.setEnabled(false);
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);
            this.jCheckBoxEnd.setEnabled(false);

            this.jList1.setModel(new DefaultListModel<String>());
        }
    }//GEN-LAST:event_jCheckBoxSubnetActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        this.dim = this.getSize();
    }//GEN-LAST:event_formComponentResized

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        this.lastPos = new java.awt.Point(evt.getX(), evt.getY());
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if (SwingUtilities.isRightMouseButton(evt)) {
            this.setLocation(this.getLocation().x + evt.getX() - this.lastPos.x,
                    this.getLocation().y + evt.getY() - this.lastPos.y);
        }
    }//GEN-LAST:event_formMouseDragged

    private void jCheckBoxEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxEndActionPerformed
        if (this.jList1.getModel().getSize() <= 0) {
            return;
        }

        String first = this.jList1.getModel().getElementAt(0).split(" ")[1].split("/")[0];
        SEaddress tmpse = new SEaddress();
        tmpse.Start = v6ST.FormalizeAddr(first);
        tmpse = v6ST.Subnetting(tmpse, this.jCheckBox128bits.isSelected());

        this.jList1.setModel(new DefaultListModel<String>());

        if (!this.jCheckBox128bits.isSelected() && (int) this.jSliderSubnet.getValue() == 64
                || this.jCheckBox128bits.isSelected() && (int) this.jSliderSubnet.getValue() == 128) {
            this.jCheckBoxEnd.setSelected(false);
            this.jCheckBoxEnd.setEnabled(false);
        } else {
            this.jCheckBoxEnd.setEnabled(true);
        }

        tmpse.slash = (int) this.jSliderPrefix.getValue();
        tmpse.subnetslash = (int) this.jSliderSubnet.getValue();
        tmpse.upto = upto;
        tmpse.UpperLimitAddress = StartEnd.End;

        subnets = v6ST.ListFirstPage(tmpse, this.jCheckBox128bits.isSelected(), this.jCheckBoxEnd.isSelected());
        page.End = subnets.End;

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        UpdateCount();
    }//GEN-LAST:event_jCheckBoxEndActionPerformed

    private void jButtonCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCalculateActionPerformed
        this.jList1.setModel(new DefaultListModel<String>());
        Calculate(this.jTextFieldInputAddress.getText());
        AddHighlights();

        if (this.jCheckBoxSubnet.isSelected() && this.jCheckBoxSubnet.isEnabled()) {
            this.jButtonListPrefixesActionPerformed(null);
            listcount.setVisible(true);
        }
    }//GEN-LAST:event_jButtonCalculateActionPerformed

    private void jButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed
        this.jCheckBoxSubnet.setSelected(false);
        this.jCheckBoxEnd.setSelected(false);
        this.jSliderPrefix.setValue(1);
        this.jSliderPrefix.setEnabled(false);
        this.jSliderSubnet.setValue(1);
        this.jSliderSubnet.setEnabled(false);
        this.listcount.setVisible(false);
        this.jTextFieldIPv6Addr.setText("");
        jTextFieldStartAddr.setText("");
        jTextFieldEndAddr.setText("");
        this.jTextFieldAddrSpaceNo.setText("");

        this.jButtonListPrefixes.setEnabled(false);
        this.jButtonListBack.setEnabled(false);
        this.jButtonListForward.setEnabled(false);
        this.jButtonListLast.setEnabled(false);
        this.jButtonPrevAddrSpace.setEnabled(false);
        this.jButtonNextAddrSpace.setEnabled(false);

        this.jList1.setModel(new DefaultListModel<String>());
        this.jProgressBar1.setValue(0);
        this.jProgressBar1.setString("0%");

        this.jTextFieldMask.setText("");
        this.jCheckBoxSubnet.setEnabled(false);
        this.jCheckBoxEnd.setEnabled(false);
    }//GEN-LAST:event_jButtonResetActionPerformed

    private void jButtonListPrefixesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListPrefixesActionPerformed
        this.jCheckBoxEnd.setEnabled(true);

        if (!this.jCheckBox128bits.isSelected() && (int) this.jSliderSubnet.getValue() == 64
                || this.jCheckBox128bits.isSelected() && (int) this.jSliderSubnet.getValue() == 128) {
            this.jCheckBoxEnd.setSelected(false);
            this.jCheckBoxEnd.setEnabled(false);
        } else {
            this.jCheckBoxEnd.setEnabled(true);
        }

        listcount.setVisible(true);
        this.jList1.setModel(new DefaultListModel<String>());

        int delta = (int) this.jSliderSubnet.getValue() - (int) this.jSliderPrefix.getValue();

        StartEnd.slash = (int) this.jSliderPrefix.getValue();
        StartEnd.subnetslash = (int) this.jSliderSubnet.getValue();
        StartEnd.upto = upto;

        subnets = v6ST.ListFirstPage(StartEnd, this.jCheckBox128bits.isSelected(), this.jCheckBoxEnd.isSelected());
        page.End = subnets.End;

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        BigInteger maxsub = BigInteger.ONE.shiftLeft(delta);
        if (maxsub.compareTo(BigInteger.valueOf(upto)) <= 0) {
            this.jButtonListBack.setEnabled(false);
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);
        } else {
            this.jButtonListBack.setEnabled(false);
            this.jButtonListForward.setEnabled(true);
            this.jButtonListLast.setEnabled(true);
        }
        UpdateCount();
    }//GEN-LAST:event_jButtonListPrefixesActionPerformed

    private void jButtonPrevAddrSpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrevAddrSpaceActionPerformed
        listcount.setVisible(false);
        this.jProgressBar1.setValue(0);
        this.jProgressBar1.setString("0%");

        if (StartEnd.Start.equals(BigInteger.ZERO)) {
            StartEnd.End = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
        } else {
            StartEnd.End = StartEnd.Start.subtract(BigInteger.ONE);
        }

        StartEnd = v6ST.PrevSpace(StartEnd, this.jCheckBox128bits.isSelected());

        DisplayPrevNextSpace();
        UpdatePrintBin();
        AddHighlights();
    }//GEN-LAST:event_jButtonPrevAddrSpaceActionPerformed

    private void jButtonNextAddrSpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextAddrSpaceActionPerformed
        listcount.setVisible(false);
        this.jProgressBar1.setValue(0);
        this.jProgressBar1.setString("0%");

        if (StartEnd.End.equals(new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16))) {
            StartEnd.Start = BigInteger.ZERO;
        } else {
            StartEnd.Start = StartEnd.End.add(BigInteger.ONE);
        }

        StartEnd = v6ST.NextSpace(StartEnd, this.jCheckBox128bits.isSelected());

        DisplayPrevNextSpace();
        UpdatePrintBin();
        AddHighlights();

    }//GEN-LAST:event_jButtonNextAddrSpaceActionPerformed

    private void jButtonListBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListBackActionPerformed
        this.jList1.setModel(new DefaultListModel<String>());

        subnets.slash = (int) this.jSliderPrefix.getValue();
        subnets.subnetslash = (int) this.jSliderSubnet.getValue();
        subnets.upto = upto;
        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;

        subnets.End = page.End = page.Start.subtract(BigInteger.ONE);
        subnets = v6ST.ListPageBackward(subnets, this.jCheckBox128bits.isSelected(), this.jCheckBoxEnd.isSelected());
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
        this.jList1.setModel(new DefaultListModel<String>());

        subnets.slash = (int) this.jSliderPrefix.getValue();
        subnets.subnetslash = (int) this.jSliderSubnet.getValue();;
        subnets.upto = upto;
        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;

        subnets.Start = page.Start = page.End.add(BigInteger.ONE);
        subnets = v6ST.ListPageForward(subnets, this.jCheckBox128bits.isSelected(), this.jCheckBoxEnd.isSelected());
        page.End = subnets.End;

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        if (subnets.End.equals(StartEnd.End)) {
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);
            this.jButtonListBack.setEnabled(true);
            UpdateCount();
            return;
        } else {
            this.jButtonListBack.setEnabled(true);
            this.jButtonListLast.setEnabled(true);
        }
        UpdateCount();
    }//GEN-LAST:event_jButtonListForwardActionPerformed

    private void jButtonListLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListLastActionPerformed
        this.jList1.setModel(new DefaultListModel<String>());

        subnets.slash = (int) this.jSliderPrefix.getValue();
        subnets.subnetslash = (int) this.jSliderSubnet.getValue();
        subnets.upto = upto;
        subnets.LowerLimitAddress = StartEnd.LowerLimitAddress;
        subnets.UpperLimitAddress = StartEnd.UpperLimitAddress;

        subnets.End = page.End = StartEnd.UpperLimitAddress;
        subnets = v6ST.ListLastPage(subnets, this.jCheckBox128bits.isSelected(), this.jCheckBoxEnd.isSelected());
        page.Start = subnets.Start;

        this.jList1.setListData(subnets.liste.toArray(String[]::new));

        this.jButtonListForward.setEnabled(false);
        this.jButtonListLast.setEnabled(false);
        this.jButtonListBack.setEnabled(true);

        UpdateCount();
    }//GEN-LAST:event_jButtonListLastActionPerformed

    private void jMenuItemListSelectedRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemListSelectedRangeActionPerformed
        if (this.jList1.getSelectedValue() != null && this.jList1.getSelectedValue().trim() != "") {

            StartEnd.slash = (int) this.jSliderPrefix.getValue();
            StartEnd.subnetslash = (int) this.jSliderSubnet.getValue();

            ListSubnetRange lsr = new ListSubnetRange(StartEnd, this.jList1.getSelectedValue(),
                    this.jCheckBox128bits.isSelected(), MySQLconnection, dbserverInfo, true);
            listSubnetRange.add(lsr);
            lsr.setVisible(true);
            if (MySQLconnection != null) {
                lsr.jLabeldbstatus.setText("db=Up ");
            }
        }
    }//GEN-LAST:event_jMenuItemListSelectedRangeActionPerformed

    private void jMenuItemList64PrefixesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemList64PrefixesActionPerformed
        if ((!this.jCheckBox128bits.isSelected() && (int) this.jSliderSubnet.getValue() == 64)
                || (this.jCheckBox128bits.isSelected() && (int) this.jSliderSubnet.getValue() == 128)
                || this.jList1.getSelectedValue() == null || this.jList1.getSelectedValue().trim() == "") {
            return;
        } else {
            StartEnd.slash = (int) this.jSliderPrefix.getValue();
            StartEnd.subnetslash = (int) this.jSliderSubnet.getValue();

            ListSubnetRange lsr = new ListSubnetRange(StartEnd,
                    this.jList1.getSelectedValue(), this.jCheckBox128bits.isSelected(),
                    MySQLconnection, dbserverInfo, false);

            listSubnetRange.add(lsr);
            lsr.setVisible(true);
            if (MySQLconnection != null) {
                lsr.jLabeldbstatus.setText("db=Up ");
            }
        }
    }//GEN-LAST:event_jMenuItemList64PrefixesActionPerformed

    private void jMenuItemList128AddressesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemList128AddressesActionPerformed
        jMenuItemList64PrefixesActionPerformed(null);
    }//GEN-LAST:event_jMenuItemList128AddressesActionPerformed

    private void jMenuItemListDNSRevZonesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemListDNSRevZonesActionPerformed
        if (this.jList1.getModel().getSize() > 0) {
            StartEnd.slash = (int) this.jSliderPrefix.getValue();
            StartEnd.subnetslash = (int) this.jSliderSubnet.getValue();

            this.listDnsr = new ListDnsReverses(StartEnd, this.jCheckBox128bits.isSelected());
            this.listDnsr.setVisible(true);
        }
    }//GEN-LAST:event_jMenuItemListDNSRevZonesActionPerformed

    private void jMenuItemWorkWithSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWorkWithSelectedActionPerformed
        if ((this.jList1.getModel().getSize() > 0)
                && this.jList1.getSelectedValue() != null
                && this.jList1.getSelectedValue().trim() != "") {
            String selected = this.jList1.getSelectedValue().split(" ")[1];
            String snet = selected.split("/")[0];
            int plen = Integer.parseInt(selected.split("/")[1]);
            this.jTextFieldInputAddress.setText(snet);
            this.jSliderPrefix.setValue((int) this.jSliderSubnet.getValue());
            this.jButtonListBack.setEnabled(false);
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);

            this.jList1.setModel(new DefaultListModel<String>());

            Calculate(snet);
            StartEnd.slash = StartEnd.subnetslash = (int) this.jSliderPrefix.getValue();
            this.jButtonListPrefixesActionPerformed(null);
        }
    }//GEN-LAST:event_jMenuItemWorkWithSelectedActionPerformed

    private void jMenuItemWhoisQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWhoisQueryActionPerformed
        String s = this.jList1.getSelectedValue();

        if (s != null && s.trim() != "") {
            s = s.split(" ")[1];
            this.whoisquery = new WhoisQuery(s);
        } else {
            this.whoisquery = new WhoisQuery(v6ST.CompressAddress(this.jTextFieldIPv6Addr.getText()));
        }
        this.whoisquery.setVisible(true);
    }//GEN-LAST:event_jMenuItemWhoisQueryActionPerformed

    private void jMenuItemCompressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCompressActionPerformed
        String sin = this.jTextFieldIPv6Addr.getText().trim();

        if (sin != "" && sin != null) {
            this.compressAddr = new CompressAddress(sin);
            this.compressAddr.setVisible(true);
        } else {
            this.compressAddr = new CompressAddress("");
            this.compressAddr.setVisible(true);
        }
    }//GEN-LAST:event_jMenuItemCompressActionPerformed

    private void jMenuItemASNconvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemASNconvertActionPerformed
        this.asnpldot = new ASNumberPlainDot();
        this.asnpldot.setVisible(true);
    }//GEN-LAST:event_jMenuItemASNconvertActionPerformed

    private void jMenuItemServiceNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemServiceNamesActionPerformed
        this.snamepnumbers = new ServiceNamesPortNumbers();
        this.snamepnumbers.setVisible(true);
    }//GEN-LAST:event_jMenuItemServiceNamesActionPerformed

    private void jMenuItemGotoAddrSpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGotoAddrSpaceActionPerformed
        String currentasnidx = this.jTextFieldAddrSpaceNo.getText().replace("#", "");
        gotoasnValue = StartEnd.subnetidx;

        Goto gasn = new Goto(this, Boolean.TRUE, ID, 0);
        gasn.setVisible(true);

        if (currentasnidx.equals(String.valueOf(gotoasnValue))) {
            return;
        } else {
            this.jTextFieldAddrSpaceNo.setText("#" + String.valueOf(gotoasnValue));
        }

        StartEnd.subnetidx = gotoasnValue;

        StartEnd = v6ST.GoToAddrSpace(StartEnd, this.jCheckBox128bits.isSelected());
        StartEnd.Resultv6 = StartEnd.Start;

        this.jList1.setModel(new DefaultListModel<String>());

        String s = v6ST.Kolonlar(StartEnd.Start);
        this.jTextFieldIPv6Addr.setText(s);
        if (this.jCheckBox128bits.isSelected()) {
            s = s + "/" + String.valueOf((int) this.jSliderPrefix.getValue());
        }
        if (!this.jCheckBox128bits.isSelected()) {
            s = s.substring(0, 19) + "::/" + String.valueOf((int) this.jSliderPrefix.getValue());
        }

        this.jTextFieldStartAddr.setText(s);
        s = v6ST.Kolonlar(StartEnd.End);

        if (this.jCheckBox128bits.isSelected()) {
            s = s + "/" + String.valueOf((int) this.jSliderPrefix.getValue());
        }
        if (!this.jCheckBox128bits.isSelected()) {
            s = s.substring(0, 19) + "::/" + String.valueOf((int) this.jSliderPrefix.getValue());
        }
        this.jTextFieldEndAddr.setText(s);
        UpdatePrintBin();
        AddHighlights();
        this.jButtonListForward.setEnabled(false);
        this.jButtonListBack.setEnabled(false);
        this.jButtonListLast.setEnabled(false);
    }//GEN-LAST:event_jMenuItemGotoAddrSpaceActionPerformed

    private void jMenuItemGotoPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGotoPrefixActionPerformed
        if (this.jList1.getModel().getSize() == 0) {
            return;
        }

        String currentpfxidx = this.jList1.getModel().getElementAt(0).split(">")[0].replace("p", "");

        gotopfxValue = new BigInteger(currentpfxidx);

        Goto gasn = new Goto(this, Boolean.TRUE, ID, 1);
        gasn.setVisible(true);

        if (currentpfxidx.equals(String.valueOf(gotopfxValue))) {
            return;
        }

        String ss = "", se = "";
        int count = 0;

        subnets.subnetidx = gotopfxValue;
        subnets.slash = (int) this.jSliderPrefix.getValue();
        subnets.subnetslash = (int) this.jSliderSubnet.getValue();
        subnets.Start = StartEnd.Start;
        subnets.Resultv6 = StartEnd.Resultv6;

        subnets = v6ST.GoToSubnet(subnets, this.jCheckBox128bits.isSelected());

        page.Start = subnets.Start;
        page.End = BigInteger.ZERO;

        if (subnets.End.equals(StartEnd.End)) {
            this.jButtonListForward.setEnabled(false);
        }

        DefaultListModel<String> model = new DefaultListModel<String>();
        this.jList1.setModel(model);

        for (count = 0; count < upto; count++) {
            subnets = v6ST.Subnetting(subnets, this.jCheckBox128bits.isSelected());

            if (this.jCheckBox128bits.isSelected()) {
                ss = v6ST.Kolonlar(subnets.Start);
                ss = v6ST.CompressAddress(ss);
                ss = "p" + subnets.subnetidx + "> " + ss + "/"
                        + String.valueOf((int) this.jSliderSubnet.getValue());

                model.addElement(ss);

                if (this.jCheckBoxEnd.isSelected()) {
                    se = v6ST.Kolonlar(subnets.End);
                    se = v6ST.CompressAddress(se);
                    se = "e" + subnets.subnetidx + "> " + se + "/"
                            + String.valueOf((int) this.jSliderSubnet.getValue());

                    model.addElement(se);
                    model.addElement(" ");
                }

            } else if (!this.jCheckBox128bits.isSelected()) {
                ss = v6ST.Kolonlar(subnets.Start);
                ss = ss.substring(0, 19);
                ss = ss + "::";
                ss = v6ST.CompressAddress(ss);
                ss = "p" + subnets.subnetidx + "> " + ss + "/"
                        + String.valueOf((int) this.jSliderSubnet.getValue());

                model.addElement(ss);

                if (this.jCheckBoxEnd.isSelected()) {
                    se = v6ST.Kolonlar(subnets.End);
                    se = se.substring(0, 19);
                    se = se + "::";
                    se = v6ST.CompressAddress(se);
                    se = "e" + subnets.subnetidx + "> " + se + "/"
                            + String.valueOf((int) this.jSliderSubnet.getValue());

                    model.addElement(se);
                    model.addElement(" ");
                }
            }
            if (subnets.End.equals(StartEnd.End)) {
                this.jButtonListForward.setEnabled(false);
                break;
            } else {
                subnets.Start = subnets.End.add(BigInteger.ONE);
            }
        }
        page.End = subnets.End;

        if (gotopfxValue.equals(BigInteger.ZERO)) {
            this.jButtonListBack.setEnabled(false);
        } else {
            this.jButtonListBack.setEnabled(true);
        }

        if (subnets.subnetidx.equals(prefixmax.subtract(BigInteger.ONE))) {
            this.jButtonListForward.setEnabled(false);
            this.jButtonListLast.setEnabled(false);
        } else {
            this.jButtonListForward.setEnabled(true);
            this.jButtonListLast.setEnabled(true);
        }
        UpdateCount();
        UpdatePrintBin();
        AddHighlights();
    }//GEN-LAST:event_jMenuItemGotoPrefixActionPerformed

    private void jMenuItemGotoSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGotoSearchActionPerformed
        if (this.jList1.getModel().getSize() == 0) {
            return;
        }

        Goto gasn = new Goto(this, Boolean.TRUE, ID, 2);
        gasn.setVisible(true);

        if (findpfx.trim().equals("")
                || (int) this.jSliderPrefix.getValue() == (int) this.jSliderSubnet.getValue()) {
            return;
        }

        SEaddress seaddr = new SEaddress();
        seaddr.slash = (int) this.jSliderPrefix.getValue();
        seaddr.subnetslash = (int) this.jSliderSubnet.getValue();
        String ss = "", se = "";
        int count = 0;

        BigInteger Resv6 = v6ST.FormalizeAddr(findpfx);
        seaddr.Resultv6 = seaddr.Start = Resv6;

        if (seaddr.Resultv6.compareTo(StartEnd.Start) >= 0
                && seaddr.Resultv6.compareTo(StartEnd.End) <= 0) {
            // inside
            BigInteger before = seaddr.Resultv6;

            seaddr = v6ST.FindPrefixIndex(seaddr, this.jCheckBox128bits.isSelected());

            subnets.subnetidx = seaddr.subnetidx;
            subnets.slash = (int) this.jSliderPrefix.getValue();
            subnets.subnetslash = (int) this.jSliderSubnet.getValue();
            subnets.Start = StartEnd.Start;
            subnets.Resultv6 = StartEnd.Resultv6;

            subnets = v6ST.GoToSubnet(subnets, this.jCheckBox128bits.isSelected());

            if (before.equals(subnets.Start)) {
                page.Start = subnets.Start;
                page.End = BigInteger.ZERO;

                if (subnets.End.equals(StartEnd.End)) {
                    this.jButtonListForward.setEnabled(false);
                }

                DefaultListModel<String> model = new DefaultListModel<String>();
                this.jList1.setModel(model);

                for (count = 0; count < upto; count++) {
                    subnets = v6ST.Subnetting(subnets, this.jCheckBox128bits.isSelected());

                    if (this.jCheckBox128bits.isSelected()) {
                        ss = v6ST.Kolonlar(subnets.Start);
                        ss = v6ST.CompressAddress(ss);
                        ss = "p" + subnets.subnetidx + "> " + ss + "/"
                                + String.valueOf((int) this.jSliderSubnet.getValue());

                        model.addElement(ss);

                        if (this.jCheckBoxEnd.isSelected()) {
                            se = v6ST.Kolonlar(subnets.End);
                            se = v6ST.CompressAddress(se);
                            se = "e" + subnets.subnetidx + "> " + se + "/"
                                    + String.valueOf((int) this.jSliderSubnet.getValue());

                            model.addElement(se);
                            model.addElement(" ");
                        }
                    } else if (!this.jCheckBox128bits.isSelected()) {
                        ss = v6ST.Kolonlar(subnets.Start);
                        ss = ss.substring(0, 19);
                        ss += "::";
                        ss = v6ST.CompressAddress(ss);
                        ss = "p" + subnets.subnetidx + "> " + ss + "/"
                                + String.valueOf((int) this.jSliderSubnet.getValue());

                        model.addElement(ss);

                        if (this.jCheckBoxEnd.isSelected()) {
                            se = v6ST.Kolonlar(subnets.End);
                            se = se.substring(0, 19);
                            se = se + "::";
                            se = v6ST.CompressAddress(se);
                            se = "e" + subnets.subnetidx + "> " + se + "/"
                                    + String.valueOf((int) this.jSliderSubnet.getValue());

                            model.addElement(se);
                            model.addElement(" ");
                        }
                    }

                    if (subnets.End.equals(StartEnd.End)) {
                        this.jButtonListForward.setEnabled(false);
                        break;
                    } else {
                        subnets.Start = subnets.End.add(BigInteger.ONE);
                    }
                }
                page.End = subnets.End;

                if (seaddr.subnetidx.equals(BigInteger.ZERO)) {
                    this.jButtonListBack.setEnabled(false);
                } else {
                    this.jButtonListBack.setEnabled(true);
                }
                if (subnets.subnetidx.equals(prefixmax.subtract(BigInteger.ONE))) {
                    this.jButtonListForward.setEnabled(false);
                    this.jButtonListLast.setEnabled(false);
                } else {
                    this.jButtonListForward.setEnabled(true);
                    this.jButtonListLast.setEnabled(true);
                }
                UpdateCount();
                UpdatePrintBin();
                AddHighlights();
            } else {
                MsgBox.Show(this, "Prefix Not Found!", "Error:");
            }
        } else {
            MsgBox.Show(this, "Out of [Start-End] interval!", "Error:");
        }
    }//GEN-LAST:event_jMenuItemGotoSearchActionPerformed

    private void jMenuItemDBConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDBConnectActionPerformed
        if (MySQLconnection == null) {

            DBConnectInfo dbConnectinfo = new DBConnectInfo(this, Boolean.TRUE);
            dbConnectinfo.setVisible(true);

            // will wait for JDialog window
            if (!dbConnectinfo.isCanceled) {
                if (dbserverInfo.ServerIP != null) {
                    ConnectToDBServer();

                    if (dbConnectinfo.isLaunchDBUI) {
                        this.jMenuItemDBSendPrefixActionPerformed(null);
                    }
                }
            }
        }
    }//GEN-LAST:event_jMenuItemDBConnectActionPerformed

    private void jMenuItemDBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDBCloseActionPerformed
        if (MySQLconnection != null) {
            if (JOptionPane.showConfirmDialog(this, "Database connection will be closed.\r\nConfirm?", "Close Database Connection", 2, 2) == 2) {
                return;
            } else {
                DBClose();
            }
        }
    }//GEN-LAST:event_jMenuItemDBCloseActionPerformed

    private void jMenuItemDBStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDBStatusActionPerformed
        if (MySQLconnection != null) {
            try {
                DatabaseMetaData dbs = MySQLconnection.getMetaData();
                String sc = "";
                if (!dbs.getConnection().isClosed()) {
                    sc = "Open";
                } else {
                    sc = "Closed";
                }

                String connStr
                        = dbserverInfo.ConnectionString.replaceAll("&", "&\r\n  ") + "\r\n";

                MsgBox.Show(this, "Connection Status: " + sc + "\r\n"
                        + "DatabaseName: " + dbserverInfo.DBname + "\r\n"
                        + "TableName: " + dbserverInfo.Tablename + "\r\n"
                        + "DB Server Version: " + dbs.getDatabaseProductVersion() + "\r\n"
                        + "Connected Username: " + dbs.getUserName() + "\r\n"
                        + "ConnectionString: " + connStr
                        + "Driver Name: " + dbs.getDriverName() + "\r\n"
                        + "Driver Version: " + dbs.getDriverVersion() + "\r\n",
                         "Information");
            } catch (SQLException ex) {
                UpdateDbStatus();
                Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(Level.SEVERE, null, ex);
                MsgBox.Show(this, ex.toString(), "Error:");
            }
        } else {
            MsgBox.Show(this, """
                            Connection Status: null\r
                            DB Server Prod.Version: null \r
                            Connected Username: null\r
                            ConnectionString: null\r
                            Driver Name: null\r
                            Driver Version: null\r
                            """, "Information");
        }
    }//GEN-LAST:event_jMenuItemDBStatusActionPerformed

    private void jMenuItemDBOpenFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDBOpenFormActionPerformed
        UpdateDbStatus();
        if (MySQLconnection != null) {
            this.jMenuItemDBSendPrefixActionPerformed(null);
        }
    }//GEN-LAST:event_jMenuItemDBOpenFormActionPerformed

    private void jMenuItemDBSendPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDBSendPrefixActionPerformed
        UpdateDbStatus();
        if (MySQLconnection != null) {
            String snet = "";
            short plen = 0;
            short parentpflen = (short) this.jSliderPrefix.getValue();

            if (this.jList1.getSelectedValue() != null && this.jList1.getSelectedValue().trim() != "") {
                String selected = this.jList1.getSelectedValue().split(" ")[1].trim();
                snet = selected.split("/")[0].trim();
                plen = Short.parseShort(selected.split("/")[1].trim());
            }

            DatabaseUI dbui = new DatabaseUI(snet, plen, parentpflen, MySQLconnection, dbserverInfo);

            dbUI.add(dbui);
            dbui.setVisible(true);
            dbui.jLabeldbstatus.setText("db=Up ");
        }
    }//GEN-LAST:event_jMenuItemDBSendPrefixActionPerformed

    private void jMenuItemDBGetPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDBGetPrefixActionPerformed
        UpdateDbStatus();
        if (MySQLconnection != null) {
            if (this.jList1.getSelectedValue() != null && this.jList1.getSelectedValue().trim() != "") {
                String selected = this.jList1.getSelectedValue().split(" ")[1].trim();

                GetPrefixInfoFromDB getpfxinfo = new GetPrefixInfoFromDB(selected, MySQLconnection, dbserverInfo);

                getPrefixInfo.add(getpfxinfo);

                getpfxinfo.setVisible(true);
            }
        }
    }//GEN-LAST:event_jMenuItemDBGetPrefixActionPerformed

    private void jMenuItemDBPrefixSubLevelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDBPrefixSubLevelsActionPerformed
        UpdateDbStatus();
        if (MySQLconnection != null) {
            if (this.jList1.getSelectedValue() != null && this.jList1.getSelectedValue().trim() != "") {
                String selected = this.jList1.getSelectedValue().split(" ")[1];
                String snet = selected.split("/")[0];
                short plen = Short.parseShort(selected.split("/")[1]);
                short ppflen = QuerySelectedPrefix(snet, plen);
                if (ppflen == 0) {
                    MsgBox.Show(this, "Prefix not found in the database.", "Information:");
                    return;
                } else if (ppflen < 0) {
                    MsgBox.Show(this, "Database connection error.", "Error:");
                    return;
                }
                String parentprefix = v6ST.FindParentNet(snet, ppflen, this.jCheckBox128bits.isSelected());

                PrefixSubLevels prefixSubs = new PrefixSubLevels(snet, plen, parentprefix,
                        this.jCheckBox128bits.isSelected(),
                        (int) this.jSliderPrefix.getValue(), (int) this.jSliderSubnet.getValue(),
                        MySQLconnection, dbserverInfo);

                prefixSublevels.add(prefixSubs);
                prefixSubs.setVisible(true);
                prefixSubs.jLabeldbstatus.setText("db=Up ");
            }
        }
    }//GEN-LAST:event_jMenuItemDBPrefixSubLevelsActionPerformed

    private void jMenuItemDBStatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDBStatsActionPerformed
        if (MySQLconnection != null) {
            if (!jTextFieldStartAddr.getText().equals("") && !jTextFieldEndAddr.getText().equals("")) {

                StatsUsage stat = new StatsUsage(jTextFieldStartAddr.getText(),
                        jTextFieldEndAddr.getText(), (short) this.jSliderPrefix.getValue(),
                        (short) this.jSliderSubnet.getValue(),
                        this.jCheckBox128bits.isSelected(), MySQLconnection, dbserverInfo);

                statsUsage.add(stat);
                stat.setVisible(true);
                stat.jLabeldbstatus.setText("db=Up ");
            }
        }
        UpdateDbStatus();
    }//GEN-LAST:event_jMenuItemDBStatsActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        About about = new About(this, Boolean.TRUE);
        about.setVisible(true);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuToolsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jMenuToolsStateChanged
        if (this.jList1.getModel().getSize() > 0) {
            this.jMenuItemListDNSRevZones.setEnabled(true);
            String s = this.jList1.getSelectedValue();

            if (s != null) {
                this.jMenuItemListSelectedRange.setEnabled(true);
                this.jMenuItemWorkWithSelected.setEnabled(true);
                if (((int) this.jSliderSubnet.getValue() == 64 && !this.jCheckBox128bits.isSelected())
                        || ((int) this.jSliderSubnet.getValue() == 128 && this.jCheckBox128bits.isSelected())) {
                    this.jMenuItemList64Prefixes.setEnabled(false);
                    this.jMenuItemList128Addresses.setEnabled(false);
                } else {
                    if (!this.jCheckBox128bits.isSelected()) {
                        this.jMenuItemList64Prefixes.setEnabled(true);
                    } else {
                        this.jMenuItemList64Prefixes.setEnabled(false);
                    }

                    if (this.jCheckBox128bits.isSelected()) {
                        this.jMenuItemList128Addresses.setEnabled(true);
                    } else {
                        this.jMenuItemList128Addresses.setEnabled(false);
                    }
                }
            } else {
                this.jMenuItemListSelectedRange.setEnabled(false);
                this.jMenuItemList64Prefixes.setEnabled(false);
                this.jMenuItemList128Addresses.setEnabled(false);
                this.jMenuItemWorkWithSelected.setEnabled(false);
            }
        } else {
            this.jMenuItemListSelectedRange.setEnabled(false);
            this.jMenuItemList64Prefixes.setEnabled(false);
            this.jMenuItemList128Addresses.setEnabled(false);
            this.jMenuItemListDNSRevZones.setEnabled(false);
            this.jMenuItemWorkWithSelected.setEnabled(false);
        }
    }//GEN-LAST:event_jMenuToolsStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        WriteInfoXML();
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

    private void contextListSelectedRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextListSelectedRangeActionPerformed
        jMenuItemListSelectedRangeActionPerformed(null);
    }//GEN-LAST:event_contextListSelectedRangeActionPerformed

    private void contextList64PrefixesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextList64PrefixesActionPerformed
        jMenuItemList64PrefixesActionPerformed(null);
    }//GEN-LAST:event_contextList64PrefixesActionPerformed

    private void contextList128AddressesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextList128AddressesActionPerformed
        jMenuItemList64PrefixesActionPerformed(null);
    }//GEN-LAST:event_contextList128AddressesActionPerformed

    private void contextListDNSRevZonesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextListDNSRevZonesActionPerformed
        jMenuItemListDNSRevZonesActionPerformed(null);
    }//GEN-LAST:event_contextListDNSRevZonesActionPerformed

    private void contextWorkWithSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextWorkWithSelectedActionPerformed
        jMenuItemWorkWithSelectedActionPerformed(null);
    }//GEN-LAST:event_contextWorkWithSelectedActionPerformed

    private void contextGotoPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextGotoPrefixActionPerformed
        jMenuItemGotoPrefixActionPerformed(null);
    }//GEN-LAST:event_contextGotoPrefixActionPerformed

    private void contextGotoSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextGotoSearchActionPerformed
        jMenuItemGotoSearchActionPerformed(null);
    }//GEN-LAST:event_contextGotoSearchActionPerformed

    private void contextWhoisQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextWhoisQueryActionPerformed
        jMenuItemWhoisQueryActionPerformed(null);
    }//GEN-LAST:event_contextWhoisQueryActionPerformed

    private void contextDBSendPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextDBSendPrefixActionPerformed
        this.jMenuItemDBSendPrefixActionPerformed(null);
    }//GEN-LAST:event_contextDBSendPrefixActionPerformed

    private void contextDBGetPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextDBGetPrefixActionPerformed
        jMenuItemDBGetPrefixActionPerformed(null);
    }//GEN-LAST:event_contextDBGetPrefixActionPerformed

    private void contextDBPrefixSubLevelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextDBPrefixSubLevelsActionPerformed
        jMenuItemDBPrefixSubLevelsActionPerformed(null);
    }//GEN-LAST:event_contextDBPrefixSubLevelsActionPerformed

    private void contextDBStatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextDBStatsActionPerformed
        jMenuItemDBStatsActionPerformed(null);
    }//GEN-LAST:event_contextDBStatsActionPerformed

    private void contextSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextSaveAsActionPerformed
        if (this.jList1.getModel().getSize() > 0) {
            jMenuItemSaveAsActionPerformed(null);
        }
    }//GEN-LAST:event_contextSaveAsActionPerformed

    private void jTextFieldInputAddressKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldInputAddressKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.jButtonCalculateActionPerformed(null);
        }
    }//GEN-LAST:event_jTextFieldInputAddressKeyPressed

    private void jPopupMenuMainFormPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenuMainFormPopupMenuWillBecomeVisible
        if (this.jList1.getModel().getSize() > 0) {
            this.contextListDNSRevZones.setEnabled(true);
            this.contextGotoPrefix.setEnabled(true);
            this.contextGotoSearch.setEnabled(true);
            this.contextWhoisQuery.setEnabled(true);
            this.contextSaveAs.setEnabled(true);

            if (this.jList1.getSelectedValue() != null) {
                this.contextListSelectedRange.setEnabled(true);
                this.contextWorkWithSelected.setEnabled(true);

                if (MySQLconnection != null) {
                    this.contextDBGetPrefix.setEnabled(true);
                    this.contextDBPrefixSubLevels.setEnabled(true);
                    this.contextModifyUpdateParent.setEnabled(true);
                    this.contextDBSendPrefix.setEnabled(true);
                } else {
                    this.contextDBGetPrefix.setEnabled(false);
                    this.contextDBPrefixSubLevels.setEnabled(false);
                    this.contextModifyUpdateParent.setEnabled(false);
                    this.contextDBSendPrefix.setEnabled(false);
                }

                if (((int) this.jSliderSubnet.getValue() == 64 && !this.jCheckBox128bits.isSelected())
                        || ((int) this.jSliderSubnet.getValue() == 128 && this.jCheckBox128bits.isSelected())) {
                    this.contextList64Prefixes.setEnabled(false);
                    this.contextList128Addresses.setEnabled(false);
                } else {
                    if (!this.jCheckBox128bits.isSelected()) {
                        this.contextList64Prefixes.setEnabled(true);
                    } else {
                        this.contextList64Prefixes.setEnabled(false);
                    }

                    if (this.jCheckBox128bits.isSelected()) {
                        this.contextList128Addresses.setEnabled(true);
                    } else {
                        this.contextList128Addresses.setEnabled(false);
                    }
                }
            } else {
                this.contextListSelectedRange.setEnabled(false);
                this.contextList64Prefixes.setEnabled(false);
                this.contextList128Addresses.setEnabled(false);
                this.contextWorkWithSelected.setEnabled(false);
                this.contextDBGetPrefix.setEnabled(false);
                this.contextModifyUpdateParent.setEnabled(false);
                this.contextDBPrefixSubLevels.setEnabled(false);
                this.contextDBSendPrefix.setEnabled(false);
            }

        } else {
            this.contextListSelectedRange.setEnabled(false);
            this.contextDBGetPrefix.setEnabled(false);
            this.contextModifyUpdateParent.setEnabled(false);
            this.contextDBPrefixSubLevels.setEnabled(false);
            this.contextDBSendPrefix.setEnabled(false);
            this.contextGotoPrefix.setEnabled(false);
            this.contextGotoSearch.setEnabled(false);
            this.contextList128Addresses.setEnabled(false);
            this.contextList64Prefixes.setEnabled(false);
            this.contextListDNSRevZones.setEnabled(false);
            this.contextSaveAs.setEnabled(false);
            this.contextWorkWithSelected.setEnabled(false);
        }
        if (MySQLconnection != null) {
            this.contextDBStats.setEnabled(true);
            this.contextDBListAllParents.setEnabled(true);
        } else {
            this.contextDBStats.setEnabled(false);
            this.contextDBListAllParents.setEnabled(false);
        }
    }//GEN-LAST:event_jPopupMenuMainFormPopupMenuWillBecomeVisible

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
            int index = this.jList1.locationToIndex(evt.getPoint());
            if (this.jList1.getSelectedValue() == null
                    || this.jList1.getSelectedValue().trim() == "") {
                return;
            }
            if (index >= 0) {
                jMenuItemList64PrefixesActionPerformed(null);
            }
        }
    }//GEN-LAST:event_jList1MouseClicked

    private void jMenuDatabaseStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jMenuDatabaseStateChanged
        if (MySQLconnection == null) {
            this.jMenuItemDBOpenForm.setEnabled(false);
            this.jMenuItemDBSendPrefix.setEnabled(false);
            this.jMenuItemDBGetPrefix.setEnabled(false);
            this.jMenuItemModifyUpdateParent.setEnabled(false);
            this.jMenuItemDBPrefixSubLevels.setEnabled(false);
            this.jMenuItemListAllParents.setEnabled(false);
            this.jMenuItemDBStats.setEnabled(false);
        } else {
            this.jMenuItemDBOpenForm.setEnabled(true);
            this.jMenuItemDBStats.setEnabled(true);
            this.jMenuItemListAllParents.setEnabled(true);

            if (this.jList1.getSelectedValue() != null) {
                this.jMenuItemDBSendPrefix.setEnabled(true);
                this.jMenuItemDBGetPrefix.setEnabled(true);
                this.jMenuItemModifyUpdateParent.setEnabled(true);
                this.jMenuItemDBPrefixSubLevels.setEnabled(true);
            } else {
                this.jMenuItemDBSendPrefix.setEnabled(false);
                this.jMenuItemDBGetPrefix.setEnabled(false);
                this.jMenuItemModifyUpdateParent.setEnabled(false);
                this.jMenuItemDBPrefixSubLevels.setEnabled(false);
            }
        }
    }//GEN-LAST:event_jMenuDatabaseStateChanged

    private void jTextFieldInputAddressFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldInputAddressFocusGained
        this.jLabel1.setText("Please enter the address without '/prefix-length'");
    }//GEN-LAST:event_jTextFieldInputAddressFocusGained

    private void jTextFieldInputAddressFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldInputAddressFocusLost
        this.jButtonCalculateActionPerformed(null);
    }//GEN-LAST:event_jTextFieldInputAddressFocusLost

    private void jMenuItemModifyUpdateParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemModifyUpdateParentActionPerformed
        UpdateDbStatus();
        if (MySQLconnection != null) {
            String snet = "";
            short plen = 0;
            short parentpflen = (short) this.jSliderPrefix.getValue();
            String myparent;

            if (this.jList1.getSelectedValue() != null && !this.jList1.getSelectedValue().trim().equals("")) {
                String selected = this.jList1.getSelectedValue().split(" ")[1].trim();
                snet = selected.split("/")[0].trim();
                plen = Short.parseShort(selected.split("/")[1].trim());

                myparent = v6ST.FindParentNet(snet, parentpflen, true).split("/")[0];

                DatabaseUI dbui = new DatabaseUI(myparent, parentpflen, parentpflen, MySQLconnection, dbserverInfo);

                dbUI.add(dbui);
                dbui.setVisible(true);
                dbui.jLabeldbstatus.setText("db=Up ");
            }
        }
    }//GEN-LAST:event_jMenuItemModifyUpdateParentActionPerformed

    private void contextModifyUpdateParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextModifyUpdateParentActionPerformed
        this.jMenuItemModifyUpdateParentActionPerformed(null);
    }//GEN-LAST:event_contextModifyUpdateParentActionPerformed

    private void jMenuItemListAllParentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemListAllParentsActionPerformed
        UpdateDbStatus();
        if (MySQLconnection != null) {
            ListParentNets listparents = new ListParentNets(MySQLconnection, dbserverInfo);

            IPv6SubnetCalculator.listParentNets.add(listparents);
            listparents.setVisible(true);
            listparents.jLabeldbstatus.setText("db=Up ");
        }
    }//GEN-LAST:event_jMenuItemListAllParentsActionPerformed

    private void contextDBListAllParentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contextDBListAllParentsActionPerformed
        this.jMenuItemListAllParentsActionPerformed(null);
    }//GEN-LAST:event_contextDBListAllParentsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());

            IPv6SubnetCalculator.setDefaultLookAndFeelDecorated(true);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IPv6SubnetCalculator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new IPv6SubnetCalculator().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem contextDBGetPrefix;
    private javax.swing.JMenuItem contextDBListAllParents;
    private javax.swing.JMenuItem contextDBPrefixSubLevels;
    private javax.swing.JMenuItem contextDBSendPrefix;
    private javax.swing.JMenuItem contextDBStats;
    private javax.swing.JMenuItem contextGotoPrefix;
    private javax.swing.JMenuItem contextGotoSearch;
    private javax.swing.JMenuItem contextList128Addresses;
    private javax.swing.JMenuItem contextList64Prefixes;
    private javax.swing.JMenuItem contextListDNSRevZones;
    private javax.swing.JMenuItem contextListSelectedRange;
    private javax.swing.JMenuItem contextModifyUpdateParent;
    private javax.swing.JMenuItem contextSaveAs;
    private javax.swing.JMenuItem contextWhoisQuery;
    private javax.swing.JMenuItem contextWorkWithSelected;
    private javax.swing.JButton jButtonCalculate;
    private javax.swing.JButton jButtonListBack;
    private javax.swing.JButton jButtonListForward;
    private javax.swing.JButton jButtonListLast;
    private javax.swing.JButton jButtonListPrefixes;
    private javax.swing.JButton jButtonNextAddrSpace;
    private javax.swing.JButton jButtonPrevAddrSpace;
    private javax.swing.JButton jButtonReset;
    private javax.swing.JCheckBox jCheckBox128bits;
    private javax.swing.JCheckBox jCheckBoxEnd;
    private javax.swing.JCheckBox jCheckBoxSubnet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private static javax.swing.JLabel jLabeldbstatus;
    private javax.swing.JList<String> jList1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuDatabase;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuGoto;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemASNconvert;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemCompress;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemDBClose;
    private javax.swing.JMenuItem jMenuItemDBConnect;
    private javax.swing.JMenuItem jMenuItemDBGetPrefix;
    private javax.swing.JMenuItem jMenuItemDBOpenForm;
    private javax.swing.JMenuItem jMenuItemDBPrefixSubLevels;
    private javax.swing.JMenuItem jMenuItemDBSendPrefix;
    private javax.swing.JMenuItem jMenuItemDBStats;
    private javax.swing.JMenuItem jMenuItemDBStatus;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemGotoAddrSpace;
    private javax.swing.JMenuItem jMenuItemGotoPrefix;
    private javax.swing.JMenuItem jMenuItemGotoSearch;
    private javax.swing.JMenuItem jMenuItemList128Addresses;
    private javax.swing.JMenuItem jMenuItemList64Prefixes;
    private javax.swing.JMenuItem jMenuItemListAllParents;
    private javax.swing.JMenuItem jMenuItemListDNSRevZones;
    private javax.swing.JMenuItem jMenuItemListSelectedRange;
    private javax.swing.JMenuItem jMenuItemModifyUpdateParent;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenuItem jMenuItemSelectAll;
    private javax.swing.JMenuItem jMenuItemServiceNames;
    private javax.swing.JMenuItem jMenuItemWhoisQuery;
    private javax.swing.JMenuItem jMenuItemWorkWithSelected;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenuMainForm;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JSlider jSliderPrefix;
    private javax.swing.JSlider jSliderSubnet;
    private javax.swing.JTextField jTextFieldAddrSpaceNo;
    private javax.swing.JTextField jTextFieldBits;
    public static javax.swing.JTextField jTextFieldEndAddr;
    private javax.swing.JTextField jTextFieldIPv6Addr;
    private javax.swing.JTextField jTextFieldInputAddress;
    private javax.swing.JTextField jTextFieldMask;
    public static javax.swing.JTextField jTextFieldStartAddr;
    private javax.swing.JTextField jTextFieldWildcard;
    private javax.swing.JLabel listcount;
    // End of variables declaration//GEN-END:variables
}
