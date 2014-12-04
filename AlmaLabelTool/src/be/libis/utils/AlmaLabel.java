package be.libis.utils;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import javax.xml.soap.SOAPException;

import com.exlibris.alma.sdk.AlmaWebServices;

public class AlmaLabel
{
    private AlmaWebServices aws=null;
    
    public String xmlLabel=null;

    /*
     * public static void main(String[] args) { AlmaLabel example = new
     * AlmaLabel(); example.testLocal(); }
     * 
     * private void testLocal() { AlmaWebServices aws =
     * AlmaWebServices.create("web service URL", "user name", "user password",
     * "institution code"); testLabel(aws, "234999960000121", "false"); }
     */

    public AlmaLabel(String URL, String inst_code, String user, String password) throws SOAPException
    {
        System.out.println("Contacting AlmaWebService");
        aws = AlmaWebServices.create(URL, user, password, inst_code);   
    }
    
    public String save (File file) throws IOException
    {
        if (xmlLabel != null && xmlLabel.length()>0)
        {
            PrintWriter out=new PrintWriter(file, "UTF-8");
            try
            {
                out.print("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
                
                out.print(xmlLabel);                  
            }
            catch (Exception e)
            {
                System.out.println("Can't write to file: " + e);
                return null;
            }
            finally
            {
                out.close();
            }
            return file.getCanonicalPath();
        }
        else
        {
            return null;
        }
    }

    public String getLabel(String value, String isBarcode) throws SOAPException
    {
        System.out.println("\nInvoking webservice for value: " + value + " isBarcode:" + isBarcode);
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put(AlmaWebServices.INPUT_PARAM_1, "1.0");
        paramsMap.put(AlmaWebServices.INPUT_PARAM_2, value);
        paramsMap.put(AlmaWebServices.INPUT_PARAM_3, isBarcode);

        xmlLabel=aws.invoke(AlmaWebServices.LABEL_PRINTING_WS, AlmaWebServices.LABEL_PRINTING_GET_METHOD, paramsMap);
        if (xmlLabel == null)
        {
            System.out.println("\nWebservice: failed, xmlLabel is null");
            return null;
        }
        else
        {
            System.out.println("\nxmlLabel is not null: OK");
            //System.out.print(xmlLabel);
        }
        xmlLabel=xmlLabel.substring(xmlLabel.indexOf("<printout>"), xmlLabel.indexOf("</printout>")+11);
        System.out.println("\nWebservice: success\n Data size: " + xmlLabel.length());
        return xmlLabel;
    }
    
    public String getUserLabel(String userIdentifier) throws SOAPException
    {
        System.out.println("\nInvoking User Details webservice for User Identifier: " + userIdentifier);
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put(AlmaWebServices.INPUT_PARAM_1, userIdentifier);
        paramsMap.put(AlmaWebServices.INPUT_PARAM_2, "");
        xmlLabel=aws.invoke(AlmaWebServices.GET_USER_DETAILS_WS_LINK, AlmaWebServices.GET_USER_DETAILS, paramsMap);
        if (xmlLabel != null)
        {
            System.out.println("\nWebservice: success\n Data size: " + xmlLabel.length());
            return xmlLabel;
        }
        else
        {
            System.out.println("\nWebservice: failed, xmlLabel is null");
            return null;
        }
    }
}
