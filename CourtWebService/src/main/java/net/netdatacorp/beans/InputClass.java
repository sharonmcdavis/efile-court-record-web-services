package net.netdatacorp.beans;

import java.io.Serializable;
import com.ibm.as400.access.Trace;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * pcml: iseries.wsbeans.tfreqprc.WebService.pcml
 * <!-- end-system-doc -->
 * 
 * InputClass bean for WebService Web service.
 */
public class InputClass implements Serializable
{
    private static final long serialVersionUID = 7456959804783912910L;

    // Program parameter: IO_DOCUMENT.
    private String IO_DOCUMENT = "";

    // Program parameter: IO_FUNCTION.
    private String IO_FUNCTION = "";

    // Program parameter: IO_XML.
    private String IO_XML = "";

    public InputClass()
    {
        super();
    }

    /*
     * Set the value of the program parameter: IO_DOCUMENT.
     * 
     * @value The value of the parameter.
     */
    public void setIO_DOCUMENT(String value)
    {
        IO_DOCUMENT = value;
    }

    /*
     * Get the value of the program parameter: IO_DOCUMENT.
     * 
     * @return The value of the parameter.
     */
    public String getIO_DOCUMENT()
    {
        return IO_DOCUMENT;
    }

    /*
     * Set the value of the program parameter: IO_FUNCTION.
     * 
     * @value The value of the parameter.
     */
    public void setIO_FUNCTION(String value)
    {
        IO_FUNCTION = value;
    }

    /*
     * Get the value of the program parameter: IO_FUNCTION.
     * 
     * @return The value of the parameter.
     */
    public String getIO_FUNCTION()
    {
        return IO_FUNCTION;
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
    public String toXMLString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buf.append("<WebService>\n");
        buf.append("  <IO_DOCUMENT>" + getIO_DOCUMENT().toString() + "</IO_DOCUMENT>\n");
        buf.append("  <IO_FUNCTION>" + getIO_FUNCTION().toString() + "</IO_FUNCTION>\n");
        buf.append("  <IO_XML>" + getIO_XML().toString() + "</IO_XML>\n");
        buf.append("</WebService>\n");
        return removeInvalidXmlCharacters(buf.toString());
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

}
