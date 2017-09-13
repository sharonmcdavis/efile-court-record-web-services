package net.netdatacorp.efile.handler;

import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;
import net.netdatacorp.efile.service.SvcProdPortBindingImpl;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceConfig.class);

	@Value("${efile.ndc.ws.endpoint-url}")
	private String url;

	@Value("${efile.ndc.ws.key-store}")
	private Resource keyStore;

	@Value("${efile.ndc.ws.key-store-password}")
	private String keyStorePassword;

	@Value("${efile.ndc.ws.trust-store}")
	private Resource trustStore;

	@Value("${efile.ndc.ws.trust-store-password}")
	private String trustStorePassword;

//	public static final String BASE_URL = "/ndc-web-service";
//	public static final String SERVICE_URL = "/ws";
	
//    @Bean
//    public ServletRegistrationBean cxfServlet() {
//        return new ServletRegistrationBean(new CXFServlet(), BASE_URL + "/*");
//    }
    
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("net.netdatacorp.efile");
        return marshaller;
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }   
    
//    @Bean
//    public SvcProdPortBindingImpl svcProdPortBindingImpl() {
//    	return new SvcProdPortBindingImpl();
//    }
//    
    @Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/*");
	}
    
//    @Bean
//    public Endpoint endpoint() {
//        EndpointImpl endpoint = new EndpointImpl(springBus());        
//  	endpointInterface="oasis.names.tc.legalxml_courtfiling.wsdl.webservicemessagingprofile_definitions_4.SvcProdPortType", 
//		targetNamespace="urn:oasis:names:tc:legalxml-courtfiling:wsdl:WebServiceMessagingProfile-Definitions-4.0", 
//		serviceName="NDCService", 
//		portName="NDCSvcProdPort")
//   endpoint.setServiceName(SvcProdPortBindingImpl().getServiceName());
//        endpoint.setWsdlLocation(SvcProdPortBindingImpl().getWSDLDocumentLocation().toString());
//        endpoint.publish(SERVICE_URL);
//        return endpoint;
//    }  
    
    @Bean
	public SvcProdPortBindingImpl teamClient(Jaxb2Marshaller marshaller) 
		throws Exception {
    	SvcProdPortBindingImpl impl = new SvcProdPortBindingImpl();
//    	impl.setDefaultUri(this.url);
//    	impl.setMarshaller(marshaller);
//        impl.setUnmarshaller(marshaller);
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(keyStore.getInputStream(), keyStorePassword.toCharArray());

        LOGGER.info("Loaded keystore: " + keyStore.getURI().toString());
        try {
            keyStore.getInputStream().close();
        } catch (IOException e) {
        }
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(ks, keyStorePassword.toCharArray());

        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(trustStore.getInputStream(), trustStorePassword.toCharArray());
        LOGGER.info("Loaded trustStore: " + trustStore.getURI().toString());
        try {
            trustStore.getInputStream().close();
        } catch(IOException e) {
        }
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(ts);

        HttpsUrlConnectionMessageSender messageSender = new HttpsUrlConnectionMessageSender();
        messageSender.setKeyManagers(keyManagerFactory.getKeyManagers());
        messageSender.setTrustManagers(trustManagerFactory.getTrustManagers());

        // otherwise: java.security.cert.CertificateException: No name matching localhost found
        messageSender.setHostnameVerifier((hostname, sslSession) -> {
            if (hostname.equals("localhost")) {
                return true;
            }
            return false;
        });

//        impl.setMessageSender(messageSender);
        return impl;
    }
    
    
}
