//
// Generated By:JAX-WS RI IBM 2.2.1-11/28/2011 08:28 AM(foreman)- (JAXB RI IBM 2.2.3-11/28/2011 06:21 AM(foreman)-)
//


package net.netdatacorp.efile.service;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * <p>Java class for NotifyDocketingComplete complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NotifyDocketingComplete">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NotifyDocketingCompleteRequest" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "NotifyDocketingComplete", namespace = "urn:oasis:names:tc:legalxml-courtfiling:wsdl:WebServiceMessagingProfile-Definitions-4.0")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotifyDocketingComplete", propOrder = {
    "notifyDocketingCompleteRequest"
})
public class NotifyDocketingComplete {

    @XmlElement(name = "NotifyDocketingCompleteRequest", namespace = "urn:oasis:names:tc:legalxml-courtfiling:wsdl:WebServiceMessagingProfile-Definitions-4.0")
    protected Object notifyDocketingCompleteRequest;

    /**
     * Gets the value of the notifyDocketingCompleteRequest property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getNotifyDocketingCompleteRequest() {
        return notifyDocketingCompleteRequest;
    }

    /**
     * Sets the value of the notifyDocketingCompleteRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setNotifyDocketingCompleteRequest(Object value) {
        this.notifyDocketingCompleteRequest = value;
    }

}