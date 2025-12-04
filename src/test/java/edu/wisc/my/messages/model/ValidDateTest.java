package edu.wisc.my.messages.model;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;


public class ValidDateTest {

  @Test
  public void filterOutNotYetLiveDates() {
    Message message = new Message();
    MessageFilter filter = new MessageFilter();
    message.setFilter(filter);
    String futureDate = "2100-12-31";
    filter.setGoLiveDate(futureDate);
    assertFalse(message.isValidToday(),
      "Future go-live date without time specified should invalidate message.");
  }
/*
  @Test
  public void pastGoLiveDatesYieldValidMessages() {
    Message message = new Message();
    String pastDate = "2000-04-12T12:21:21";
    MessageFilter filter = new MessageFilter();
    message.setFilter(filter);
    filter.setGoLiveDate(pastDate);
    assertTrue("Messages with past go-live date-times with time specified should be valid.",
      filter.isValidToday());
  }

  @Test
  public void pastGoLiveDatesCanOmitTime() {
    Message message = new Message();
    String pastDate = "2000-04-12";
    message.setGoLiveDate(pastDate);
    assertTrue("Past go-live date omitting time should not invalidate message.",
      message.isValidToday());
  }

  @Test
  public void filterOutImproperDates() {
    Message message = new Message();
    String nonsenseDate = "Not a date";
    message.setGoLiveDate(nonsenseDate);
    assertFalse("Unparseable date strings should invalidate a message (fail closed).",
      message.isValidToday());
  }

  @Test
  public void nullDatesPass() {
    Message message = new Message();
    message.setExpireDate(null);
    message.setGoLiveDate(null);
    assertTrue("Null go live or expire dates should not invalidate message.",
      message.isValidToday());
  }

  @Test
  public void ignoresEmptyStringDates() {
    Message message = new Message();
    message.setExpireDate("");
    message.setGoLiveDate("");
    assertTrue("Empty date strings shouldn't invalidate a message",
      message.isValidToday());
  }

  /**
   * Test that messages that expire later today are not considered expired. That is, that expiration
   * supports the THH:MM suffix on ISO date-times.
  @Test
  public void expiringLaterTodayIsNotExpired() throws InterruptedException {

    // assumption: this test will not take more than 2 seconds.

    // this test relies on two seconds from now being in the same date, so that an implementation
    // that respects time will notice that the that timestamp is not yet expired whereas an
    // implementation that only respects dates will consider that timestamp expired.
    //
    // therefore edge case: it is less than four seconds before the date rolls over
    // in this case wait four seconds to escape the edge case

    LocalDateTime now = LocalDateTime.now();

    if (now.getHour() == 23 && now.getSecond() > 56) {
      wait(4000);
    }

    // two seconds from now is within the current date, either because not in the edge case
    // or because waited for the edge case to pass

    LocalDateTime twoSecondsLaterThanNow = LocalDateTime.now().plusSeconds(2);

    Message message = new Message();
    // this works because LocalDateTime toString() --> ISO date-time format.
    message.setExpireDate(twoSecondsLaterThanNow.toString());
    message.setGoLiveDate("1900-01-01T03:00");
    assertTrue("Message considered expired prematurely.",
      message.isValidToday());
  }

  @Test
  public void goingLiveLaterTodayIsNotLive() throws InterruptedException {

    // assumption: this test will not take more than 2 seconds.

    // this test relies on two seconds from now being in the same date, so that an implementation
    // that respects time will notice that the that timestamp is not yet expired whereas an
    // implementation that only respects dates will consider that timestamp expired.
    //
    // therefore edge case: it is less than four seconds before the date rolls over
    // in this case wait four seconds to escape the edge case

    LocalDateTime now = LocalDateTime.now();

    if (now.getHour() == 23 && now.getSecond() > 56) {
      wait(4000);
    }

    // two seconds from now is within the current date, either because not in the edge case
    // or because waited for the edge case to pass

    LocalDateTime twoSecondsLaterThanNow = LocalDateTime.now().plusSeconds(2);

    Message message = new Message();
    message.setGoLiveDate(twoSecondsLaterThanNow.toString());
    assertFalse("Message considered gone live prematurely.",
      message.isValidToday());

  }
  */
}
