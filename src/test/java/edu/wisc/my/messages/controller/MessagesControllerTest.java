package edu.wisc.my.messages.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.wisc.my.messages.data.MessagesFromTextFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class MessagesControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private MessagesFromTextFile messageReader;

  @Test
  public void siteIsUp() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith("application/json"))
      .andExpect(content().json("{\"status\":\"up\"}"));
  }

  /**
   * Test that the autowired MessageReader successfully reads messages. This is an essential
   * building block towards richer tests of the application-as-running.
   */
  @Test
  public void dataIsValid() {
    this.messageReader.allMessages();
  }


  /**
   * Test the /admin/message/{id} path reading a message.
   *
   * @throws Exception as an unexpected test failure modality
   */
  @Test
  public void adminMessageById() throws Exception {
    String expectedJson = """
      {
        "id": "demo-high-priority-valid-group-no-date",
        "title": "Valid group. No date. High priority.",
        "titleShort": "Valid group. No date. High priority.",
        "titleUrl": null,
        "description": "Valid group. No date. High priority.",
        "descriptionShort": "Valid group. No date. High priority.",
        "messageType": "notification",
        "featureImageUrl": null,
        "priority": "high",
        "recurrence": null,
        "dismissible": null,
        "filter": {
          "goLiveDate": null,
          "expireDate": null,
          "groups": [
            "Portal Administrators"
          ]
        },
        "data": {
          "dataUrl": null,
          "dataObject": null,
          "dataArrayFilter": null,
          "dataMessageTitle": null,
          "dataMessageMoreInfoUrl": null
        },
        "actionButton": {
          "label": "Go",
          "url": "http://www.google.com"
        },
        "moreInfoButton": null,
        "confirmButton": null
      }\
      """;

    mvc.perform(MockMvcRequestBuilders.get("/admin/message/demo-high-priority-valid-group-no-date")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith("application/json"))
      .andExpect(content().json(expectedJson));
  }

  /**
   * Test that looking for a message by an ID that does not match yields a 404 NOT FOUND.
   */
  @Test
  public void adminNotFoundMessageYields404() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/admin/message/no-such-message")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  /**
   * Test the /admin/message/{id} path reading a message.
   *
   * @throws Exception as an unexpected test failure modality
   */
  @Test
  public void messageById() throws Exception {
    String expectedJson = """
      {
        "id": "has-no-audience-filter",
        "title": "An announcement lacking an audience filter.",
        "titleShort": "Not filtered by audience",
        "titleUrl": null,
        "description": "This announcement is not filtered by groups.",
        "descriptionShort": "Not filtered by groups.",
        "messageType": "announcement",
        "featureImageUrl": null,
        "priority": null,
        "recurrence": null,
        "dismissible": null,
        "filter": null,
        "data": {
          "dataUrl": null,
          "dataObject": null,
          "dataArrayFilter": null,
          "dataMessageTitle": null,
          "dataMessageMoreInfoUrl": null
        },
        "actionButton": {
          "label": "Add to home",
          "url": "addToHome/open-apereo"
        },
        "moreInfoButton": {
          "label": "More info",
          "url": "https://www.apereo.org/content/2018-open-apereo-montreal-quebec"
        },
        "confirmButton": null
      }\
      """;

    mvc.perform(MockMvcRequestBuilders.get("/message/has-no-audience-filter")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith("application/json"))
      .andExpect(content().json(expectedJson));
  }

  /**
   * Attempting to get a message you are not in the audience of yields 403 FORBIDDEN.
   */
  @Test
  public void notInAudienceMessageByIdYieldsError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/message/1")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden());
  }

  /**
   * Attempting to get an expired message yields 403 FORBIDDEN.
   */
  @Test
  public void expiredMessageByIdYieldsError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/message/expired")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden());
  }

  /**
   * Attempting to get premature message yields 403 FORBIDDEN.
   */
  @Test
  public void prematureMessageByIdYieldsError() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/message/premature")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden());
  }

  /**
   * Test that looking for a message by an ID that does not match yields a 404 NOT FOUND.
   */
  @Test
  public void notFoundMessageYields404() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/admin/message/no-such-message")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }
}
