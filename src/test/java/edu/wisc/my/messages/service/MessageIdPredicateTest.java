package edu.wisc.my.messages.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wisc.my.messages.model.Message;
import org.junit.jupiter.api.Test;

public class MessageIdPredicateTest {

  @Test
  public void nullMessageTestsFalse() {
    MessageIdPredicate predicate = new MessageIdPredicate("does-not-matter");
    assertFalse(predicate.test(null));
  }

  @Test
  public void mismatchIdTestsFalse() {
    MessageIdPredicate predicate = new MessageIdPredicate("a-particular-id");
    Message message = new Message();
    message.setId("not-that-particular-id");

    assertFalse(predicate.test(message));
  }

  @Test
  public void matchingIdTestsTrue() {
    MessageIdPredicate predicate = new MessageIdPredicate("a-particular-id");
    Message message = new Message();
    message.setId("a-particular-id");

    assertTrue(predicate.test(message));
  }

  @Test
  public void matchingNullIdTestsTrue() {
    MessageIdPredicate predicate = new MessageIdPredicate(null);
    Message messageWithNullId = new Message();

    assertTrue(predicate.test(messageWithNullId));
  }


  /**
   * Test that testing a message with a non-null id for a null message ID evaluates correctly false
   * and does not NullPointer out.
   */
  @Test
  public void notMatchingPredicateLookingForNullTestsFalse() {
    MessageIdPredicate messageHasNullIdPredicate = new MessageIdPredicate(null);
    Message messageWithNonNullId = new Message();
    messageWithNonNullId.setId("not-null-id");

    assertFalse(messageHasNullIdPredicate.test(messageWithNonNullId));
  }

  /**
   * Test that testing a message with a null id for a non-null message ID evaluates correctly false
   * and does not NullPointer out.
   */
  @Test
  public void nullMessageIdNotMatchingPredicateLookingForNonNullTestsFalse() {
    MessageIdPredicate someMessageIdPredicate = new MessageIdPredicate("some-id");
    Message messageWithNullId = new Message();

    assertFalse(someMessageIdPredicate.test(messageWithNullId));
  }

}
