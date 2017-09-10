package net.netdatacorp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import net.netdatacorp.client.CourtRecordClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApplicationTests {

  @Autowired
  private CourtRecordClient courtRecordClient;

  @Test
  public void testSayHello() {
    assertThat(courtRecordClient.sayHello("John", "Doe")).isEqualTo("Hello John Doe!");
  }
}
