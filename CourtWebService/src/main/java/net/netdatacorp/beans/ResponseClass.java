package net.netdatacorp.beans;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.ibm.as400.access.Trace;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * pcml: iseries.wsbeans.tfreqprc.WebService.pcml
 * <!-- end-system-doc -->
 * 
 * Result bean for WebService Web service
 */
public class ResponseClass implements Serializable
{
    private static final long serialVersionUID = 792296825748225682L;

    // Program parameter: IO_XML.
    private String IO_XML = "";

    public ResponseClass()
    {
        super();
    }

    /*
     * Set the value of the program parameter: IO_XML.
     * 
     * @value The value of the parameter.
     */
    public void setIO_XML(String value)
    {
        IO_XML = value;
    }

    /*
     * Get the value of the program parameter: IO_XML.
     * 
     * @return The value of the parameter.
     */
    public String getIO_XML()
    {
        return IO_XML;
    }

    /*
     * This method converts all bean properties to an XML string.
     * 
     * @return An XML string of all the Java bean properties and their values.
     */
    public String toTxtString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(getIO_XML().toString() + "\n");
        return removeInvalidXmlCharacters(buf.toString());
    }

    /*
     * This method converts all bean properties to an XML string.
     * 
     * @return An XML string of all the Java bean properties and their values.
     */
    public String toXMLString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buf.append("<WebService>\n");
        buf.append("  <IO_XML>" + getIO_XML().toString() + "</IO_XML>\n");
        buf.append("</WebService>\n");
        return xmlize(removeInvalidXmlCharacters(buf.toString()));
    }

    /*
     * Removes invalid XML characters from the input string.
     * 
     * @return The XML string.
     */
    private String removeInvalidXmlCharacters(String result)
    {
        int i=0;
        char c = 0x00;
        while(i < result.length()) {
            c = result.charAt(i);
            if (!((c == 0x9) || ( c == 0xA ) || ( c == 0xD ) || ( c >= 0x20 && c <= 0xD7FF ) || ( c >= 0xE000 && c <= 0xFFFD ) || ( c >= 0x10000 && c <= 0x10FFFF )) ) {
                Trace.log(Trace.ERROR, "Character at location "+i+"=["+c+"] is invalid XML character and is removed.");
                if (i == 0) {
                    result = result.substring(1);
                }
                else if (i == result.length()-1) {
                    result = result.substring(0, i);
                }
                else {
                    result = result.substring(0, i) + result.substring(i+1);
                }
            }
            else {
                i++;
            }
        }
        return result;
    }

    private String xmlize(String result)
    {
    	result.replaceAll(Pattern.quote("&lt;"), "&gt;");
    	result.replaceAll(Pattern.quote("&gt;"), ">");
    	return result;
    }

}
