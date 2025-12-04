package edu.wisc.my.messages.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class MessageFilterTest {

  @Test
  public void matchesWhenUserIsInAMatchingGroup() {
    MessageFilter filter = new MessageFilter();
    filter.addGroupsItem("matchingGroup");
    filter.addGroupsItem("someOtherGroup");

    final User user = new User();
    Set<String> groups = new HashSet<>();
    groups.add("matchingGroup");
    groups.add("unrelatedGroup");
    user.setGroups(groups);

    assertTrue(filter.test(user));
  }

  @Test
  public void doesNotMatchWhenUserIsInNoMatchingGroup() {
    MessageFilter filter = new MessageFilter();
    filter.addGroupsItem("someGroup");
    filter.addGroupsItem("someOtherGroup");

    final User user = new User();
    Set<String> groups = new HashSet<>();
    groups.add("notMatchingGroup");
    groups.add("unrelatedGroup");
    user.setGroups(groups);

    assertFalse(filter.test(user));
  }

  @Test
  public void stringRepresentationContainsGoLiveDate() {
    MessageFilter filter = new MessageFilter();
    filter.setGoLiveDate("2018-02-24");
    assertTrue(filter.toString().contains("2018-02-24"));
  }

  @Test
  public void stringRepresentationContainsExpireDate() {
    MessageFilter filter = new MessageFilter();
    filter.setExpireDate("2018-02-24");
    assertTrue(filter.toString().contains("2018-02-24"));
  }

  @Test
  public void stringRepresentationContainsGroups() {
    MessageFilter filter = new MessageFilter();
    filter.addGroupsItem("somegroup");
    filter.addGroupsItem("some-other-group");

    assertTrue(filter.toString().contains("somegroup"));
    assertTrue(filter.toString().contains("some-other-group"));
  }

  @Test
  public void filtersWithSameGroupsAndDatesAreEqual() {

    List<String> groupsListOne = new ArrayList<String>();
    groupsListOne.add("some-group");
    groupsListOne.add("some-other-group");

    List<String> groupsListTwo = new ArrayList<String>();
    groupsListTwo.addAll(groupsListOne);

    MessageFilter filterOne = new MessageFilter();
    filterOne.setGoLiveDate("2000-01-01");
    filterOne.setExpireDate("2030-01-01");
    filterOne.setGroups(groupsListOne);

    MessageFilter filterTwo = new MessageFilter();
    filterTwo.setGoLiveDate("2000-01-01");
    filterTwo.setExpireDate("2030-01-01");
    filterTwo.setGroups(groupsListOne);

    assertEquals(filterOne.hashCode(), filterTwo.hashCode());
    assertEquals(filterOne, filterTwo);
  }

  @Test
  public void filtersWithDifferentGroupsAreNotEqual() {

    List<String> groupsListOne = new ArrayList<String>();
    groupsListOne.add("some-group");
    groupsListOne.add("some-other-group");

    List<String> groupsListTwo = new ArrayList<String>();
    groupsListTwo.add("a-different-group");
    groupsListTwo.add("never-seen-this-group-before");

    MessageFilter filterOne = new MessageFilter();
    filterOne.setGoLiveDate("2000-01-01");
    filterOne.setExpireDate("2030-01-01");
    filterOne.setGroups(groupsListOne);

    MessageFilter filterTwo = new MessageFilter();
    filterTwo.setGoLiveDate("2000-01-01");
    filterTwo.setExpireDate("2030-01-01");
    filterTwo.setGroups(groupsListTwo);

    assertNotEquals(filterOne, filterTwo);
  }

  @Test
  public void filtersWithDifferentGoLiveDatesAreNotEqual() {

    List<String> groupsListOne = new ArrayList<String>();
    groupsListOne.add("some-group");
    groupsListOne.add("some-other-group");

    List<String> groupsListTwo = new ArrayList<String>();
    groupsListTwo.addAll(groupsListOne);

    MessageFilter filterOne = new MessageFilter();
    filterOne.setGoLiveDate("2000-01-01");
    filterOne.setExpireDate("2030-01-01");
    filterOne.setGroups(groupsListOne);

    MessageFilter filterTwo = new MessageFilter();
    filterTwo.setGoLiveDate("2002-02-02");
    filterTwo.setExpireDate("2030-01-01");
    filterTwo.setGroups(groupsListOne);

    assertNotEquals(filterOne, filterTwo);
  }

  @Test
  public void filtersWithDifferentExpireDatesAreNotEqual() {

    List<String> groupsListOne = new ArrayList<String>();
    groupsListOne.add("some-group");
    groupsListOne.add("some-other-group");

    List<String> groupsListTwo = new ArrayList<String>();
    groupsListTwo.addAll(groupsListOne);

    MessageFilter filterOne = new MessageFilter();
    filterOne.setGoLiveDate("2000-01-01");
    filterOne.setExpireDate("2030-01-01");
    filterOne.setGroups(groupsListOne);

    MessageFilter filterTwo = new MessageFilter();
    filterTwo.setGoLiveDate("2000-01-01");
    filterTwo.setExpireDate("2044-04-01");
    filterTwo.setGroups(groupsListOne);

    assertNotEquals(filterOne, filterTwo);
  }

}
