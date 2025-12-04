package edu.wisc.my.messages.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class IsMemberOfHeaderParserTest {

  @Test
  public void parsesNullAsEmptySet() {
    assertEquals(Collections.EMPTY_SET, new IsMemberOfHeaderParser().groupsFromHeaderValue(null));
  }

  @Test
  public void parsesSingleGroupValue() {
    String headerValue = "uw:domain:tools.advising.wisc.edu:advising_gateway_access";
    Set<String> expected = new HashSet<>();
    expected.add(headerValue);

    assertEquals(expected, new IsMemberOfHeaderParser().groupsFromHeaderValue(headerValue));
  }

  @Test
  public void parsesSemicolonTerminateSingleGroupValue() {
    String headerValue = "uw:domain:tools.advising.wisc.edu:advising_gateway_access;";
    Set<String> expected = new HashSet<>();
    expected.add("uw:domain:tools.advising.wisc.edu:advising_gateway_access");

    assertEquals(expected, new IsMemberOfHeaderParser().groupsFromHeaderValue(headerValue));
  }

  @Test
  public void parsesManyValuedHeader() {
    String headerValue = "uw:domain:my.wisc.edu:my_uw_administrators;" +
      "uw:domain:ohr.wisc.edu:trems;" +
      "uw:domain:ADI_STAR_TimeEntry:star_users;" +
      "uw:domain:office365.wisc.edu:license_change_2016;" +
      "uw:domain:ohr.wisc.edu:myuw pmdp;" +
      "uw:domain:registrar.wisc.edu:emergency_info_admin;" +
      "uw:domain:tools.advising.wisc.edu:advising_gateway_access;" +
      "uw:domain:apps.mumaa.doit.wisc.edu:lumen_access;" +
      "uw:domain:my.wisc.edu:my_uw_hr_officers";

    Set<String> expected = new HashSet<>();
    expected.add("uw:domain:my.wisc.edu:my_uw_administrators");
    expected.add("uw:domain:ohr.wisc.edu:trems");
    expected.add("uw:domain:ADI_STAR_TimeEntry:star_users");
    expected.add("uw:domain:office365.wisc.edu:license_change_2016");
    expected.add("uw:domain:ohr.wisc.edu:myuw pmdp");
    expected.add("uw:domain:registrar.wisc.edu:emergency_info_admin");
    expected.add("uw:domain:tools.advising.wisc.edu:advising_gateway_access");
    expected.add("uw:domain:apps.mumaa.doit.wisc.edu:lumen_access");
    expected.add("uw:domain:my.wisc.edu:my_uw_hr_officers");

    assertEquals(expected, new IsMemberOfHeaderParser().groupsFromHeaderValue(headerValue));
  }

}
