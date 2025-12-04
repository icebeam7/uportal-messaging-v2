package edu.wisc.my.messages.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  public void cannotSetGroupsToNull() {
    assertThrows(NullPointerException.class, () -> {
      User user = new User();
      user.setGroups(null);
    });
  }

}
