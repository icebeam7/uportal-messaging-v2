package edu.wisc.my.messages.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.URL;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessagesControllerIT {

  @LocalServerPort
  private int port;

  private URL base;

  @Autowired
  private TestRestTemplate template;

  @BeforeEach
  public void setUp() throws Exception {
    this.base = URI.create("http://localhost:" + port + "/").toURL();
  }

  @Test
  public void siteIsUp() throws Exception {
    ResponseEntity<String> response = template.getForEntity(base.toString(),
      String.class);
    assertThat(response.getBody(), StringContains.containsString("status"));
  }

  @Test
  public void nonexistentPathYields404() throws Exception {
    ResponseEntity<String> response =
      template.getForEntity(base.toString() + "someGoofyPath", String.class);
    assertEquals(404, response.getStatusCodeValue(), "Missing path should yield 404 not found response.");
  }
}
