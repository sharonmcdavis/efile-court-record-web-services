package net.netdatacorp.endpoint;

import net.netdatacorp.services.courtrecord.CourtRecordPortType;
import net.netdatacorp.types.courtrecord.Greeting;
import net.netdatacorp.types.courtrecord.ObjectFactory;
import net.netdatacorp.types.courtrecord.Person;

public class CourtRecordImpl implements CourtRecordPortType {

  @Override
  public Greeting sayHello(Person request) {
    String greeting = "Hello " + request.getFirstName() + " " + request.getLastName() + "!";

    ObjectFactory factory = new ObjectFactory();
    Greeting response = factory.createGreeting();
    response.setGreeting(greeting);

    return response;
  }

@Override
public int sum(int add1, int add2) {
	System.out.println("\n\nin the sum method!");
	return add1 + add2;
}

@Override
public int multiply(int mul1, int mul2) {
	System.out.println("\n\nin the multiply method!");
	return mul1 * mul2;
}
}
