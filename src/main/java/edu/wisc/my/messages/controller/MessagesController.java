package edu.wisc.my.messages.controller;

import edu.wisc.my.messages.exception.ExpiredMessageException;
import edu.wisc.my.messages.exception.MessageNotFoundException;
import edu.wisc.my.messages.exception.PrematureMessageException;
import edu.wisc.my.messages.exception.UserNotInMessageAudienceException;
import edu.wisc.my.messages.model.Message;
import edu.wisc.my.messages.model.User;
import edu.wisc.my.messages.service.MessagesService;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Understands what HTTP requests are asking about messages, queries the MessagesService
 * accordingly, and replies in JSON.
 */
@RestController
public class MessagesController {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private MessagesService messagesService;
  private IsMemberOfHeaderParser isMemberOfHeaderParser;

  /**
   * Messages filtered to the context of the request. <p> Expects user group memberships as
   * isMemberOf header with semicolon-delimited values. Fails gracefully in absence of this header.
   * <p> The details of the filtering are NOT a semantically versioned aspect of this API. That is,
   * the microservice can get more sophisticated at filtering and this will be considered a MINOR
   * rather than MAJOR (breaking) change. <p> As currently implemented, EXCLUDES messages that are
   * ANY of <p> <ul> <li>premature per not-before metadata on the message</li> <li>expired per
   * not-after metadata on the message</li> <li>limited to groups none of which include the
   * requesting user</li> </ul>
   */
  @GetMapping("/messages")
  public Map<String, Object> messages(HttpServletRequest request) {

    String isMemberOfHeader = request.getHeader("isMemberOf");
    Set<String> groups =
      isMemberOfHeaderParser.groupsFromHeaderValue(isMemberOfHeader);
    User user = new User();
    user.setGroups(groups);

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("messages", messagesService.filteredMessages(user));

    return responseMap;
  }

  /**
   * Get all the messages in the system, regardless of audience, dates, etc.
   *
   * @return Map where key "messages" has value List of all Messages.
   */
  @GetMapping("/admin/allMessages")
  public Map<String, Object> allMessages() {
    Map<String, Object> responseMap = new HashMap<String, Object>();
    responseMap.put("messages", messagesService.allMessages());

    return responseMap;
  }

  @RequestMapping("/")
  public Map<String, String> index() {
    HashMap<String, String> statusResponse = new HashMap<>();
    statusResponse.put("status", "up");
    return statusResponse;
  }

  /**
   * Get a specific message regardless of the message's audience, dates, etc.
   *
   * @param id message ID to match
   * @return Message with matching ID
   */
  @RequestMapping("/admin/message/{id}")
  public Message adminMessageById(@PathVariable String id) throws MessageNotFoundException {

    Message message = messagesService.messageById(id);

    if (null == message) {
      throw new MessageNotFoundException();
    }
    return message;
  }

  /**
   * Get a specific message, limited by the requesting user's context.
   *
   * @throws PrematureMessageException if the message is not yet gone live
   * @throws ExpiredMessageException if the message is expired
   * @throws UserNotInMessageAudienceException if the requesting user is not in the audience
   * @returns the requested message, or null if none matching
   */
  @RequestMapping("/message/{id}")
  public Message messageById(@PathVariable String id, HttpServletRequest request)
    throws UserNotInMessageAudienceException, PrematureMessageException, ExpiredMessageException, MessageNotFoundException {

    String isMemberOfHeader = request.getHeader("isMemberOf");
    Set<String> groups =
      isMemberOfHeaderParser.groupsFromHeaderValue(isMemberOfHeader);
    User user = new User();
    user.setGroups(groups);

    Message message = messagesService.messageByIdForUser(id, user);

    if (null == message) {
      throw new MessageNotFoundException();
    }

    return message;
  }

  @Autowired
  public void setMessagesService(MessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Autowired
  public void setIsMemberOfHeaderParser(
    IsMemberOfHeaderParser isMemberOfHeaderParser) {
    this.isMemberOfHeaderParser = isMemberOfHeaderParser;
  }
}
