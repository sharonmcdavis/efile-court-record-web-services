package net.netdatacorp.endpoint;

import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;

public class Increment implements SOAPHandler<SOAPMessageContext> {

    public boolean handleMessage(SOAPMessageContext mc) {
        try {
            final SOAPMessage message = mc.getMessage();
            final SOAPBody body = message.getSOAPBody();
            final String localName = body.getFirstChild().getLocalName();

            if ("sumResponse".equals(localName) || "multiplyResponse".equals(localName)) {
                final Node responseNode = body.getFirstChild();
                final Node returnNode = responseNode.getFirstChild();
                final Node intNode = returnNode.getFirstChild();

                final int value = new Integer(intNode.getNodeValue());
                intNode.setNodeValue(Integer.toString(value + 1));
            }

            return true;
        } catch (SOAPException e) {
            return false;
        }
    }

    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

    public void close(MessageContext mc) {
    }

    public boolean handleFault(SOAPMessageContext mc) {
        return true;
    }
}