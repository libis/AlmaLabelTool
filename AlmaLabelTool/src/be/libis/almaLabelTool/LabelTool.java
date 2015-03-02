package be.libis.almaLabelTool;

import be.libis.utils.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;

public class LabelTool implements ActionListener
{
    static String VERSION = "1.0.9";

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
    
    JFrame frame;
    JTextField pidText;
    JTextField barcodeText;
    
    JTextField idText;

    JFilePicker filePicker;

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
            if ("barcodes".equals(e.getActionCommand()))
            {
                barcodes_click(false);
            }
            else if ("spines".equals(e.getActionCommand()))
            {
                spines_click(false);
            }
            else if ("pbarcodes".equals(e.getActionCommand()))
            {
                barcodes_click(true);
            }
            else if ("pspines".equals(e.getActionCommand()))
            {
                spines_click(true);
            }
            if ("barcode".equals(e.getActionCommand()))
            {
                barcode_click(false);
            }
            else if ("spine".equals(e.getActionCommand()))
            {
                spine_click(false);
            }
            else if ("pbarcode".equals(e.getActionCommand()))
            {
                barcode_click(true);
            }
            else if ("pspine".equals(e.getActionCommand()))
            {
                spine_click(true);
            }
            else if ("both".equals(e.getActionCommand()))
            {
                both_click(false);
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
            else if ("save".equals(e.getActionCommand()))
            {
                // save_click();
            }
            else if ("cancel".equals(e.getActionCommand()))
            {
                // cancel_click();
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

    private void barcodes_click(boolean preview) throws Exception
    {
        System.out.println("barcodes_click");
        if (mode != MULTIBARCODE)
        {
            // set template for multiple barcodes
            if (renameTemplates("multipleBarcode", "barcode3x7"))
            {
                mode = MULTIBARCODE;
                System.out.println("mode=" + mode);
            }
            else
            {
                return;
            }
        }
        String filePath = filePicker.getSelectedFilePath();
        String saxonPath = p.getProperty("saxonPath", "C:/Program Files/Alma Label Tool/saxon9he.jar");
        String barcodespinexslPath = p.getProperty("barcodespinexslPath", "C:/Program Files/Alma Label Tool/barcodespine.xsl");
        //transform local if barcodespinexslPath not empty
        if (!barcodespinexslPath.trim().equals(""))
        {
            filePath=xmlConvert(filePath,saxonPath,barcodespinexslPath);
        }
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
        System.out.println("spines_click");
        if (mode != MULTISPINE)
        {
            // set template for multiple spines
            if (renameTemplates("multipleSpine", "spine3x7"))
            {
                mode = MULTISPINE;
                System.out.println("mode=" + mode);
            }
            else
            {
                return;
            }
        }
        String filePath = filePicker.getSelectedFilePath();
        String saxonPath = p.getProperty("saxonPath", "C:/Program Files/Alma Label Tool/saxon9he.jar");
        String barcodespinexslPath = p.getProperty("barcodespinexslPath", "C:/Program Files/Alma Label Tool/barcodespine.xsl");
        //transform local if barcodespinexslPath not empty
        if (!barcodespinexslPath.trim().equals(""))
        {
            filePath=xmlConvert(filePath,saxonPath,barcodespinexslPath);
        }
        if (filePath!=null)
        {
            runBIAF(filePath, preview, "PrinterMulti");
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably Error transforming XML data", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void barcode_click(boolean preview) throws Exception
    {
        System.out.println("barcode_click");
        if (mode != SINGLEBARCODE)
        {
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
        }
        String filePath = getAlmaLabel();
        if (filePath==null)
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably wrong PID or barcode", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String saxonPath = p.getProperty("saxonPath", "C:/Program Files/Alma Label Tool/saxon9he.jar");
        String barcodespinexslPath = p.getProperty("barcodespinexslPath", "C:/Program Files/Alma Label Tool/barcodespine.xsl");
        //transform local if barcodespinexslPath not empty
        if (!barcodespinexslPath.trim().equals(""))
        {
            filePath=xmlConvert(filePath,saxonPath,barcodespinexslPath);
        }
        if (filePath!=null)
        {
            runBIAF(filePath, preview, "PrinterSingle");
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably Error transforming XML data", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void spine_click(boolean preview) throws Exception
    {
        System.out.println("spine_click");
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
        String filePath = getAlmaLabel();
        if (filePath==null)
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably wrong PID or barcode", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String saxonPath = p.getProperty("saxonPath", "C:/Program Files/Alma Label Tool/saxon9he.jar");
        String barcodespinexslPath = p.getProperty("barcodespinexslPath", "C:/Program Files/Alma Label Tool/barcodespine.xsl");
        //transform local if barcodespinexslPath not empty
        if (!barcodespinexslPath.trim().equals(""))
        {
            filePath=xmlConvert(filePath,saxonPath,barcodespinexslPath);
        }
        if (filePath!=null)
        {
            runBIAF(filePath, preview, "PrinterSingle");
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No data returned: probably Error transforming XML data", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
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
        
        String saxonPath = p.getProperty("saxonPath", "C:/Program Files/Alma Label Tool/saxon9he.jar");
        String barcodespinexslPath = p.getProperty("barcodespinexslPath", "C:/Program Files/Alma Label Tool/barcodespine.xsl");
        //transform local if barcodespinexslPath not empty
        if (!barcodespinexslPath.trim().equals(""))
        {
            filePath=xmlConvert(filePath,saxonPath,barcodespinexslPath);
        }
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
//TODO TESTEN runBIAF en de optie runBIAFold alvorens volgende versie te verspreiden
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
    
    private void runBIAFnotworking (String filePath, boolean preview, String printerParam)
    {
        
        java.util.List<String> cm = new ArrayList<String>();
        String line;
        
        cm.add("C:/Program Files/BIAFLABEL/AddToQueue.exe");
        cm.add(filePath);
        
        String printer = new String(p.getProperty(printerParam, ""));
        printer = printer.trim(); //TODO remove ""
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
        subpanel5.add(new JLabel("  "));
        
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

    private void showError(Exception e, String module, String message)
    {
        JOptionPane.showMessageDialog(frame, "Error in " + module + ": " + message + "\n\n" + e);
    }

}
