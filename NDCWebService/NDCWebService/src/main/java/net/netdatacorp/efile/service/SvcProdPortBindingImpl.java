package net.netdatacorp.efile.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.xerces.dom.ElementNSImpl;

import net.netdatacorp.efile.handler.ClientLogicalHandler;
import net.netdatacorp.efile.handler.EnvelopeLoggingSOAPHandler;

@javax.jws.WebService (
		endpointInterface="oasis.names.tc.legalxml_courtfiling.wsdl.webservicemessagingprofile_definitions_4.SvcProdPortType", 
		targetNamespace="urn:oasis:names:tc:legalxml-courtfiling:wsdl:WebServiceMessagingProfile-Definitions-4.0", 
		serviceName="NDCService", 
		portName="NDCSvcProdPort")
// @javax.xml.ws.BindingType (value=javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING)
@javax.xml.ws.BindingType (value=javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@MTOM(threshold=300000000)

public class SvcProdPortBindingImpl{

    @SuppressWarnings("rawtypes")
	public Object notifyDocketingComplete(Object notifyDocketingCompleteRequest) {
    	// for return data.
    	Properties cfgProps = getConfigFile();
//    	cfgProps.list(System.out); //only needed for full listing of what's in the file.

    	try {
			MessageFactory mf = MessageFactory.newInstance();
			SOAPMessage reqMesg = mf.createMessage();
	    	SOAPMessage response = mf.createMessage();
	    	SOAPElement inContent = toSOAPElement(notifyDocketingCompleteRequest);
	    	reqMesg.getSOAPBody().appendChild(inContent.getFirstChild());
//	    	System.out.println("--=Request To Send=--");
//	    	reqMesg.writeTo(System.out);
//	    	System.out.println("");
    	
	    	System.out.println("--== Config Entries ==--");
			String namespace = cfgProps.getProperty("efm.namespace","");
			System.out.println("efm.namespace = " + namespace);
			String serviceName = cfgProps.getProperty("efm.serviceName","");
			System.out.println("efm.serviceName = " + serviceName);
			String portName = cfgProps.getProperty("efm.portName","");
			System.out.println("efm.portName = " + portName);
			String methodName = cfgProps.getProperty("efm.methodName.nDC","");
			System.out.println("efm.methodName = " + methodName);
			String endpointAddress = cfgProps.getProperty("efm.prodEndpointAddress","");
			System.out.println("efm.endpointAddress = " + endpointAddress);
			String WS_URL = cfgProps.getProperty("efm.prodWS_URL","");
			System.out.println("efm.WS_URL = " + WS_URL);
//			String soapAction = "\"" + namespace + "/" + serviceName + "/" + methodName + "\"";
//			System.out.println("soapAction = " + soapAction);
			
			
	    	// Build the dispatch
			System.out.println("--== Building Message ==--");
			System.out.println("--== wsdl URL ==--");
			URL wsdlLocation = new URL(WS_URL);
			System.out.println("--== Service QName ==--");
			QName serviceQName = new QName(namespace, serviceName);
			System.out.println("--== Port QName ==--");
			QName portQName = new QName(namespace, portName);
			System.out.println("--== Creating Service ==--");
			Service service = Service.create(wsdlLocation, serviceQName);
			
			// Create a dispatch instance.
			System.out.println("--== Creating Dispatch Instance ==--");
			Dispatch<SOAPMessage> dispatch = service.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);
//			dispatch.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY,true);
//			dispatch.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY,soapAction);
			// Use Dispatch as BindingProvider
			System.out.println("--== Building Message ==--");
			BindingProvider bp = (BindingProvider) dispatch;
			SOAPBinding binding = (SOAPBinding) bp.getBinding();
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
			binding.setMTOMEnabled(true);
			// Double-check the endpoint.
//			String dispatchEndpoint = (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
//			System.out.println("--== Dispatch Endpoint ==--");
//			System.out.println("Endpoint = " + dispatchEndpoint);
			
			// Set up handler chain for the client dispatch
			System.out.println("--== Building Handler Chain ==--");
			List<Handler> handlerChain = new ArrayList<Handler>();
			ClientLogicalHandler cAuth = new ClientLogicalHandler();
			EnvelopeLoggingSOAPHandler logging = new EnvelopeLoggingSOAPHandler();
			handlerChain.add(cAuth);
			handlerChain.add(logging);
			binding.setHandlerChain(handlerChain);
			// Put it all in the context object
			System.out.println("--== Mapping Context ==--");
			Map<String, Object> myContext = bp.getRequestContext();
			
			
//---===== Digital signature part. ==---//
/*			
			System.out.println("--=== Attempting Signature ===--");
			
				try {
					//generate WSSFactory instance
					WSSFactory wsFactory = WSSFactory.getInstance();

					//generate WSSGenerationContext instance
					WSSGenerationContext gencont = wsFactory.newWSSGenerationContext();

					//generate callback handler
					X509GenerateCallbackHandler callbackHandler = new X509GenerateCallbackHandler(
							cfgProps.getProperty("x509.storeRef",""),
							cfgProps.getProperty("x509.storePath",""),
							cfgProps.getProperty("x509.storeType",""),
							cfgProps.getProperty("x509.storePassword","").toCharArray(),
							cfgProps.getProperty("x509.alias",""),
							cfgProps.getProperty("x509.keyPassword","").toCharArray(),
							cfgProps.getProperty("x509.keyName",""),
							null); // java.util.List<java.security.cert.CertStore> certStores
					
					//generate the security token used to the signature
					SecurityToken token = wsFactory.newSecurityToken(X509Token.class, callbackHandler);

					//generate WSSSignature instance
					WSSSignature sig = wsFactory.newWSSSignature(token);
				
					
					// Set the part to be signed specified by WSSTimestamp
					WSSTimestamp timestamp = wsFactory.newWSSTimestamp();
					sig.addSignPart(timestamp);

					//set the canonicalization method
					// DEFAULT: WSSSignature.EXC_C14N
					sig.setCanonicalizationMethod(WSSSignature.EXC_C14N);
					
					//set the signature method
					// DEFAULT: WSSSignature.RSA_SHA1
					sig.setSignatureMethod(WSSSignature.RSA_SHA1);
					
					//add the WSSSignature to the WSSGenerationContext
					gencont.add(sig);

					//generate the WS-Security header
					gencont.process(myContext);  
					
					System.out.println("===Signed Request===");

				} catch (WSSException e) {
					e.printStackTrace();
				}    	
				
			// invoke the dispatch and send the message.	
			System.out.println("--== Do invoke. ==--");
			response = dispatch.invoke(reqMesg);
			ElementNSImpl myResponseElement = (ElementNSImpl)response.getSOAPBody();
    	
			return myResponseElement;
			
		} catch (SOAPException e1) {
			e1.printStackTrace();
		} catch (SOAPFaultException e1) {
			SOAPFault fault = e1.getFault();
			e1.printStackTrace();
			return fault;
		} catch (Exception wse) {
			String errResponse = wse.getMessage();
			wse.printStackTrace();
			System.out.println(wse);
	    	Object response = errResponse;
	    	return response;
		}
    	
    	Object response = null;
    	return response;
    }
    
    @SuppressWarnings("rawtypes")
	public Object notifyCaseAssignmentComplete(Object notifyCaseAssignmentCompleteRequest) {
    	// for return data.
    	Properties cfgProps = getConfigFile();
//    	cfgProps.list(System.out); //only needed for full listing of what's in the file.

    	try {
			MessageFactory mf = MessageFactory.newInstance();
			SOAPMessage reqMesg = mf.createMessage();
	    	SOAPMessage response = mf.createMessage();
	    	SOAPElement inContent = toSOAPElement(notifyCaseAssignmentCompleteRequest);
	    	reqMesg.getSOAPBody().appendChild(inContent.getFirstChild());
//	    	System.out.println("--=Request To Send=--");
//	    	reqMesg.writeTo(System.out);
//	    	System.out.println("");
    	
	    	System.out.println("--== Config Entries ==--");
			String namespace = cfgProps.getProperty("efm.namespace","");
			System.out.println("efm.namespace = " + namespace);
			String serviceName = cfgProps.getProperty("efm.serviceName","");
			System.out.println("efm.serviceName = " + serviceName);
			String portName = cfgProps.getProperty("efm.portName","");
			System.out.println("efm.portName = " + portName);
			String methodName = cfgProps.getProperty("efm.methodName.nCAC","");
			System.out.println("efm.methodName = " + methodName);
			String endpointAddress = cfgProps.getProperty("efm.prodEndpointAddress","");
			System.out.println("efm.endpointAddress = " + endpointAddress);
			String WS_URL = cfgProps.getProperty("efm.prodWS_URL","");
			System.out.println("efm.WS_URL = " + WS_URL);
//			String soapAction = "\"" + namespace + "/" + serviceName + "/" + methodName + "\"";
//			System.out.println("soapAction = " + soapAction);
			
			
	    	// Build the dispatch
			System.out.println("--== Building Message ==--");
			System.out.println("--== wsdl URL ==--");
			URL wsdlLocation = new URL(WS_URL);
			System.out.println("--== Service QName ==--");
			QName serviceQName = new QName(namespace, serviceName);
			System.out.println("--== Port QName ==--");
			QName portQName = new QName(namespace, portName);
			System.out.println("--== Creating Service ==--");
			Service service = Service.create(wsdlLocation, serviceQName);
			
			// Create a dispatch instance.
			System.out.println("--== Creating Dispatch Instance ==--");
			Dispatch<SOAPMessage> dispatch = service.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);
//			dispatch.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY,true);
//			dispatch.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY,soapAction);
			// Use Dispatch as BindingProvider
			System.out.println("--== Building Message ==--");
			BindingProvider bp = (BindingProvider) dispatch;
			SOAPBinding binding = (SOAPBinding) bp.getBinding();
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
			binding.setMTOMEnabled(true);
			// Double-check the endpoint.
//			String dispatchEndpoint = (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
//			System.out.println("--== Dispatch Endpoint ==--");
//			System.out.println("Endpoint = " + dispatchEndpoint);
			
			// Set up handler chain for the client dispatch
			System.out.println("--== Building Handler Chain ==--");
			List<Handler> handlerChain = new ArrayList<Handler>();
			p.ClientLogicalHandler cAuth = new p.ClientLogicalHandler();
			p.EnvelopeLoggingSOAPHandler logging = new p.EnvelopeLoggingSOAPHandler();
			handlerChain.add(cAuth);
			handlerChain.add(logging);
			binding.setHandlerChain(handlerChain);
			// Put it all in the context object
			System.out.println("--== Mapping Context ==--");
			Map<String, Object> myContext = bp.getRequestContext();
			
			
//---===== Digital signature part. ==---//
			
			System.out.println("--=== Attempting Signature ===--");
			
				try {
					//generate WSSFactory instance
					WSSFactory wsFactory = WSSFactory.getInstance();

					//generate WSSGenerationContext instance
					WSSGenerationContext gencont = wsFactory.newWSSGenerationContext();

					//generate callback handler
					X509GenerateCallbackHandler callbackHandler = new X509GenerateCallbackHandler(
							cfgProps.getProperty("x509.storeRef",""),
							cfgProps.getProperty("x509.storePath",""),
							cfgProps.getProperty("x509.storeType",""),
							cfgProps.getProperty("x509.storePassword","").toCharArray(),
							cfgProps.getProperty("x509.alias",""),
							cfgProps.getProperty("x509.keyPassword","").toCharArray(),
							cfgProps.getProperty("x509.keyName",""),
							null); // java.util.List<java.security.cert.CertStore> certStores
					
					//generate the security token used to the signature
					SecurityToken token = wsFactory.newSecurityToken(X509Token.class, callbackHandler);

					//generate WSSSignature instance
					WSSSignature sig = wsFactory.newWSSSignature(token);
				
					
					// Set the part to be signed specified by WSSTimestamp
					WSSTimestamp timestamp = wsFactory.newWSSTimestamp();
					sig.addSignPart(timestamp);

					//set the canonicalization method
					// DEFAULT: WSSSignature.EXC_C14N
					sig.setCanonicalizationMethod(WSSSignature.EXC_C14N);
					
					//set the signature method
					// DEFAULT: WSSSignature.RSA_SHA1
					sig.setSignatureMethod(WSSSignature.RSA_SHA1);
					
					//add the WSSSignature to the WSSGenerationContext
					gencont.add(sig);

					//generate the WS-Security header
					gencont.process(myContext);  
					
					System.out.println("===Signed Request===");

				} catch (WSSException e) {
					e.printStackTrace();
				}    	
				*/
			
			
			// invoke the dispatch and send the message.	
			System.out.println("--== Do invoke. ==--");
			response = dispatch.invoke(reqMesg);
			ElementNSImpl myResponseElement = (ElementNSImpl)response.getSOAPBody();
    	
			return myResponseElement;
			
		} catch (SOAPException e1) {
			e1.printStackTrace();
		} catch (SOAPFaultException e1) {
			SOAPFault fault = e1.getFault();
			e1.printStackTrace();
			return fault;
		} catch (Exception wse) {
			String errResponse = wse.getMessage();
			wse.printStackTrace();
			System.out.println(wse);
	    	Object response = errResponse;
	    	return response;
		}
    	
    	Object response = null;
    	return response;
    }
    
    private SOAPElement toSOAPElement(Object inElement){
    	SOAPElement retElement = null;
    	try {
    		ElementNSImpl myElement = (ElementNSImpl)inElement;
    		org.w3c.dom.Document myDoc = myElement.getOwnerDocument();
    		SOAPFactory sf = SOAPFactory.newInstance();
    		retElement = sf.createElement("root", "", "");
    		retElement.appendChild(myDoc.getFirstChild().getFirstChild());
    	} catch (SOAPException e) {
    		return null;
    	}
    	return retElement; 
    }
    
    private Properties getConfigFile(){
    	Properties prop = new Properties();
    	try {
    		InputStream iStrm = new FileInputStream("/TexFile/app.config");
    		prop.load(iStrm);
    		
    	} catch (FileNotFoundException e) {
                e.printStackTrace();
    	} catch (IOException e) {
                e.printStackTrace();
    	}
    	return prop;
    }
    
}