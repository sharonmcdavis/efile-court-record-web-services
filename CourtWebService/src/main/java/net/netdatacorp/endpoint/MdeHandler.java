package net.netdatacorp.endpoint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.IFSFileOutputStream;
import com.ibm.as400.access.Trace;

import net.netdatacorp.beans.Messages;

public class MdeHandler implements SOAPHandler<SOAPMessageContext>
{
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
	   //check for inbound message.
	   if (Boolean.FALSE.equals(context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))) {
		    System.out.println("--===< New Inbound Message To Handle >===--");
		   	SOAPMessage myMessage = context.getMessage();
			ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
			try {
				myMessage.writeTo(msgStream);
			} catch (SOAPException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			String requestXml = msgStream.toString();
			if (requestXml.length() <= 0){
				requestXml = "No XML content imported.";
			}
			String myFile = saveMyFile(context);
			try {
				Boolean itsFound = Boolean.FALSE;
				Object myObject = null;
				SOAPFactory soapFactory = SOAPFactory.newInstance();
				Iterator<?> myIterator = myMessage.getSOAPBody().getChildElements();
				while (itsFound == Boolean.FALSE){
					if (Boolean.TRUE.equals(myIterator.hasNext())){
						myObject = myIterator.next();
					} else {
						itsFound = Boolean.TRUE;
					}
					if ( myObject == null || myObject.toString().isEmpty() ){
						// we're ignoring the text.
						//System.out.println(((com.ibm.ws.webservices.engine.xmlsoap.Text) myObject).getTextContent()+" is text.");
					} else {
					
						SOAPElement myElement = (SOAPElement)myObject;
						String myOperation = null;
						//	"Set the operation from the tag"
						if(myElement.getTagName().contains("RecordFiling")){
							myOperation = "RecordFiling";
						} else if(myElement.getTagName().contains("CaseListQueryMessage") ||
							myElement.getTagName().contains("GetCaseList")){
							myOperation = "GetCaseList";
						} else if(myElement.getTagName().contains("CaseQueryMessage") || 
							myElement.getTagName().contains("GetCase")){
							myOperation = "GetCase";
						} else if(myElement.getTagName().contains("DocumentQueryMessage") ||
							myElement.getTagName().contains("GetDocument")){
							myOperation = "GetDocument";
						} else if(myElement.getTagName().contains("ServiceInformationQueryMessage") ||
							myElement.getTagName().contains("GetServiceInformation")){
							myOperation = "GetServiceInformation";
						} else if(myElement.getTagName().contains("CreateCase")){
							myOperation = "CreateCase";
						} else if(myElement.getTagName().contains("ServiceInformationHistoryQueryMessage")  ||
							myElement.getTagName().contains("GetServiceInformationHistory")){
							myOperation = "GetServiceInformationHistory";
						} else if(myElement.getTagName().contains("ServiceAttachCaseListQueryMessage") ||
							myElement.getTagName().contains("GetServiceAttachCaseList")){
							myOperation = "GetServiceAttachCaseList";
						} else {
							myOperation = "UnknownOperation";
						}
						if(myOperation != null){
							Name childDocName = soapFactory.createName("IO_DOCUMENT");
							Name childFuncName = soapFactory.createName("IO_FUNCTION");
							SOAPElement myInput1 = myElement.addChildElement(childDocName);
							SOAPElement myInput2 = myElement.addChildElement(childFuncName);
							myInput1.addTextNode(myFile);
							myInput2.addTextNode(myOperation);
							System.out.println("filename= '"+myFile+"'");
							System.out.println("Operation = '"+myOperation+"'");
							itsFound = Boolean.TRUE;
						} 
					}
				}
			} catch (SOAPException e1) {
				e1.printStackTrace();
			}
			
			try {
				myMessage.saveChanges();
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		} else { // we're on the outbound message.
			SOAPMessage myMessage = context.getMessage();
			try {
				Boolean itsFound = Boolean.FALSE;
				Object myObject = null;
				Iterator<?> myIterator = myMessage.getSOAPBody().getChildElements();
				while (itsFound == Boolean.FALSE){
					if (Boolean.TRUE.equals(myIterator.hasNext())){
						myObject = myIterator.next();
					} else {
						itsFound = Boolean.TRUE;
					}

					if ( myObject == null || myObject.toString().isEmpty() ){
						// we're ignoring the text.
						//System.out.println(((com.ibm.ws.webservices.engine.xmlsoap.Text) myObject).getTextContent()+" is text.");
					} else {

						SOAPElement myElement = (SOAPElement)myObject;
						String textContent = myElement.getTextContent();
						if (textContent != null){
							// This resets the headers & reinserts the message contents.
							myMessage.getSOAPBody().removeContents();
							SOAPElement newElement = strToSOAPElement(textContent);
							myMessage.getSOAPBody().addChildElement(newElement);
							SOAPElement newHeaderElement = strToSOAPElement(secHeaderTimestamp());
							if(myMessage.getSOAPPart().getEnvelope().getHeader() == null){
								myMessage.getSOAPPart().getEnvelope().addHeader();
								myMessage.getSOAPPart().getEnvelope().getHeader().addChildElement(newHeaderElement);
							} else {
								myMessage.getSOAPHeader().addChildElement(newHeaderElement);
							}
							logToSystemOut(myMessage,"Synchronous Response");
						} else {
//							System.out.println("Parse failed.");
						}
						itsFound = Boolean.TRUE;
					}
				}
			} catch (SOAPException e1) {
				e1.printStackTrace();
			}
			
			try {
				myMessage.saveChanges();
				System.out.println("-----====<{ End Transaction }>====-----");
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		
		}
      return true;
   }

   public boolean handleFault(SOAPMessageContext context)
   {
	   SOAPMessage myMessage = context.getMessage();
	   System.err.println("------=======<{ Fault Handler }>======------");
	   logToSystemErr(myMessage,"Program Response");
	   return true;
   }

   public void close(MessageContext context)
   {
      // TO DO
   }

	private String saveMyFile(SOAPMessageContext myText)
	{
//		System.out.println("-= JT400 version:" + utilities.AboutToolbox.getVersionDescription());
		// Build variables.
		AS400 sys = null;
		IFSFileOutputStream oStream = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Properties cfgProps = getConfigFile("TFREQPRC.config");
		if (cfgProps.isEmpty()){
			System.out.println("Error: No data read from TFREQPRC.config file.");
		}
		String myServer = cfgProps.getProperty("WDT_HOSTNAME");
		String myUser = cfgProps.getProperty("WDT_USERID");
		String myPasswd = cfgProps.getProperty("WDT_PASSWORD");
		cfgProps = null;
		cfgProps = getPropFile("/TexFile/app.config");
		String cmsfilePath = cfgProps.getProperty("cms.filePath","/TexFile/XML_Incoming/");
		String cmsfilePrefix = cfgProps.getProperty("cms.filePrefix","TF");
		String cmsfileSuffix = cfgProps.getProperty("cms.fileSuffix",".XML");
		cfgProps = null;
		String retFile = cmsfilePrefix + dateFormat.format(date);
		String myFile = cmsfilePath + retFile + cmsfileSuffix;
		try {
			// Connect to server
			sys = new AS400(myServer, myUser, myPasswd);
			sys.connectService(AS400.FILE);
			// Create output stream to create file with proper CCSID
			oStream = new IFSFileOutputStream(sys, myFile, 1252);
			// write the XML to the output stream (file).
			try {
				ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
				myText.getMessage().writeTo(msgStream);
				byte[] contentinbytes = msgStream.toString().getBytes();
				oStream.write(contentinbytes);
				// ensure all data is written.
				oStream.flush();
				// close output stream.
				oStream.close();
				// close connections.
				if (sys.isConnected(AS400.FILE))
					sys.disconnectAllServices();
			} catch (IOException e) {
				// TO DO Auto-generated catch block
				System.out.println("-=IOException writing to output stream=-");
			} catch (SOAPException e) {
				// TO DO Auto-generated catch block
				System.out.println("-=SOAPException writing to output stream=-");
			}
		} catch (IOException e1) {
			// TO DO Auto-generated catch block
			System.out.println("-=IOException IFSFileOutputStream=-");
		} catch (AS400SecurityException e1) {
			// TO DO Auto-generated catch block
			System.out.println("-=AS400 Security Exception=-");
		}
		if(retFile.length()>255){
			return retFile.substring(0, 256);
		}else{
			return retFile.substring(0, retFile.length());
		}
	}

	public String secHeaderTimestamp(){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date thisDate = new Date();
		Date thatDate = new Date(); 
		thatDate.setTime(thisDate.getTime() + 5*60*1000);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcDate = df.format(thisDate);
		String expDate = df.format(thatDate);
		//soapenv:mustUnderstand=\"1\"
		String msgHeader = "<o:Security xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"+
				"<u:Timestamp u:Id=\"_0\" xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><u:Created>"+utcDate+
				"</u:Created><u:Expires>"+expDate+"</u:Expires></u:Timestamp></o:Security>";
		return msgHeader;
	}
	public SOAPElement strToSOAPElement(String inXML){
		try {
			DocumentBuilderFactory myBuilder = DocumentBuilderFactory.newInstance();
			myBuilder.setNamespaceAware(true);
			InputStream myStream = new ByteArrayInputStream(inXML.getBytes());
			Document myDoc = myBuilder.newDocumentBuilder().parse(myStream);
            MessageFactory msgFactory = MessageFactory.newInstance();
            SOAPMessage    message    = msgFactory.createMessage();
            SOAPBody       soapBody   = message.getSOAPBody();
            
            // This returns the SOAPBodyElement that contains ONLY the Payload
 		return soapBody.addDocument(myDoc);
        } catch (SOAPException  e) {
            System.out.println("SOAPException : " + e);
            return null;
 
        } catch (IOException  e) {
            System.out.println("IOException : " + e);
            return null;
 
        } catch (ParserConfigurationException  e) {
            System.out.println("ParserConfigurationException : " + e);
            return null;
 
        } catch (SAXException  e) {
            System.out.println("SAXException : " + e);
            return null;
 
        }
	}
    private void logToSystemOut(SOAPMessage message, String strIdentifier) {
        try {
        	System.out.println("--=== "+strIdentifier+" ===--");
        	// This only writes out the message identifier. To log the whole message, must convert it to text.
        	message.writeTo(System.out);
        	System.out.println("");   
        	System.out.println("--=== End "+strIdentifier+" ===--");   
        } catch (Exception e) {
        	System.out.println("Exception in SOAP Handler #1: " + e);
        }
    }
    private void logToSystemErr(SOAPMessage message, String strIdentifier) {
        try {
        	System.err.println("--=== "+strIdentifier+" ===--");
        	// This only writes out the message identifier. To log the whole message, must convert it to text.
        	message.writeTo(System.out);
        	System.err.println("");   
        	System.err.println("--=== End "+strIdentifier+" ===--");   
        } catch (Exception e) {
        	System.err.println("Exception in SOAP Handler #1: " + e);
        }
    }
    private Properties getPropFile(String myPath){
    	InputStream iStrm = null;
    	Properties prop = new Properties();
    	try {
    		iStrm = new FileInputStream(myPath); // "/TexFile/app.config");
    		prop.load(iStrm);
    		
    	} catch (FileNotFoundException e) {
    		System.out.println("--=FileNotFoundException getPropFile=--");
    			e.printStackTrace();
    	} catch (IOException e) {
    		System.out.println("--=IOException getPropFile=--"); 
                e.printStackTrace();
    	} finally {
    		if (iStrm != null){
    			try {
    				iStrm.close();
    			} catch (Exception e) {
					System.out.println("Error Trying InputStream.close.");
					Trace.log(Trace.ERROR, e);
				}
    		}
    	}
    	return prop;
    }
    private Properties getConfigFile(String myFile){
  		InputStream is = null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(myFile);
			if (is != null) {
				Properties rtcfgProps = new Properties();
				rtcfgProps.load(is);
				
				return rtcfgProps;
			}
			else {
				System.out.println("Error trying to open config file.");
				Trace.log(Trace.DIAGNOSTIC, Messages.getMsgText("ConfigFileNotFound", myFile));
			}
		} catch (Exception e) {
			System.out.println("Error trying AbstractProgramCallBean.");
			Trace.log(Trace.ERROR, e);
		} finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (Exception e) {
					System.out.println("Error Trying InputStream.close.");
					Trace.log(Trace.ERROR, e);
				}
			}
		}
		
		return null;
    }
}