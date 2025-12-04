package edu.wisc.my.messages.time;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class IsoDateTimeStringBeforePredicateTest {

  private IsoDateTimeStringBeforePredicate beforeNowPredicate =
    new IsoDateTimeStringBeforePredicate(LocalDateTime.now());

  @Test
  public void nullEvaluatesFalse() {
    assertFalse(beforeNowPredicate.test(null), "Null shouldn't be considered before now.");
  }

  @Test
  public void emptyStringEvaluatesFalse() {
    assertFalse(beforeNowPredicate.test(""), "Empty string shouldn't be considered before now.");
  }

  @Test
  public void whitespaceStringEvaluatesFalse() {
    assertFalse(beforeNowPredicate.test("\t   \t"),
      "Whitespace shouldn't be considered before now.");
  }

  @Test
  public void stringThatIsNotADateThrows() {
    assertThrows(RuntimeException.class, () ->
      beforeNowPredicate.test("Garbage"));
  }

  @Test
  public void uwWasIncorporatedBeforeNow() {
    assertTrue(beforeNowPredicate.test("1848-07-26"),
      "July 26 1848 should be before now.");
  }

  /**
   * Test that even if the time of incorporation is specified to the second, it's still before now.
   */
  @Test
  public void uwWasIncorporatedBeforeNowWhenTimeSpecific() {
    assertTrue(beforeNowPredicate.test("1848-07-26T13:01:04"),
      "July 26 1848 around 1p should be before now.");
  }

  @Test
  public void uwTercentenialIsNotBeforeNow() {
    assertFalse(beforeNowPredicate.test("2148-07-26"),
      "July 26 2148 should not be before now.");
  }

  /**
   * Test that even if the time of the tercentenial is specified to the second, it's still not
   * before now.
   */
  @Test
  public void uwTercentenialIsNotBeforeNowWhenTimeSpecific() {
    assertFalse(beforeNowPredicate.test("2148-07-26T09:05:32"),
      "July 26 2148 around 9a should not be before now.");
  }

  /**
   * Test that the predicate actually considers the time component of the date-time.
   */
  @Test
  public void breakfastIsBeforeElevensies() {

    LocalDateTime elevensies = LocalDateTime.parse("2000-01-01T11:00:00");
    String breakfastTime = "2000-01-01T07:00:00";

    IsoDateTimeStringBeforePredicate beforeElevensies =
      new IsoDateTimeStringBeforePredicate(elevensies);

    assertTrue(beforeElevensies.test(breakfastTime));
  }

  /**
   * TEst that the predicate actually considers the time component of the date-time.
   */
  @Test
  public void elevensiesIsNotBeforeBreakfast() {

    LocalDateTime breakfastTime = LocalDateTime.parse("2000-01-01T07:00:00");
    String elevensies = "2000-01-01T11:00:00";

    IsoDateTimeStringBeforePredicate beforeBreakfast =
      new IsoDateTimeStringBeforePredicate(breakfastTime);

    assertFalse(beforeBreakfast.test(elevensies));
  }

}
