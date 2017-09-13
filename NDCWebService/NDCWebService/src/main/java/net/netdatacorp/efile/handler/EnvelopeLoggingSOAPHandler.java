package net.netdatacorp.efile.handler;

import java.io.PrintStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/*
 * Log the whole SOAP message
 */
public class EnvelopeLoggingSOAPHandler extends BaseHandler<SOAPMessageContext> implements SOAPHandler<SOAPMessageContext> {
    
    private static PrintStream out = System.out;
	private static final String HANDLER_NAME = "EnvelopeLoggingSOAPHandler";
     
    public EnvelopeLoggingSOAPHandler(){
		super();
		super.setHandlerName(HANDLER_NAME);		
    }
    
    public boolean handleMessage(SOAPMessageContext smc) {
		out.println("------------------------------------");
		out.println("In SOAPHandler " + HandlerName + ":handleMessage()");
		logToSystemOut(smc);
		out.println("Exiting SOAPHandler " + HandlerName + ":handleMessage()");
		out.println("------------------------------------");    	
        return true;
    }

    private void logToSystemOut(SOAPMessageContext smc) {
        Boolean outboundProperty = (Boolean)
            smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        
        if (outboundProperty.booleanValue()) {
            out.println("direction = outbound");
        } else {
            out.println("direction = inbound");
        }
        
        SOAPMessage message = smc.getMessage();
        try {
        	out.println("-=SOAP Message Identifier=-");
        	// This only writes out the message identifier. To log the whole message, must convert it to text.
        	message.writeTo(out);
            out.println("");   
            out.println("-= End Identifier =-");   
        } catch (Exception e) {
            out.println("Exception in SOAP Handler #1: " + e);
        }
    }

	public Set<QName> getHeaders() {
		return null;
	}
}
