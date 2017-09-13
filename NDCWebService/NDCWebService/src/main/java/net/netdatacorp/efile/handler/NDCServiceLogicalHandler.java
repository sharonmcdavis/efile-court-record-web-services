package net.netdatacorp.efile.handler;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class NDCServiceLogicalHandler implements SOAPHandler<SOAPMessageContext>
{
	@Override
	public Set<QName> getHeaders()
	{
		final QName securityHeader = new QName(
	            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
	            "Security",
	            "wsse");
	 
	        // ... "understand" the response, very complex logic goes here
	 
	        final HashSet<QName> headers = new HashSet<QName>();
	        headers.add(securityHeader);
//	        System.out.println("-= Got headers: " + headers);
	 
	        // notify the runtime that this is handled
	        return headers;
	}

   public boolean handleMessage(SOAPMessageContext context)
   {
	   //check for message direction.
	   if (Boolean.FALSE.equals(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))) {
		   // we're on the inbound message.
		   SOAPMessage myMessage = context.getMessage();
		   String requestXml = getSOAPMessageAsString(myMessage);
		   if (requestXml.length() > 0){
			   // write to log.
				System.out.println("--===NDCService - InBound===--");
				// write out the message we received.
//				System.out.println(requestXml + "\n");
				if (requestXml.contains("<NotifyDocketingComplete")) {
					System.out.println("NotifyDocketingComplete found. Processing...");
				} else {
					// we can't send it, so return an error response message.
					System.out.println("NotifyDocketingComplete NOT found. ERROR!\n");
				}
			} else {
				requestXml = "No XML content imported.";
				System.out.println(requestXml + "\n");
			}
			
			try {
				myMessage.saveChanges();
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		} else { // we're on the outbound message.
//		   	SOAPMessage myMessage = context.getMessage();
//			String requestXml = getSOAPMessageAsString(myMessage);
			System.out.println("--===NDCService - OutBound===--");
//			System.out.println(requestXml + "\n");
			
		}
      return true;
   }

   public boolean handleFault(SOAPMessageContext context)
   {
      return true;
   }

   public void close(MessageContext context)
   {
      // Do nothing. Just close.
   }

   private String getSOAPMessageAsString(SOAPMessage msg) {
   	ByteArrayOutputStream baos = null;
   	String s = null;
       try {
       	baos = new ByteArrayOutputStream();
           msg.writeTo(baos);
           s = baos.toString();
       } catch(Exception e) {
       	e.printStackTrace();
       }
       return s;
   }

}