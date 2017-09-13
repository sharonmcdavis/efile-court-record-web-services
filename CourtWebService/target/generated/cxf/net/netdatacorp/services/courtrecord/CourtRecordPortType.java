package net.netdatacorp.services.courtrecord;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.1.12
 * 2017-09-13T03:35:48.166-05:00
 * Generated source version: 3.1.12
 * 
 */
@WebService(targetNamespace = "http://netdatacorp.net/services/courtrecord", name = "CourtRecord_PortType")
@XmlSeeAlso({net.netdatacorp.types.courtrecord.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface CourtRecordPortType {

    @WebMethod(action = "http://netdatacorp.net/services/courtrecord/sayHello")
    @WebResult(name = "greeting", targetNamespace = "http://netdatacorp.net/types/courtrecord", partName = "greeting")
    public net.netdatacorp.types.courtrecord.Greeting sayHello(
        @WebParam(partName = "person", name = "person", targetNamespace = "http://netdatacorp.net/types/courtrecord")
        net.netdatacorp.types.courtrecord.Person person
    );
}
