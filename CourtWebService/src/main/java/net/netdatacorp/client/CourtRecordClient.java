package net.netdatacorp.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.netdatacorp.services.courtrecord.CourtRecordPortType;
import net.netdatacorp.types.courtrecord.Greeting;
import net.netdatacorp.types.courtrecord.ObjectFactory;
import net.netdatacorp.types.courtrecord.Person;

@Component
public class CourtRecordClient {

  @Autowired
  private CourtRecordPortType courtRecordProxy;

  public String sayHello(String firstName, String lastName) {

    ObjectFactory factory = new ObjectFactory();
    Person person = factory.createPerson();
    person.setFirstName(firstName);
    person.setLastName(lastName);

    Greeting response = courtRecordProxy.sayHello(person);

    return response.getGreeting();
  }
}
