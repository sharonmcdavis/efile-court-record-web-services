//
// Generated By:JAX-WS RI IBM 2.2.1-11/28/2011 08:28 AM(foreman)- (JAXB RI IBM 2.2.3-11/28/2011 06:21 AM(foreman)-)
//


package net.netdatacorp.efile.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NotifyCaseAssignmentComplete complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NotifyCaseAssignmentComplete">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NotifyCaseAssignmentCompleteRequest" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "NotifyCaseAssignmentComplete", namespace = "urn:oasis:names:tc:legalxml-courtfiling:wsdl:WebServiceMessagingProfile-Definitions-4.0")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotifyCaseAssignmentComplete", propOrder = {
    "NotifyCaseAssignmentCompleteRequest"
})
public class NotifyCaseAssignmentComplete {

    @XmlElement(name = "NotifyCaseAssignmentCompleteRequest", namespace = "urn:oasis:names:tc:legalxml-courtfiling:wsdl:WebServiceMessagingProfile-Definitions-4.0")
    protected Object NotifyCaseAssignmentCompleteRequest;

    /**
     * Gets the value of the NotifyCaseAssignmentCompleteRequest property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getNotifyCaseAssignmentCompleteRequest() {
        return NotifyCaseAssignmentCompleteRequest;
    }

    /**
     * Sets the value of the NotifyCaseAssignmentCompleteRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setNotifyCaseAssignmentCompleteRequest(Object value) {
        this.NotifyCaseAssignmentCompleteRequest = value;
    }

}
