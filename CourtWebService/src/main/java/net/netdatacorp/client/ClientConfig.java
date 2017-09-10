package net.netdatacorp.client;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.netdatacorp.services.courtrecord.CourtRecordPortType;

@Configuration
public class ClientConfig {

  @Value("${client.ticketagent.address}")
  private String address;

  @Bean(name = "helloWorldProxy")
  public CourtRecordPortType helloWorldProxy() {
    JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
    jaxWsProxyFactoryBean.setServiceClass(CourtRecordPortType.class);
    jaxWsProxyFactoryBean.setAddress(address);

    return (CourtRecordPortType) jaxWsProxyFactoryBean.create();
  }
}
