package edu.wisc.my.messages.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ActionButtonTest {

  @Test
  public void actionButtonsCanBeCreatedElegantly() {

    ActionButton button = new ActionButton().label("Visit Apereo").url("http://www.apereo.org");

    assertEquals("Visit Apereo", button.getLabel());
    assertEquals("http://www.apereo.org", button.getUrl());
  }

  @Test
  public void stringRepresentationContainsUrl() {
    ActionButton button = new ActionButton().label("Visit MyUW").url("https://my.wisc.edu");

    assertTrue(button.toString().contains("https://my.wisc.edu"));
  }

  @Test
  public void stringRepresentationContainsLabel() {
    ActionButton button = new ActionButton().label("Visit MyUW").url("https:/my.wisc.edu");

    assertTrue(button.toString().contains("Visit MyUW"));
  }

  @Test
  public void differentLabelsYieldDifferentHashCodes() {
    ActionButton button1 = new ActionButton().label("Visit MyuW").url("https://my.wisc.edu");
    ActionButton button2 = new ActionButton().label("Launch MyuW").url("https://my.wisc.edu");

    assertNotEquals(button1.hashCode(), button2.hashCode());
  }

  @Test
  public void buttonsWithSameLabelAndUrlAreEqual() {
    ActionButton button1 = new ActionButton().label("Visit MyuW").url("https://my.wisc.edu");
    ActionButton button2 = new ActionButton().label("Visit MyuW").url("https://my.wisc.edu");

    assertEquals(button1.hashCode(), button2.hashCode());
    assertEquals(button1, button2);
  }

}
