package net.netdatacorp.efile.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class ClientLogicalHandler implements SOAPHandler<SOAPMessageContext>{

	private static PrintStream out = System.out;
	
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
			// we're on the inbound message from the external service.
           out.println("--===Client Handler Service -> inbound from external===--");
           saveMyFile("/TexFile/", "CL_RET_", ".XML", context);
		} else {
			// we're on the outbound message to the external service.
	       out.println("--===Client Handler Service -> outbound to external===--");
	       saveMyFile("/TexFile/", "CL_OUT_", ".XML", context);
		}
      return true;
   }

   public boolean handleFault(SOAPMessageContext context){
	   
	   out.println("------------------------------------");
		out.println("In SOAPHandler ClientLogicalHandler:handleFault()");
		logToSystemOut(context);
		out.println("Exiting SOAPHandler ClientLogicalHandler:handleFault()");
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


   public void close(MessageContext context)
   {
      // Nothing to do. Just close.
   }

	public String saveMyFile(String myDir, String myPrefix, String mySuffix, SOAPMessageContext myText)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String retFile = myPrefix + dateFormat.format(date); // + mySuffix;
		String myFile = myDir + myPrefix + dateFormat.format(date) + mySuffix;
		File file = new File(myFile);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			// if file doesn't exist, then create it
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TO DO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// write the XML to a text file.
			try {
				ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
				myText.getMessage().writeTo(msgStream);
				byte[] contentinbytes = msgStream.toString().getBytes();
				fileOutputStream.write(contentinbytes);
			} catch (IOException e) {
				// TO DO Auto-generated catch block
				e.printStackTrace();
			} catch (SOAPException e) {
				// TO DO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fileOutputStream.flush();
			} catch (IOException e) {
				// TO DO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				// TO DO Auto-generated catch block
				e.printStackTrace();
			}	
		} catch (FileNotFoundException e1) {
			// TO DO Auto-generated catch block
			e1.printStackTrace();
		}
		if(retFile.length()>35){
			return retFile.substring(0, 36);
		}else{
			return retFile.substring(0, retFile.length());
		}
	}

}