package edu.wisc.my.messages.time;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class IsoDateTimeStringAfterPredicateTest {

  IsoDateTimeStringAfterPredicate afterNow =
    new IsoDateTimeStringAfterPredicate(LocalDateTime.now());

  /**
   * Test that null is not after now.
   */
  @Test
  public void nullIsNotAfter() {
    assertFalse(afterNow.test(null));
  }

  /**
   * Test that "" is not after now.
   */
  @Test
  public void emptyIsNotAfter() {
    assertFalse(afterNow.test(""));
  }

  /**
   * Test that whitespace is not after now.
   */
  @Test
  public void whitespaceIsNotAfter() {
    assertFalse(afterNow.test("\t     \t"));
  }

  /**
   * Test that input that cannot be parsed results in RuntimeException.
   */
  @Test
  public void garbageThrows() {
    assertThrows(RuntimeException.class, () ->
      afterNow.test("Garbage"));
  }

  @Test
  public void uwWasNotIncorporatedAfterNow() {
    assertFalse(afterNow.test("1848-07-26"));
  }

  @Test
  public void uwWasNotIncorporatedAfterNowWhenTimeSpecified() {
    assertFalse(afterNow.test("1848-07-26T13:45:22"));
  }

  @Test
  public void uwTercentenialIsAfterNow() {
    assertTrue(afterNow.test("2148-07-26"));
  }

  @Test
  public void uwTercentenialIsAfterNowWhenTimeSpecified() {
    assertTrue(afterNow.test("2148-07-26T04:17:32"));
  }

  @Test
  public void elevensiesIsAfterBreakfastDuringAGivenDay() {

    LocalDateTime breakfastTime = LocalDateTime.parse("2010-01-01T07:00:00");
    String elevensies = "2010-01-01T11:00:00";

    IsoDateTimeStringAfterPredicate afterBreakfast =
      new IsoDateTimeStringAfterPredicate(breakfastTime);

    assertTrue(afterBreakfast.test(elevensies));
  }

  @Test
  public void breakfastIsNotAfterElevensies() {

    LocalDateTime elevensies = LocalDateTime.parse("2010-01-01T11:00:00");
    String breakfastTime = "2010-01-01T07:00:00";

    IsoDateTimeStringAfterPredicate afterElevensies =
      new IsoDateTimeStringAfterPredicate(elevensies);

    assertFalse(afterElevensies.test(breakfastTime));
  }

}
