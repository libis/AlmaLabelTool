package be.libis.almaLabelTool;

import be.libis.utils.*;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.commons.lang3.StringEscapeUtils;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;

public class LabelTool implements ActionListener
{
    static String VERSION = "1.0.11";

    static String MANUF = "LIBIS 2014";

    static int MULTIBARCODE = 1;
    static int MULTISPINE = 2;
    static int SINGLEBARCODE = 3;
    static int SINGLESPINE = 4;
    static int SINGLECARD = 5;
    static int SINGLEADDRESS = 6;

    int mode = 0; // 0=undef, 1=multibarcode, 2=multispine, 3=singlebarcode,
                  // 4=singlespine)

    String almaUser=null;
    String almaPW=null;
    Boolean parsedInAlma=null; //true=xml was parsed in Alma / false=try to parse xml local
                               //checked in getAlmaLabel() based on presence of tag <lbs-spine1>
    
    JFrame frame;
    JTextField pidText;
    JTextField barcodeText;
    JTextField idText;
    JFilePicker filePicker;
    
    JFrame editFrame;
    JTextField line1Text;
    JTextField line2Text;
    JTextField line3Text;
    JTextField line4Text;
    JTextField line5Text;
    JTextField line6Text;
    JTextField locationText;
    JTextField barcode1Text;
    JTextField callnumberText;
    JTextField numberOfLabelsText;
    int numberOfLabels=1;//only for edit screen
    boolean checkDigit = false;
    
    String lbspine1Edit="";
    String lbspine2Edit="";
    String lbspine3Edit="";
    String lbspine4Edit="";
    String lbspine5Edit="";
    String lbspine6Edit="";
    String locationcodeEdit="";
    String barcodeEdit="";
    String callnumberEdit="";

    Properties p = null;  

    public static void main(String[] args)
    {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI()
    {
        JFrame.setDefaultLookAndFeelDecorated(true);
        // JDialog.setDefaultLookAndFeelDecorated(true);
        new LabelTool();
    }

    public LabelTool()
    {
        JTabbedPane tabbedPane = new JTabbedPane();

        // read parameters
        try
        {
            p = new Properties();
            p.load(new FileInputStream("labeltool.ini"));
        }
        catch (Exception e)
        {
            System.out.println("Error reading ini-file: " + e);
            return;
        }
        
        if (p.getProperty("checkDigit", "true").toUpperCase().equals("TRUE"))
        {
            checkDigit = true;
        }
        
        JComponent panel1 = makeTextPanel1("Print labels from Alma export file");

        JComponent panel2 = makeTextPanel2("Print individual labels");
        
        JComponent panel3 = makeTextPanel3("Print user labels");

        // JComponent panel4 = makeTextPanel4("Configuration");

        frame = new JFrame("Alma Label Tool - " + VERSION + " - " + MANUF);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                frame.dispose();
                System.exit(0);
            }
        });

        tabbedPane.addTab("Multi", null, panel1, "Print labels from Alma export file");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_M);
        tabbedPane.addTab("Single", null, panel2, "Print individual labels");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_S);
        tabbedPane.addTab("User", null, panel3, "Print user labels");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_U);
        // tabbedPane.addTab("Config", null, panel4, "Change configuration");
        // tabbedPane.setMnemonicAt(1, KeyEvent.VK_C);

        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        // Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);// ==center screen
        frame.setVisible(true);
        
        
    }

    public void actionPerformed(ActionEvent e)
    {
        frame.setEnabled(false);
        try
        {
            numberOfLabels=isNumeric(numberOfLabelsText.getText());
        }
        catch (Exception ee)
        {
            numberOfLabels=1;
        }
        try
        {
            if ("barcodes".equals(e.getActionCommand()))
            {
                System.out.println("print barcodes");
                process_barcode_spine(false, true, MULTIBARCODE);
            }
            else if ("spines".equals(e.getActionCommand()))
            {
                System.out.println("print spines");
                process_barcode_spine(false, true, MULTISPINE);
            }
            else if ("pbarcodes".equals(e.getActionCommand()))
            {
                System.out.println("preview barcodes");
                process_barcode_spine(true, true, MULTIBARCODE);
            }
            else if ("pspines".equals(e.getActionCommand()))
            {
                System.out.println("preview spines");
                process_barcode_spine(true, true, MULTISPINE);
            }
            if ("barcode".equals(e.getActionCommand()))
            {
                System.out.println("print barcode");
                process_barcode_spine(false, true, SINGLEBARCODE);
            }
            else if ("spine".equals(e.getActionCommand()))
            {
                System.out.println("print spine");
                process_barcode_spine(false, true, SINGLESPINE);
            }
            else if ("pbarcode".equals(e.getActionCommand()))
            {
                System.out.println("preview barcode");
                process_barcode_spine(true, true, SINGLEBARCODE);
            }
            else if ("pspine".equals(e.getActionCommand()))
            {
                System.out.println("preview spine");
                process_barcode_spine(true, true, SINGLESPINE);
            }
            else if ("both".equals(e.getActionCommand()))
            {
                both_click(false);
            }
            else if ("edit".equals(e.getActionCommand()))
            {
                edit_click();
            }
            else if ("cancel".equals(e.getActionCommand()))
            {
                clearEditFields();
                cancel_click();
            }
            else if ("address".equals(e.getActionCommand()))
            {
                address_click(false);
            }
            else if ("paddress".equals(e.getActionCommand()))
            {
                address_click(true);
            }
            else if ("card".equals(e.getActionCommand()))
            {
                card_click(false);
            }
            else if ("pcard".equals(e.getActionCommand()))
            {
                card_click(true);
            }
            else if ("bothuser".equals(e.getActionCommand()))
            {
                bothuser_click(false);
            }
            else if ("barcodeEdit".equals(e.getActionCommand()))
            {
                System.out.println("print edit barcode");
                if (numberOfLabels>1)
                {
                    process_barcode_spine(false, false, MULTIBARCODE);
                }
                else
                {
                    process_barcode_spine(false, false, SINGLEBARCODE);
                }
            }
            else if ("spineEdit".equals(e.getActionCommand()))
            {
                System.out.println("print edit spine");
                if (numberOfLabels>1)
                {
                    process_barcode_spine(false, false, MULTISPINE);
                }
                else
                {
                    process_barcode_spine(false, false, SINGLESPINE);
                }
            }
            else if ("pbarcodeEdit".equals(e.getActionCommand()))
            {
                System.out.println("preview edit barcode");
                if (numberOfLabels>1)
                {
                    process_barcode_spine(true, false, MULTIBARCODE);
                }
                else
                {
                    process_barcode_spine(true, false, SINGLEBARCODE);
                }
            }
            else if ("pspineEdit".equals(e.getActionCommand()))
            {
                System.out.println("preview edit spine");
                if (numberOfLabels>1)
                {
                    process_barcode_spine(true, false, MULTISPINE);
                }
                else
                {
                    process_barcode_spine(true, false, SINGLESPINE);
                }
            }
            else if ("bothEdit".equals(e.getActionCommand()))
            {
                System.out.println("print edit both");
                if (numberOfLabels>1)
                {
                    //not possible to print both in one click when numberOfLabels>1
                    JOptionPane.showMessageDialog(frame, "You can only use \"Print Both\" when \"Number of labels to print\" equals 1");
                    return;
                }
                process_barcode_spine(false, false, SINGLESPINE);
                try 
                {
                    Thread.sleep(Integer.parseInt(p.getProperty("BiafDelay", "1000")));
                } 
                catch(InterruptedException ex) 
                {
                    Thread.currentThread().interrupt();
                }
                process_barcode_spine(false, false, SINGLEBARCODE);
            }
            else if ("clearEdit".equals(e.getActionCommand()))
            {
                System.out.println("clear edit fields");
                clearEditFields();
            }
        }
        catch (Exception ee)
        {
            showError(ee, "actionPerformed", "");
            System.out.println("Error in actionPerformed: " + ee);
            System.out.println("ActionEvent: " + e);
        }
        finally
        {
            frame.setEnabled(true);
        }
    }

   /* private void barcodes_click(boolean preview) throws Exception
    {
        if (!changeMode(setMode))
        {
            return;
        }
 
        String filePath = filePicker.getSelectedFilePath();
        filePath = parseLocal(filePath);
        if (filePath!=null)
        {
            runBIAF(filePath, preview, "PrinterMulti");
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably Error transforming XML data", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void spines_click(boolean preview) throws Exception
    {
        if (!changeMode(setMode))
        {
            return;
        }
        
        String filePath = filePicker.getSelectedFilePath();
        filePath = parseLocal(filePath);
        if (filePath!=null)
        {
            runBIAF(filePath, preview, "PrinterMulti");
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably Error transforming XML data", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }*/

    private void process_barcode_spine(boolean preview, boolean getAlmaData, int setMode) throws Exception
    {
        if (!changeMode(setMode))
        {
            return;
        }
        String filePath = null;
        if (getAlmaData)//NOT EDIT screen
        {
            if (mode<3) //multi: use filepicker
            {
                filePath = filePicker.getSelectedFilePath();
            }
            else //single: use webservice
            {
                filePath=getAlmaLabel();
                if (filePath==null)
                {
                    JOptionPane.showMessageDialog(frame, "No data returned: probably wrong PID or barcode", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            filePath = parseLocal(filePath);
        }
        else //from EDIT screen!
        {
            try
            {
                filePath=saveEditLabel();
            }
            catch (Exception e)
            {
                System.out.println("Error saving edit screen to XML-file: " + e.getMessage());
                JOptionPane.showMessageDialog(frame, "Error saving edit screen to XML", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        if (filePath!=null)
        {
            String oneOrMore="PrinterSingle";
            if (mode<3)//multi
            {
                oneOrMore="PrinterMulti";
            }
            System.out.println("Using temp file: " + filePath);
            runBIAF(filePath, preview, oneOrMore);
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably Error transforming XML data", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
 /*   private void spine_click(boolean preview, boolean getAlmaData, int setMode) throws Exception
    {
        if (!changeMode(setMode))
        {
            return;
        }
        String filePath = getAlmaLabel();
        if (filePath==null)
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably wrong PID or barcode", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        filePath = parseLocal(filePath);
 
        if (filePath!=null)
        {
            runBIAF(filePath, preview, "PrinterSingle");
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably Error transforming XML data", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }*/
    
    private void both_click(boolean preview) throws Exception
    {
        System.out.println("both_click");
        System.out.println("mode=" + mode);
        if (mode != SINGLESPINE)
        {
            System.out.println("both_click: setting single spine");
            // set template for single spine
            if (renameTemplates("singleSpine", "spine"))
            {
                mode = SINGLESPINE;
                System.out.println("mode=" + mode);
            }
            else
            {
                return;
            }
        }
        String filePath = getAlmaLabel();
        if (filePath==null)
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably wrong PID or barcode", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        filePath = parseLocal(filePath);
        if (filePath!=null)
        {
            //print spine
            runBIAF(filePath, preview, "PrinterSingle");
            try 
            {
                Thread.sleep(Integer.parseInt(p.getProperty("BiafDelay", "1000")));
            } 
            catch(InterruptedException ex) 
            {
                Thread.currentThread().interrupt();
            }
            System.out.println("both_click: setting single barcode");
            // set template for single barcode
            if (renameTemplates("singleBarcode", "barcode"))
            {
                mode = SINGLEBARCODE;
                System.out.println("mode=" + mode);
            }
            else
            {
                return;
            }
            
            //print barcode
            runBIAF(filePath, preview, "PrinterSingle");
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably Error transforming XML data", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void edit_click() throws Exception
    {
        System.out.println("edit_click");
        //set mode to SINGLESPINE (it must be SINGLESPINE or SINGLEBARCODE)
        if (mode != SINGLESPINE)
        {
            // set template for single spine
            if (renameTemplates("singleSpine", "spine"))
            {
                mode = SINGLESPINE;
                System.out.println("mode=" + mode);
            }
            else
            {
                return;
            }
        }
        String filePath = null;
        //try to get data from Alma - if PID or barcode was filled in
        if(pidText.getText().length()!=0 || barcodeText.getText().length()!=0)
        {
            filePath = getAlmaLabel();
        }     
        if (filePath!=null)
        {
            //we got data from Alma
            //parse local if needed:
            filePath = parseLocal(filePath);
            //read data from file and fill screen fields
            File file = new File(filePath);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            try{lbspine1Edit=StringEscapeUtils.unescapeXml(document.getElementsByTagName("lbs-spine1").item(0).getTextContent().trim());}
            catch(Exception e){System.out.println("Error in edit_click() parsing XML lbs-spine1: " + e.getMessage()); e.printStackTrace();}
            try{lbspine2Edit=StringEscapeUtils.unescapeXml(document.getElementsByTagName("lbs-spine2").item(0).getTextContent().trim());}
            catch(Exception e){System.out.println("Error in edit_click() parsing XML lbs-spine2: " + e.getMessage()); e.printStackTrace();}
            try{lbspine3Edit=StringEscapeUtils.unescapeXml(document.getElementsByTagName("lbs-spine3").item(0).getTextContent().trim());}
            catch(Exception e){System.out.println("Error in edit_click() parsing XML lbs-spine3: " + e.getMessage()); e.printStackTrace();}
            try{lbspine4Edit=StringEscapeUtils.unescapeXml(document.getElementsByTagName("lbs-spine4").item(0).getTextContent().trim());}
            catch(Exception e){System.out.println("Error in edit_click() parsing XML lbs-spine4: " + e.getMessage()); e.printStackTrace();}
            try{lbspine5Edit=StringEscapeUtils.unescapeXml(document.getElementsByTagName("lbs-spine5").item(0).getTextContent().trim());}
            catch(Exception e){System.out.println("Error in edit_click() parsing XML lbs-spine5: " + e.getMessage()); e.printStackTrace();}
            try{lbspine6Edit=StringEscapeUtils.unescapeXml(document.getElementsByTagName("lbs-spine6").item(0).getTextContent().trim());}
            catch(Exception e){System.out.println("Error in edit_click() parsing XML lbs-spine6: " + e.getMessage()); e.printStackTrace();}
            try{locationcodeEdit=StringEscapeUtils.unescapeXml(document.getElementsByTagName("location_code").item(0).getTextContent().trim());}
            catch(Exception e){System.out.println("Error in edit_click() parsing XML location_code: " + e.getMessage()); e.printStackTrace();}
            try{barcodeEdit=StringEscapeUtils.unescapeXml(document.getElementsByTagName("barcode").item(0).getTextContent().trim());}
            catch(Exception e){System.out.println("Error in edit_click() parsing XML barcode: " + e.getMessage()); e.printStackTrace();}
            try{callnumberEdit=StringEscapeUtils.unescapeXml(document.getElementsByTagName("call_number").item(0).getTextContent().trim());}
            catch(Exception e){System.out.println("Error in edit_click() parsing XML call_number: " + e.getMessage()); e.printStackTrace();}
        }
        else
        {
            if (pidText.getText().length()!=0 || barcodeText.getText().length()!=0)
            {
                //PID or barcode was filled in but we got no data: 
                JOptionPane.showMessageDialog(frame, "No data returned: probably wrong PID or barcode", "Info", JOptionPane.INFORMATION_MESSAGE);
                //returning to screen "Print individual labels" without any action
                return;
            }
        }
        
        JComponent editPanel = makeEditPanel("Edit labels", filePath);

        editFrame = new JFrame("Alma Label Tool - " + VERSION + " - " + MANUF);
        //editFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        editFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                editFrame.dispose();
                frame.setVisible(true);
                //System.exit(0);
            }
        });

        editFrame.getContentPane().add(editPanel, BorderLayout.CENTER);
        // Display the window.
        editFrame.pack();
        editFrame.setLocationRelativeTo(null);// ==center screen
        
        line1Text.setText(lbspine1Edit);
        line2Text.setText(lbspine2Edit);
        line3Text.setText(lbspine3Edit);
        line4Text.setText(lbspine4Edit);
        line5Text.setText(lbspine5Edit);
        line6Text.setText(lbspine6Edit);
        locationText.setText(locationcodeEdit);
        barcode1Text.setText(barcodeEdit);
        callnumberText.setText(callnumberEdit);
        
        frame.setVisible(false);
        editFrame.setVisible(true);     
    }
    
    private void cancel_click() throws Exception
    {
        editFrame.dispose();
        frame.setVisible(true);
    }
    
    private void card_click(boolean preview) throws Exception
    {
        System.out.println("card_click");
        if (mode != SINGLECARD)
        {
            // set template for single spine
            if (renameTemplates("singleCard", "card"))
            {
                mode = SINGLECARD;
                System.out.println("mode=" + mode);
            }
            else
            {
                return;
            }
        }
        String filePath = getAlmaLabel();
        if (filePath!=null)
        {
            String saxonPath = p.getProperty("saxonPath", "C:/Program Files/Alma Label Tool/saxon9he.jar");
            String userxslPath = p.getProperty("userxslPath", "C:/Program Files/Alma Label Tool/user.xsl");
            filePath=xmlConvert(filePath,saxonPath,userxslPath);
        }
        if (filePath!=null)
        {
            runBIAF(filePath, preview, "PrinterSingle");
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably wrong User Identifier", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void address_click(boolean preview) throws Exception
    {
        System.out.println("address_click");
        if (mode != SINGLEADDRESS)
        {
            // set template for single spine
            if (renameTemplates("singleAddress", "address"))
            {
                mode = SINGLEADDRESS;
                System.out.println("mode=" + mode);
            }
            else
            {
                return;
            }
        }
        String filePath = getAlmaLabel();
        if (filePath!=null)
        {
            String saxonPath = p.getProperty("saxonPath", "C:/Program Files/Alma Label Tool/saxon9he.jar");
            String userxslPath = p.getProperty("userxslPath", "C:/Program Files/Alma Label Tool/user.xsl");
            filePath=xmlConvert(filePath,saxonPath,userxslPath);
        }
        if (filePath!=null)
        {
            runBIAF(filePath, preview, "PrinterSingle");
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably wrong User Identifier", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void bothuser_click(boolean preview) throws Exception
    {
        System.out.println("bothuser_click");
        System.out.println("mode=" + mode);
        if (mode != SINGLECARD)
        {
            System.out.println("both_click: setting single card");
            // set template for single spine
            if (renameTemplates("singleCard", "card"))
            {
                mode = SINGLECARD;
                System.out.println("mode=" + mode);
            }
            else
            {
                return;
            }
        }
        String filePath = getAlmaLabel();
        if (filePath!=null)
        {
            String saxonPath = p.getProperty("saxonPath", "C:/Program Files/Alma Label Tool/saxon9he.jar");
            String userxslPath = p.getProperty("userxslPath", "C:/Program Files/Alma Label Tool/user.xsl");
            filePath=xmlConvert(filePath,saxonPath,userxslPath);
        }
        if (filePath!=null)
        {
            
            //print card
            runBIAF(filePath, preview, "PrinterSingle");
            try 
            {
                Thread.sleep(Integer.parseInt(p.getProperty("BiafDelay", "1000")));
            } 
            catch(InterruptedException ex) 
            {
                Thread.currentThread().interrupt();
            }
            System.out.println("bothuser_click: setting single address");
            // set template for single barcode
            if (renameTemplates("singleAddress", "address"))
            {
                mode = SINGLEADDRESS;
                System.out.println("mode=" + mode);
            }
            else
            {
                return;
            }
            //print barcode
            //add runBIAFold=true to labeltool.ini to use the old way of running BIAF
            //this is not in the documentation!
            if (p.getProperty("runBIAFold", "false").equalsIgnoreCase("true"))
            {
                runBIAForiginal(filePath, preview, "PrinterSingle");
            }
            else
            {
                runBIAF(filePath, preview, "PrinterSingle");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably wrong User Identifier", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void runBIAForiginal(String filePath, boolean preview, String printerParam)
    {
        InputStream stderr = null;
        InputStream stdout = null;
        String strData = null;

        StringBuffer cm = new StringBuffer("");
        cm=cm.append(" \"").append(p.getProperty("BiafPath", "C:/Program Files/BIAFLABEL/AddToQueue.exe")).append(" \"");
        String printer = new String(p.getProperty(printerParam, ""));
        printer = printer.trim();
        System.out.println("runBIAF Printer=" + printer);
        cm = cm.append(" ").append(filePath);
        if (!printer.equals(""))
        {
            cm = cm.append(" -printer ").append(printer);
        }
        if (preview)
        {
            cm = cm.append(" -preview");
        }
        // out.write("[" + now() + "] " + "command: " + cm.toString() + "\r\n");
        try
        {
            StringBuffer sb = new StringBuffer("");
            System.out.println("runBIAF: " + cm);
            Process p = Runtime.getRuntime().exec(cm.toString());
            p.waitFor();
            System.out.println("runBIAF completed");
            stdout = p.getInputStream();
            stderr = p.getErrorStream();

            BufferedReader brData = new BufferedReader(new InputStreamReader(stdout));
            while ((strData = brData.readLine()) != null)
            {
                sb = sb.append(strData).append("\r\n");
            }
            brData.close();

            brData = new BufferedReader(new InputStreamReader(stderr));
            while ((strData = brData.readLine()) != null)
            {
                sb = sb.append(strData).append("\r\n");
            }
            brData.close();
            System.out.println("runBIAF out: " + sb);
        }
        catch (Exception e)
        {
            showError(e, "LabelTool", "Error running BIAF: " + cm);
            System.out.println("Error running BIAF: " + e);
            return;
        }
    }
    
    private void runBIAF(String filePath, boolean preview, String printerParam)
    {
        InputStream stderr = null;
        InputStream stdout = null;
        String strData = null;

        String[] cm = new String[4];
        cm[0]=p.getProperty("BiafPath", "C:/Program Files/BIAFLABEL/AddToQueue.exe");
        String printer = new String(p.getProperty(printerParam, ""));
        printer = printer.trim();
        System.out.println("runBIAF Printer=" + printer);
        cm[1] = filePath.replaceAll("\\\\", "/");
        if (!printer.equals(""))
        {
            cm[2] = "-printer " + printer;
        }
        else
        {
            cm[2] = "";
        }
        if (preview)
        {
            cm[3] = "-preview";
        }
        else
        {
            cm[3] = "";
        }
        // out.write("[" + now() + "] " + "command: " + cm.toString() + "\r\n");
        try
        {
            StringBuffer sb = new StringBuffer("");
            System.out.println("runBIAF: " );
            System.out.println(cm[0]);
            System.out.println(cm[1]);
            System.out.println(cm[2]);
            System.out.println(cm[3]);
            Process p = Runtime.getRuntime().exec(cm);
            p.waitFor();
            System.out.println("runBIAF completed");
            stdout = p.getInputStream();
            stderr = p.getErrorStream();

            BufferedReader brData = new BufferedReader(new InputStreamReader(stdout));
            while ((strData = brData.readLine()) != null)
            {
                sb = sb.append(strData).append("\r\n");
            }
            brData.close();

            brData = new BufferedReader(new InputStreamReader(stderr));
            while ((strData = brData.readLine()) != null)
            {
                sb = sb.append(strData).append("\r\n");
            }
            brData.close();
            System.out.println("runBIAF out: " + sb);
        }
        catch (Exception e)
        {
            showError(e, "LabelTool", "Error running BIAF: " + cm);
            System.out.println("Error running BIAF: " + e);
            return;
        }
    }
    
    /*private void runBIAFnotworking (String filePath, boolean preview, String printerParam)
    {
        
        java.util.List<String> cm = new ArrayList<String>();
        String line;
        
        cm.add("C:/Program Files/BIAFLABEL/AddToQueue.exe");
        cm.add(filePath);
        
        String printer = new String(p.getProperty(printerParam, ""));
        printer = printer.trim(); 
        System.out.println("runBIAF Printer=" + printer);
        if (!printer.equals(""))
        {
            cm.add("-printer " + printer);
        }
        if (preview)
        {
            cm.add("-preview");
        }
        try
        {
            System.out.println("Running BIAF...");
            System.out.println(cm.toString());
            ProcessBuilder builder = new ProcessBuilder(cm);
            Map<String, String> environ = builder.environment();
            //builder.directory(new File(System.getenv("temp")));
            //System.out.println("BIAF Temp Directory: " + System.getenv("temp") );
            final Process process = builder.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) 
            {
              System.out.println(line);
            }
            System.out.println("BIAF finished");
        }
        catch (Exception e)
        {
            showError(e, "LabelTool", "Error running BIAF");
            System.out.println("Error running BIAF: " + e);
            return;
        }
    }*/
    
    private String parseLocal(String filePath)
    {   
        //transform local if not transformed in Alma
        if (!parsedInAlma)
        {
            String saxonPath = p.getProperty("saxonPath", "C:/Program Files/Alma Label Tool/saxon9he.jar");
            String barcodespinexslPath = p.getProperty("barcodespinexslPath", "C:/Program Files/Alma Label Tool/barcodespine.xsl");
            
            if (!barcodespinexslPath.trim().equals(""))
            {
                File f = new File(barcodespinexslPath.trim());
            
                if (f.exists())
                {
                    filePath=xmlConvert(filePath,saxonPath,barcodespinexslPath);
                }
            }
        }
        return filePath;
    }
    
    private String xmlConvert(String file, String saxon, String xsl)
    {
        InputStream stderr = null;
        InputStream stdout = null;
        String strData = null;
        
        File resultFile = null;
        
        StringBuffer cm = new StringBuffer("java -jar");
        
        System.out.println("xmlConvert (running Saxon)");
        try
        {
            resultFile = File.createTempFile("labeltool", ".xml");
            System.out.println("Temp file 2 created");
        }
        catch (Exception e)
        {
            showError(e, "xmlConvert", "Can't create tempfile 2.");
            System.out.println("Can't create tempfile 2: " + e);
            return null;
        }
        
        cm = cm.append(" \"").append(saxon).append("\"");
        cm = cm.append(" -o:").append(resultFile.getAbsolutePath());
        cm = cm.append(" ").append(file);
        cm = cm.append(" \"").append(xsl).append(" \"");
        System.out.println("command: " + cm.toString() + "\r\n");        
        try
        {            
            StringBuffer sb = new StringBuffer("");
            Process p = Runtime.getRuntime().exec(cm.toString());
            
            stdout = p.getInputStream ();
            stderr = p.getErrorStream ();
            
            BufferedReader brData = new BufferedReader (new InputStreamReader (stdout));
            while ((strData = brData.readLine ()) != null) 
            {
                sb=sb.append(strData).append("\r\n");
            }
            brData.close();
            
            brData = new BufferedReader (new InputStreamReader (stderr));
            while ((strData = brData.readLine ()) != null) 
            {
                sb=sb.append(strData).append("\r\n");
            }
            brData.close();
            
            System.out.println("Stderr Saxon: " + sb.toString() + "\r\n");
            return resultFile.getCanonicalPath();
        }
        catch (Exception e)
        {
            System.out.println("Error running Saxon: " + e + "\r\n");
            return null;
        }
    }
    
    private String getAlmaLabel() throws IOException //returns path to xml file
    {
        String value=null;
        String isBarcode="false";
        AlmaLabel almaLabel=null;
        File resultFile = null;
        
        System.out.println("getAlmaLabel");
        try
        {
            resultFile = File.createTempFile("labeltool", ".xml");
            System.out.println("Temp file created");
        }
        catch (Exception e)
        {
            showError(e, "getAlmaLabel", "Can't create tempfile.");
            System.out.println("Can't create tempfile: " + e);
            return null;
        }
        if (almaPW==null)
        {
            System.out.println("almaPW==null");
            PasswordDialog pwDialog = new PasswordDialog(p.getProperty("AlmaUser", null),null,frame);
            if(pwDialog.getCancelled())
            {
                System.out.println("pwDialog was cancelled");
                return null;
            }
            almaUser=pwDialog.getUser();
            almaPW=pwDialog.getPW();
        }
        if (mode!=SINGLECARD && mode!=SINGLEADDRESS)
        {
            if(pidText.getText().length()!=0)
            {
                value=pidText.getText();
                System.out.println("pidText=" + value);
            }
            else if (barcodeText.getText().length()!=0)
            {
                value=barcodeText.getText();
                isBarcode="true";
                System.out.println("barcodeText=" + value);
            }
            else
            {
                JOptionPane.showMessageDialog(frame, "Please fill in PID or barcode", "Info", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("pid & barcode empty");
                return null;
            }
        }
        else
        {
            System.out.println("mode is SINGLECARD or SINGLEADDRESS");
            if(idText.getText().length()!=0)
            {
                value=idText.getText();
                System.out.println("idText=" + value);
            }
            else
            {
                JOptionPane.showMessageDialog(frame, "Please fill in User Identifier", "Info", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("user identifier empty");
                return null;
            }
        }
            
        try
        {
            almaLabel = new AlmaLabel(p.getProperty("AlmaURL", "https://eu.alma.exlibrisgroup.com"), 
                    p.getProperty("AlmaInstitution", "32KUL_KUL"), almaUser, almaPW);
            System.out.println("Contacted AlmaWebService");
        }
        catch (Exception e)
        {
            showError(e, "getAlmaLabel", "Error connecting to Alma");
            System.out.println("Error connecting to Alma: " + e);
            almaPW=null;
            return null;
        }
        
        try
        {
            String xmlLabel=null;
            if (mode!=SINGLECARD && mode!=SINGLEADDRESS)
            {
                xmlLabel=almaLabel.getLabel(value, isBarcode);
            }
            else
            {
                xmlLabel=almaLabel.getUserLabel(value);
                /*if (xmlLabel!=null && xmlLabel.length()>500)
                {
                    xmlLabel = xmlLabel.replaceAll("xb:", "").replaceAll("xsi", "");
                }*/
            }
            if (xmlLabel!=null && xmlLabel.length()>500)
            {
                System.out.println("got almaLabel");
                //check if parsed in Alma and set Boolean
                if (xmlLabel.contains("<lbs-spine1>"))
                {
                    parsedInAlma=true;
                }
                else
                {
                    parsedInAlma=false;
                }
            }
            else
            {
                System.out.println("xmlLabel is null or too short: \n" + xmlLabel);
                return null;
            }
        }
        catch (Exception e)
        {
            
            if (e.toString().contains("Unauthorized")) 
            {
                almaPW=null;
                JOptionPane.showMessageDialog(frame, "Authorization error: wrong username, password or Alma role missing", "Info", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Authorization error: " + e);
            }
            else
            {
                showError(e, "getAlmaLabel", "Error getting Label from Alma.");
                System.out.println("Error getting Label from Alma: " + e);
            }
            return null;
        }
        //escape & with another ampersand to avoid BIAF mnemonic interpretation!
        //add doubleAmp=false to labeltool.ini to undo double &
        //this is not in documentation!
        return almaLabel.save(resultFile); //returns filepath if successful, null on failure
    }
    
    private String saveEditLabel() throws IOException 
    //create XML from data on Edit-screen and return path to xml file
    {
        File resultFile = null;
        
        System.out.println("saveEditLabel");
        try
        {
            resultFile = File.createTempFile("labeltool", ".xml");
            System.out.println("Temp file created");
        }
        catch (Exception e)
        {
            showError(e, "saveEditLabel", "Can't create tempfile.");
            System.out.println("Can't create tempfile: " + e);
            return null;
        }
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            Document doc=docBuilder.newDocument();
            Element printout = doc.createElement("printout");
            doc.appendChild(printout);
            
            Element formname = doc.createElement("form-name");
            formname.appendChild(doc.createTextNode("Default"));
            printout.appendChild(formname);
            
            Element formlanguage = doc.createElement("form-language");
            formlanguage.appendChild(doc.createTextNode("eng"));
            printout.appendChild(formlanguage);
            
            Element formformat = doc.createElement("form-format");
            formformat.appendChild(doc.createTextNode("00"));
            printout.appendChild(formformat);
            
            int i=1;
            while (i<=numberOfLabels)//printing identical labels
            {
            
                Element section01 = doc.createElement("section-01");
                Element physical = doc.createElement("physical_item_display_for_printing");
                
                Element barcode = doc.createElement("barcode");
                barcode.appendChild(doc.createTextNode(barcode1Text.getText()));
                physical.appendChild(barcode);
                
                Element barcodecd = doc.createElement("barcodecd");
                if (checkDigit)
                {
                    barcodecd.appendChild(doc.createTextNode("*" + barcode1Text.getText() + Utils.mod43(barcode1Text.getText()) + "*")); 
                }
                else
                {
                    barcodecd.appendChild(doc.createTextNode("*" + barcode1Text.getText() + "*"));
                }
                physical.appendChild(barcodecd);
                
                Element location = doc.createElement("location_code");
                location.appendChild(doc.createTextNode(locationText.getText().replaceAll("&", "&&"))); //& -> && for BIAF mnemonic interpretation!
                physical.appendChild(location);
                
                Element callnumber = doc.createElement("call_number");
                callnumber.appendChild(doc.createTextNode(callnumberText.getText().replaceAll("&", "&&")));
                physical.appendChild(callnumber);
                
                Element spine1 = doc.createElement("lbs-spine1");
                spine1.appendChild(doc.createTextNode(line1Text.getText().replaceAll("&", "&&")));
                physical.appendChild(spine1);
                Element spine2 = doc.createElement("lbs-spine2");
                spine2.appendChild(doc.createTextNode(line2Text.getText().replaceAll("&", "&&")));
                physical.appendChild(spine2);
                Element spine3 = doc.createElement("lbs-spine3");
                spine3.appendChild(doc.createTextNode(line3Text.getText().replaceAll("&", "&&")));
                physical.appendChild(spine3);
                Element spine4 = doc.createElement("lbs-spine4");
                spine4.appendChild(doc.createTextNode(line4Text.getText().replaceAll("&", "&&")));
                physical.appendChild(spine4);
                Element spine5 = doc.createElement("lbs-spine5");
                spine5.appendChild(doc.createTextNode(line5Text.getText().replaceAll("&", "&&")));
                physical.appendChild(spine5);
                Element spine6 = doc.createElement("lbs-spine6");
                spine6.appendChild(doc.createTextNode(line6Text.getText().replaceAll("&", "&&")));
                physical.appendChild(spine6);
                
                section01.appendChild(physical);
                printout.appendChild(section01);
                i++;
            }
         // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(resultFile);
      
            transformer.transform(source, result);
        }
        catch (TransformerException e)
        {
            showError(e, "saveEditLabel", "TransformerException");
            System.out.println("TransformerException: " + e);
            return null;
        }
        catch (ParserConfigurationException e)
        {
            showError(e, "saveEditLabel", "ParserConfigurationException");
            System.out.println("ParserConfigurationException: " + e);
            return null;
        }
 
        System.out.println("XML-file saved");
        return resultFile.getCanonicalPath();
    }
    
    private boolean changeMode(int setMode)
    {
        if (mode != setMode)
        {
            if (setMode==SINGLEBARCODE)
            {
                // set template for single barcode
                if (renameTemplates("singleBarcode", "barcode"))
                {
                    mode = SINGLEBARCODE;
                    
                }
                else
                {
                    return false;
                }
            }
            else if (setMode==SINGLESPINE)
            {
                // set template for single spine
                if (renameTemplates("singleSpine", "spine"))
                {
                    mode = SINGLESPINE;
                }
                else
                {
                    return false;
                }
            }
            else if (setMode==MULTIBARCODE)
            {
                
                if (mode != MULTIBARCODE)
                {
                    // set template for multiple barcodes
                    if (renameTemplates("multipleBarcode", "barcode3x7"))
                    {
                        mode = MULTIBARCODE;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            else if (setMode==MULTISPINE)
            {
                if (mode != MULTISPINE)
                {
                    // set template for multiple spines
                    if (renameTemplates("multipleSpine", "spine3x7"))
                    {
                        mode = MULTISPINE;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }
        System.out.println("mode=" + mode);
        return true;
    }
    
    private boolean renameTemplates(String parameterName, String defValue)
    {
        Path source = null;
        Path dest = null;
        try
        {
            source = FileSystems.getDefault().getPath(
                    p.getProperty("TemplatesPath", "C:/Program Files/BIAFLABEL/Templates") + "/"
                            + p.getProperty(parameterName, defValue) + ".lbs");
            dest = FileSystems.getDefault().getPath(
                    p.getProperty("TemplatesPath", "C:/Program Files/BIAFLABEL/Templates") + "/libis.lbs");
            Files.copy(source, dest, REPLACE_EXISTING);

            source = FileSystems.getDefault().getPath(
                    p.getProperty("TemplatesPath", "C:/Program Files/BIAFLABEL/Templates") + "/"
                            + p.getProperty(parameterName, defValue) + ".lrs");
            dest = FileSystems.getDefault().getPath(
                    p.getProperty("TemplatesPath", "C:/Program Files/BIAFLABEL/Templates") + "/libis.lrs");
            Files.copy(source, dest, REPLACE_EXISTING);
        }
        catch (Exception e)
        {
            // out.write("[" + now() + "] " + "Error running BIAF: " + e +
            // "\r\n");
            // out.close();
            showError(e, "LabelTool",
                    "Error renaming files: source: " + source.toString() + " destination: " + dest.toString());
            System.out.println("Error renaming files: \nsource: " + source.toString() + "\ndestination: " + 
                    dest.toString() + "\n" + e);
            return false;
        }

        return true;
    }
    
    public static int isNumeric(String str)  
    {  
      int number=1;
      try  
      {  
          number = Integer.parseInt(str);  
      }  
      catch(Exception e)  
      {  
          return 1;  
      }  
      if (number > 100) 
      {
          return 100;
      }
      return number;  
    }

    private JComponent makeTextPanel1(String text)
    {
        // mainpanel
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        JLabel tabTitle = new JLabel(text);
        tabTitle.setHorizontalAlignment(JLabel.CENTER);
        tabTitle.setVerticalAlignment(JLabel.TOP);
        panel.add(tabTitle);

        filePicker = new JFilePicker("Alma export file", "Browse...");
        filePicker.setMode(JFilePicker.MODE_OPEN);
        filePicker.addFileTypeFilter(".xml", "XML files");
        JFileChooser fileChooser = filePicker.getFileChooser();
        try
        {
            fileChooser.setCurrentDirectory(new File(p.getProperty("AlmaExportDir", "C:/")));
        }
        catch (Exception e)
        {
            showError(e, "makeTextPanel1()", "Error setting Directory: " + p.getProperty("AlmaExportDir", "C:/"));
            System.out.println("Error setting Directory: " + p.getProperty("AlmaExportDir", "C:/\n " + e));
        }
        panel.add(filePicker);

        // subpanels
        JPanel subpanel1 = new JPanel(new GridLayout(1, 3, 10, 10));
        JPanel subpanel2 = new JPanel(new GridLayout(1, 3, 10, 10));
        // print buttons
        JButton barcodeButton = new JButton("Print Barcodes");
        barcodeButton.setMnemonic(KeyEvent.VK_B);
        barcodeButton.setActionCommand("barcodes");
        barcodeButton.addActionListener(this);
        JButton spineButton = new JButton("Print Spine Labels");
        spineButton.setMnemonic(KeyEvent.VK_I);
        spineButton.setActionCommand("spines");
        spineButton.addActionListener(this);
        // preview buttons
        JButton pBarcodeButton = new JButton("Preview Barcodes");
        pBarcodeButton.setMnemonic(KeyEvent.VK_P);
        pBarcodeButton.setActionCommand("pbarcodes");
        pBarcodeButton.addActionListener(this);
        JButton pSpineButton = new JButton("Preview Spine Labels");
        pSpineButton.setMnemonic(KeyEvent.VK_L);
        pSpineButton.setActionCommand("pspines");
        pSpineButton.addActionListener(this);

        subpanel1.add(barcodeButton);
        subpanel1.add(new JLabel("  "));
        subpanel1.add(pBarcodeButton);
        subpanel2.add(spineButton);
        subpanel2.add(new JLabel("  "));
        subpanel2.add(pSpineButton);
        // subpanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(subpanel1);
        panel.add(subpanel2);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return panel;
    }

    private JComponent makeTextPanel2(String text)
    {
        // mainpanel
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        JLabel tabTitle = new JLabel(text);
        tabTitle.setHorizontalAlignment(JLabel.CENTER);
        tabTitle.setVerticalAlignment(JLabel.TOP);

        // subpanels
        JPanel subpanel1 = new JPanel(new GridLayout(1, 3, 10, 10));
        JPanel subpanel2 = new JPanel(new GridLayout(1, 3, 10, 10));
        JPanel subpanel3 = new JPanel(new GridLayout(1, 2, 60, 10));
        JPanel subpanel4 = new JPanel(new GridLayout(1, 2, 60, 10));
        JPanel subpanel5 = new JPanel(new GridLayout(1, 2, 60, 10));

        // PID & Barcode
        JLabel pidLabel = new JLabel("PID", SwingConstants.RIGHT);
        pidText = new JTextField(15);
        pidText.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    barcodeText.setText("");
                    pidText.selectAll();
                }
            }
        });
        JLabel barcodeLabel = new JLabel("Barcode", SwingConstants.RIGHT);
        barcodeText = new JTextField(15);
        barcodeText.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    pidText.setText("");
                    barcodeText.selectAll();
                }
            }
        });

        // print buttons
        JButton barcodeButton = new JButton("Print Barcode");
        barcodeButton.setMnemonic(KeyEvent.VK_B);
        barcodeButton.setActionCommand("barcode");
        barcodeButton.addActionListener(this);
        JButton spineButton = new JButton("Print Spine Label");
        spineButton.setMnemonic(KeyEvent.VK_S);
        spineButton.setActionCommand("spine");
        spineButton.addActionListener(this);
        // preview buttons
        JButton pBarcodeButton = new JButton("Preview Barcode");
        pBarcodeButton.setMnemonic(KeyEvent.VK_P);
        pBarcodeButton.setActionCommand("pbarcode");
        pBarcodeButton.addActionListener(this);
        JButton pSpineButton = new JButton("Preview Spine Label");
        pSpineButton.setMnemonic(KeyEvent.VK_L);
        pSpineButton.setActionCommand("pspine");
        pSpineButton.addActionListener(this);
        // print both button
        JButton bothButton = new JButton("Print Both");
        bothButton.setMnemonic(KeyEvent.VK_O);
        bothButton.setActionCommand("both");
        bothButton.addActionListener(this);
        //Edit button
        JButton editButton = new JButton("Edit Labels");
        editButton.setMnemonic(KeyEvent.VK_E);
        editButton.setActionCommand("edit");
        editButton.addActionListener(this);

        panel.add(tabTitle);
        panel.add(subpanel1);
        panel.add(subpanel2);
        panel.add(subpanel3);
        panel.add(subpanel4);
        panel.add(subpanel5);

        subpanel1.add(pidLabel);
        subpanel1.add(pidText);
        subpanel1.add(new JLabel("  "));

        subpanel2.add(barcodeLabel);
        subpanel2.add(barcodeText);
        subpanel2.add(new JLabel("  "));

        subpanel3.add(barcodeButton);
        subpanel3.add(pBarcodeButton);

        subpanel4.add(spineButton);
        subpanel4.add(pSpineButton);

        subpanel5.add(bothButton);
        subpanel5.add(editButton);
        
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        return panel;
    }
    
    private JComponent makeTextPanel3(String text)
    {
        // mainpanel
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        JLabel tabTitle = new JLabel(text);
        tabTitle.setHorizontalAlignment(JLabel.CENTER);
        tabTitle.setVerticalAlignment(JLabel.TOP);

        // subpanels
        JPanel subpanel1 = new JPanel(new GridLayout(1, 3, 10, 10));
        JPanel subpanel2 = new JPanel(new GridLayout(1, 2, 60, 10));
        JPanel subpanel3 = new JPanel(new GridLayout(1, 2, 60, 10));
        JPanel subpanel4 = new JPanel(new GridLayout(1, 2, 60, 10));

        // User Identifier
        JLabel idLabel = new JLabel("User Identifier", SwingConstants.RIGHT);
        idText = new JTextField(15);
        idText.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    idText.selectAll();
                }
            }
        });
        
        // print buttons
        JButton cardButton = new JButton("Print Card");
        cardButton.setMnemonic(KeyEvent.VK_C);
        cardButton.setActionCommand("card");
        cardButton.addActionListener(this);
        JButton addressButton = new JButton("Print Address");
        addressButton.setMnemonic(KeyEvent.VK_A);
        addressButton.setActionCommand("address");
        addressButton.addActionListener(this);
        // preview buttons
        JButton pCardButton = new JButton("Preview Card");
        pCardButton.setMnemonic(KeyEvent.VK_P);
        pCardButton.setActionCommand("pcard");
        pCardButton.addActionListener(this);
        JButton pAddressButton = new JButton("Preview Address");
        pAddressButton.setMnemonic(KeyEvent.VK_D);
        pAddressButton.setActionCommand("paddress");
        pAddressButton.addActionListener(this);
        // print both button
        JButton bothButton = new JButton("Print Both");
        bothButton.setMnemonic(KeyEvent.VK_O);
        bothButton.setActionCommand("bothuser");
        bothButton.addActionListener(this);

        panel.add(tabTitle);
        panel.add(subpanel1);
        panel.add(subpanel2);
        panel.add(subpanel3);
        panel.add(subpanel4);

        subpanel1.add(idLabel);
        subpanel1.add(idText);
        subpanel1.add(new JLabel("  "));

        subpanel2.add(cardButton);
        subpanel2.add(pCardButton);

        subpanel3.add(addressButton);
        subpanel3.add(pAddressButton);

        subpanel4.add(bothButton);
        subpanel4.add(new JLabel("  "));
        
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        return panel;
    }
    
    private JComponent makeEditPanel(String text, String filePath)
    {
     // mainpanel
        JPanel panel = new JPanel(new GridLayout(14, 1, 10, 10));
        JLabel tabTitle = new JLabel(text);
        tabTitle.setHorizontalAlignment(JLabel.CENTER);
        tabTitle.setVerticalAlignment(JLabel.TOP);

        // subpanels
        JPanel subpanel1 = new JPanel(new GridLayout(1, 4, 10, 10));
        JPanel subpanel2 = new JPanel(new GridLayout(1, 4, 10, 10));
        JPanel subpanel3 = new JPanel(new GridLayout(1, 4, 10, 10));
        JPanel subpanel4 = new JPanel(new GridLayout(1, 4, 10, 10));
        JPanel subpanel5 = new JPanel(new GridLayout(1, 4, 10, 10));
        JPanel subpanel6 = new JPanel(new GridLayout(1, 4, 10, 10));
        JPanel subpanel7 = new JPanel(new GridLayout(1, 4, 10, 10));  
        
        JPanel subpanel8 = new JPanel(new GridLayout(1, 3, 10, 10));
        JPanel subpanel8_2 = new JPanel(new GridLayout(1, 4, 10, 10));

        JPanel subpanel10 = new JPanel(new GridLayout(1, 2, 60, 10));
        JPanel subpanel11 = new JPanel(new GridLayout(1, 2, 60, 10));
        JPanel subpanel12 = new JPanel(new GridLayout(1, 2, 60, 10));

        //spine label
        JLabel spineLabel = new JLabel("Spine label", SwingConstants.CENTER);
        
        JLabel line1Label = new JLabel("Line 1", SwingConstants.RIGHT);
        JLabel line2Label = new JLabel("Line 2", SwingConstants.RIGHT);
        JLabel line3Label = new JLabel("Line 3", SwingConstants.RIGHT);
        JLabel line4Label = new JLabel("Line 4", SwingConstants.RIGHT);
        JLabel line5Label = new JLabel("Line 5", SwingConstants.RIGHT);
        JLabel line6Label = new JLabel("Line 6", SwingConstants.RIGHT);
        
        line1Text = new JTextField(10);
        line2Text = new JTextField(10);
        line3Text = new JTextField(10);
        line4Text = new JTextField(10);
        line5Text = new JTextField(10);
        line6Text = new JTextField(10);
        line1Text.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    line1Text.selectAll();
                }
            }
        });
        line2Text.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    line2Text.selectAll();
                }
            }
        });
        line3Text.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    line3Text.selectAll();
                }
            }
        });
        line4Text.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    line4Text.selectAll();
                }
            }
        });
        line5Text.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    line5Text.selectAll();
                }
            }
        });
        line6Text.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    line6Text.selectAll();
                }
            }
        });
        
        //barcode
        JLabel barcodeLabel = new JLabel("Barcode", SwingConstants.CENTER);
        
        JLabel locationLabel = new JLabel("Location", SwingConstants.RIGHT);
        JLabel barcode1Label = new JLabel("Barcode", SwingConstants.RIGHT);
        JLabel callnumberLabel = new JLabel("Call number", SwingConstants.RIGHT);
        
        locationText = new JTextField(10);
        barcode1Text = new JTextField(10);
        callnumberText = new JTextField(10);
        locationText.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    locationText.selectAll();
                }
            }
        });
        barcode1Text.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    barcode1Text.selectAll();
                }
            }
        });
        callnumberText.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    callnumberText.selectAll();
                }
            }
        });
        
        //number of labels
        numberOfLabelsText = new JTextField(2);
        numberOfLabelsText.setText("1");
        numberOfLabelsText.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) 
                {
                    numberOfLabelsText.selectAll();
                }
            }
        });
        
        //clear all button
        JButton clearButton = new JButton("Clear fields");
        clearButton.setMnemonic(KeyEvent.VK_B);
        clearButton.setActionCommand("clearEdit");
        clearButton.addActionListener(this);
        
        //subpanel1
        subpanel1.add(new JLabel("  "));
        subpanel1.add(spineLabel);
        subpanel1.add(new JLabel("  "));
        subpanel1.add(barcodeLabel);
        //subpanel2
        subpanel2.add(line1Label);
        subpanel2.add(line1Text);
        subpanel2.add(locationLabel);
        subpanel2.add(locationText);
        //subpanel3
        subpanel3.add(line2Label);
        subpanel3.add(line2Text);
        subpanel3.add(barcode1Label);
        subpanel3.add(barcode1Text);
        //subpanel4
        subpanel4.add(line3Label);
        subpanel4.add(line3Text);
        subpanel4.add(callnumberLabel);
        subpanel4.add(callnumberText);
        //subpanel5
        subpanel5.add(line4Label);
        subpanel5.add(line4Text);
        subpanel5.add(new JLabel("  "));    
        subpanel5.add(new JLabel("  "));
        //subpanel6
        subpanel6.add(line5Label);
        subpanel6.add(line5Text);
        subpanel6.add(new JLabel("  "));
        subpanel6.add(new JLabel("  "));
        //subpanel7
        subpanel7.add(line6Label);
        subpanel7.add(line6Text);
        subpanel7.add(new JLabel("  "));
        subpanel7.add(new JLabel("  "));
        //subpanel8
        subpanel8_2.add(numberOfLabelsText);
        subpanel8_2.add(new JLabel("  "));
        subpanel8_2.add(new JLabel("  "));
        subpanel8.add(new JLabel("Number of labels to print: "));
        subpanel8.add(subpanel8_2);
        subpanel8.add(clearButton);

        // print buttons
        JButton barcodeButton = new JButton("Print Barcode");
        barcodeButton.setMnemonic(KeyEvent.VK_B);
        barcodeButton.setActionCommand("barcodeEdit");
        barcodeButton.addActionListener(this);
        JButton spineButton = new JButton("Print Spine Label");
        spineButton.setMnemonic(KeyEvent.VK_S);
        spineButton.setActionCommand("spineEdit");
        spineButton.addActionListener(this);
        // preview buttons
        JButton pBarcodeButton = new JButton("Preview Barcode");
        pBarcodeButton.setMnemonic(KeyEvent.VK_P);
        pBarcodeButton.setActionCommand("pbarcodeEdit");
        pBarcodeButton.addActionListener(this);
        JButton pSpineButton = new JButton("Preview Spine Label");
        pSpineButton.setMnemonic(KeyEvent.VK_L);
        pSpineButton.setActionCommand("pspineEdit");
        pSpineButton.addActionListener(this);
        // print both button
        JButton bothButton = new JButton("Print Both");
        bothButton.setMnemonic(KeyEvent.VK_O);
        bothButton.setActionCommand("bothEdit");
        bothButton.addActionListener(this);
        //Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);

        panel.add(tabTitle);
        panel.add(subpanel1);
        panel.add(subpanel2);
        panel.add(subpanel3);
        panel.add(subpanel4);
        panel.add(subpanel5);
        panel.add(subpanel6);
        panel.add(subpanel7);
        panel.add(new JLabel("  "));
        panel.add(subpanel8);
        panel.add(new JLabel("  "));
        panel.add(subpanel10);
        panel.add(subpanel11);
        panel.add(subpanel12);

        subpanel10.add(barcodeButton);
        subpanel10.add(pBarcodeButton);

        subpanel11.add(spineButton);
        subpanel11.add(pSpineButton);

        subpanel12.add(bothButton);
        subpanel12.add(cancelButton);
        
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        return panel;
    }
    
    private void clearEditFields()
    {
        lbspine1Edit="";
        lbspine2Edit="";
        lbspine3Edit="";
        lbspine4Edit="";
        lbspine5Edit="";
        lbspine6Edit="";
        locationcodeEdit="";
        barcodeEdit="";
        callnumberEdit="";
        line1Text.setText("");
        line2Text.setText("");
        line3Text.setText("");
        line4Text.setText("");
        line5Text.setText("");
        line6Text.setText("");
        locationText.setText("");
        barcode1Text.setText("");
        callnumberText.setText("");
        numberOfLabelsText.setText("1");
    }

    private void showError(Exception e, String module, String message)
    {
        JOptionPane.showMessageDialog(frame, "Error in " + module + ": " + message + "\n\n" + e);
    }

}
