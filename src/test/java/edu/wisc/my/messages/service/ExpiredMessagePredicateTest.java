package edu.wisc.my.messages.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wisc.my.messages.model.Message;
import edu.wisc.my.messages.model.MessageFilter;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class ExpiredMessagePredicateTest {

  ExpiredMessagePredicate predicate = new ExpiredMessagePredicate(LocalDateTime.now());

  @Test
  public void throwsOnNull() {
    assertThrows(RuntimeException.class, () ->
      predicate.test(null));
  }

  @Test
  public void messageWithGarbageExpirationDateIsConsideredExpired() {
    Message brokenMessage = new Message();
    MessageFilter messageFilter = new MessageFilter();
    brokenMessage.setFilter(messageFilter);
    messageFilter.setExpireDate("Garbage");
    assertTrue(predicate.test(brokenMessage));
  }

  @Test
  public void expiredMessageIsExpired() {
    Message expiredMessage = new Message();
    MessageFilter messageFilter = new MessageFilter();
    expiredMessage.setFilter(messageFilter);
    messageFilter.setExpireDate("2010-01-01");
    assertTrue(predicate.test(expiredMessage));
  }

  @Test
  public void preciselyExpiredMessageIsExpired() {
    Message preciselyExpiredMessage = new Message();
    MessageFilter messageFilter = new MessageFilter();
    preciselyExpiredMessage.setFilter(messageFilter);
    messageFilter.setExpireDate("2001-04-12T17:35:24");
    assertTrue(predicate.test(preciselyExpiredMessage));
  }

  @Test
  public void notYetExpiredMessageIsNotExpired() {
    ExpiredMessagePredicate expiredAsOfMillenium =
      new ExpiredMessagePredicate(LocalDateTime.parse("2000-01-01T00:00:00"));

    Message expiresAfterMillenium = new Message();
    MessageFilter messageFilter = new MessageFilter();
    expiresAfterMillenium.setFilter(messageFilter);
    messageFilter.setExpireDate("2012-11-29");

    assertFalse(expiredAsOfMillenium.test(expiresAfterMillenium));
  }

  @Test
  public void preciselyNotYetExpiredMessageIsNotExpired() {
    ExpiredMessagePredicate expiredAsOfMillenium =
      new ExpiredMessagePredicate(LocalDateTime.parse("2000-01-01T00:00:00"));

    Message preciselyExpiresAfterMillenium = new Message();
    MessageFilter messageFilter = new MessageFilter();
    preciselyExpiresAfterMillenium.setFilter(messageFilter);
    messageFilter.setExpireDate("2015-12-22T09:00:00");

    assertFalse(expiredAsOfMillenium.test(preciselyExpiresAfterMillenium));
  }

  @Test
  public void messageWithNoFilterIsNotExpired() {
    ExpiredMessagePredicate expiredAsOfMillenium =
      new ExpiredMessagePredicate(LocalDateTime.parse("2000-01-01T00:00:00"));

    Message messageWithNoFilter = new Message();

    assertFalse(expiredAsOfMillenium.test(messageWithNoFilter));
  }

}
