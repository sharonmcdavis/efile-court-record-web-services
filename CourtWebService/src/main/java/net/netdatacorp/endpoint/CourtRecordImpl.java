package net.netdatacorp.endpoint;

import javax.jws.HandlerChain;

import net.netdatacorp.beans.InputClass;
import net.netdatacorp.beans.ResponseClass;
import net.netdatacorp.services.courtrecord.CourtRecordPortType;
import net.netdatacorp.types.courtrecord.Greeting;
import net.netdatacorp.types.courtrecord.ObjectFactory;
import net.netdatacorp.types.courtrecord.Person;

@HandlerChain(file = "/net/netdatacorp/services/courtrecord/handlers.xml")
public class CourtRecordImpl implements CourtRecordPortType {

  @Override
  public Greeting sayHello(Person request) {
    String greeting = "Hello " + request.getFirstName() + " " + request.getLastName() + "!";

    ObjectFactory factory = new ObjectFactory();
    Greeting response = factory.createGreeting();
    response.setGreeting(greeting);

    return response;
  }

	public ResponseClass CreateCase(InputClass inputData) {
		System.out.println("in the create case method");
		return null;
	}
}
