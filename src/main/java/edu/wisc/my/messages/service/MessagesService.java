package edu.wisc.my.messages.service;

import edu.wisc.my.messages.data.MessagesFromTextFile;
import edu.wisc.my.messages.exception.ExpiredMessageException;
import edu.wisc.my.messages.exception.PrematureMessageException;
import edu.wisc.my.messages.exception.UserNotInMessageAudienceException;
import edu.wisc.my.messages.model.Message;
import edu.wisc.my.messages.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagesService {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private MessagesFromTextFile messageSource;

  public List<Message> allMessages() {
    return messageSource.allMessages();
  }

  public List<Message> filteredMessages(User user) {

    Predicate<Message> neitherPrematureNorExpired =
      new ExpiredMessagePredicate(LocalDateTime.now()).negate()
        .and(new GoneLiveMessagePredicate(LocalDateTime.now()));

    // retain those messages with suitable dates and audiences
    Predicate<Message> retainMessage =
      neitherPrematureNorExpired.and(new AudienceFilterMessagePredicate(user));

    List<Message> validMessages = new ArrayList<>();

    validMessages.addAll(messageSource.allMessages());
    validMessages.removeIf(retainMessage.negate()); // remove the messages we're not retaining

    logger.trace("Found {} messages for user {}.", validMessages.size(), user);

    return validMessages;
  }

  @Autowired
  public void setMessageSource(MessagesFromTextFile messageSource) {
    this.messageSource = messageSource;
  }

  /**
   * Get the message with a given ID, or null if no such message
   *
   * @return Message matching ID, or null if none.
   */
  public Message messageById(String idToMatch) {
    logger.trace("messageById(\"{}\")", idToMatch);
    Validate.notNull(idToMatch);

    Predicate<Message> messageMatchesRequestedId = new MessageIdPredicate(idToMatch);

    List<Message> allMessages = allMessages();

    List<Message> matchingMessages = allMessages.stream().filter(messageMatchesRequestedId)
      .collect(Collectors.toList());

    if (matchingMessages.isEmpty()) {
      logger.debug("Found no message for id [{}]", idToMatch);
      return null;
    } else if (matchingMessages.size() == 1) {
      Message foundMessage = matchingMessages.get(0);
      logger.trace("Found message [{}].", foundMessage);
      return foundMessage;
    } else {
      logger.error("Multiple messages have id [{}]. Messages data corruption?", idToMatch);
      throw new IllegalStateException("Multiple messages matched id [" + idToMatch
        + "], which should have been a unique ID matching at most one message.");
    }
  }

  /**
   * Get the message by id in the context of serving some user.
   *
   * @param id message ID
   * @return the message with the given ID if that message should be given to the requesting user,
   * null if not found
   * @throws RuntimeException if id blank
   * @throws NullPointerException if user null
   * @throws ExpiredMessageException if requested message is expired
   * @throws PrematureMessageException if requested message is not yet live
   * @throws UserNotInMessageAudienceException if the message requires group membership and user
   * lacks a sufficient group membership
   */
  public Message messageByIdForUser(String id, User user)
    throws PrematureMessageException, ExpiredMessageException, UserNotInMessageAudienceException {
    Validate.notBlank(id);
    Validate.notNull(user);

    Message messageWithRequestedId = messageById(id);

    if (null == messageWithRequestedId) {
      logger.debug("No message with id [{}]");
      return null;
    }

    LocalDateTime now = LocalDateTime.now();
    Predicate<Message> prematureMessagePredicate =
      new GoneLiveMessagePredicate(now).negate();
    Predicate<Message> expiredMessagePredicate =
      new ExpiredMessagePredicate(now);
    Predicate<Message> userInAudienceMessagePredicate =
      new AudienceFilterMessagePredicate(user);

    if (prematureMessagePredicate.test(messageWithRequestedId)) {
      logger.debug("There is a message with id [{}] but it is not yet gone live. message: [{}]",
        id, messageWithRequestedId);
      throw new PrematureMessageException(messageWithRequestedId, now);
    } else if (expiredMessagePredicate.test(messageWithRequestedId)) {
      logger.debug("There is a message with id [{}] but it is expired. message: [{}]",
        id, messageWithRequestedId);
      throw new ExpiredMessageException(messageWithRequestedId, now);
    } else if (!userInAudienceMessagePredicate.test(messageWithRequestedId)) {
      logger.debug("There is a message with id [{}] but user [{}] is not in its audience.",
        id, user);
      throw new UserNotInMessageAudienceException(messageWithRequestedId, user);
    }

    logger.trace("Found message with id [{}] as requested by [{}].",
      id, user);
    return messageWithRequestedId;
  }
}
