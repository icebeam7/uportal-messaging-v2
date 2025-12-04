package edu.wisc.my.messages.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.wisc.my.messages.data.MessagesFromTextFile;
import edu.wisc.my.messages.exception.ExpiredMessageException;
import edu.wisc.my.messages.exception.ForbiddenMessageException;
import edu.wisc.my.messages.exception.PrematureMessageException;
import edu.wisc.my.messages.exception.UserNotInMessageAudienceException;
import edu.wisc.my.messages.model.Message;
import edu.wisc.my.messages.model.MessageFilter;
import edu.wisc.my.messages.model.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class MessagesServiceTest {

   private MessagesFromTextFile demoMessageSource() {
     Environment mockEnv = mock(Environment.class);
     when(mockEnv.getProperty(any(String.class))).thenReturn("classpath:demoMessages.json");
     ResourceLoader resourceLoader = new DefaultResourceLoader();
     MessagesFromTextFile messagesFromTextFile = new MessagesFromTextFile();
     messagesFromTextFile.setEnv(mockEnv);
     messagesFromTextFile.setResourceLoader(resourceLoader);
     return messagesFromTextFile;
   }

  @Test
  public void getDemoMessages() {
    MessagesService messagesService = new MessagesService();
    messagesService.setMessageSource(demoMessageSource());
    assertTrue(messagesService.allMessages().size() > 0);
  }
  /**
   * Test that passes along all messages from repository.
   */
  @Test
  public void handsAlongAllMessagesFromRepository() {
    MessagesService service = new MessagesService();

    Message firstMessage = new Message();
    firstMessage.setId("uniqueMessageId-1");

    Message secondMessage = new Message();
    secondMessage.setId("anotherMessageId-2");

    List<Message> messagesFromRepository = new ArrayList<>();
    messagesFromRepository.add(firstMessage);
    messagesFromRepository.add(secondMessage);

    MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
    when(messageSource.allMessages()).thenReturn(messagesFromRepository);

    service.setMessageSource(messageSource);

    List<Message> result = service.allMessages();

    // result should be a message array containing both messages

    assertNotNull(result);

    assertEquals(2, result.size());

    Message firstResultMessage = result.getFirst();
    assertEquals("uniqueMessageId-1", firstResultMessage.getId());

    Message secondResultMessage = result.get(1);
    assertEquals("anotherMessageId-2", secondResultMessage.getId());
  }


  /**
   * Test that filters away messages with AudienceFilters reporting no match.
   */
  @Test
  public void includesOnlyMessagesMatchingAudienceFilters() {
    MessagesService service = new MessagesService();

    MessageFilter yesFilter = mock(MessageFilter.class);
    when(yesFilter.test(any())).thenReturn(true);
    Message matchingMessage = new Message();
    matchingMessage.setFilter(yesFilter);
    matchingMessage.setId("uniqueMessageId");

    MessageFilter noFilter = mock(MessageFilter.class);
    when(noFilter.test(any())).thenReturn(false);
    Message unmatchingMessage = new Message();
    unmatchingMessage.setFilter(noFilter);

    List<Message> unfilteredMessages = new ArrayList<>();
    unfilteredMessages.add(matchingMessage);
    unfilteredMessages.add(unmatchingMessage);

    MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
    when(messageSource.allMessages()).thenReturn(unfilteredMessages);

    service.setMessageSource(messageSource);

    User user = new User();

    List<Message> result = service.filteredMessages(user);

    assertNotNull(result);

    assertEquals(1, result.size());

    Message resultMessage = result.getFirst();

    assertEquals("uniqueMessageId", resultMessage.getId());
  }

  @Test
  public void excludesExpiredMessages() {
    MessagesService service = new MessagesService();

    Message expiredMessage = new Message();
    MessageFilter expiredFilter = new MessageFilter();
    expiredMessage.setFilter(expiredFilter);
    String longAgoDate = "1999-12-31";
    expiredFilter.setExpireDate(longAgoDate);
    expiredMessage.setFilter(expiredFilter);

    Message preciselyExpiredMessage = new Message();
    MessageFilter preciselyExpiredMessageFilter = new MessageFilter();
    String preciseLongAgoDate = "1999-12-31T13:21:14";
    preciselyExpiredMessageFilter.setExpireDate(preciseLongAgoDate);
    preciselyExpiredMessage.setFilter(preciselyExpiredMessageFilter);

    List<Message> unfilteredMessages = new ArrayList<>();
    unfilteredMessages.add(expiredMessage);
    unfilteredMessages.add(preciselyExpiredMessage);

    MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
    when(messageSource.allMessages()).thenReturn(unfilteredMessages);

    service.setMessageSource(messageSource);

    User user = new User();

    List<Message> result = service.filteredMessages(user);

    assertNotNull(result);

    assertTrue(result.isEmpty());
  }

  @Test
  public void includesUnExpiredMessages() {
    MessagesService service = new MessagesService();

    Message unexpiredMessage = new Message();
    MessageFilter unexpiredMessageFilter = new MessageFilter();
    String longFutureDate = "2999-12-31";
    unexpiredMessageFilter.setExpireDate(longFutureDate);
    unexpiredMessage.setFilter(unexpiredMessageFilter);

    Message preciselyUnexpiredMessage = new Message();
    MessageFilter preciselyUnexpiredMessageFilter = new MessageFilter();
    String preciseFutureDate = "2999-12-31T12:21:21";
    preciselyUnexpiredMessageFilter.setExpireDate(preciseFutureDate);
    preciselyUnexpiredMessage.setFilter(preciselyUnexpiredMessageFilter);

    List<Message> unfilteredMessages = new ArrayList<>();
    unfilteredMessages.add(unexpiredMessage);
    unfilteredMessages.add(preciselyUnexpiredMessage);

    MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
    when(messageSource.allMessages()).thenReturn(unfilteredMessages);

    service.setMessageSource(messageSource);

    User user = new User();

    List<Message> result = service.filteredMessages(user);

    assertNotNull(result);

    assertEquals(2, result.size());
  }

  /**
   * Test that returns the message matching a given ID.
   */
  @Test
  public void returnsMessageMatchingId() {
    MessagesService service = new MessagesService();

    Message firstMessage = new Message();
    firstMessage.setId("uniqueMessageId-1");

    Message secondMessage = new Message();
    secondMessage.setId("anotherMessageId-2");

    List<Message> messagesFromRepository = new ArrayList<>();
    messagesFromRepository.add(firstMessage);
    messagesFromRepository.add(secondMessage);

    MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
    when(messageSource.allMessages()).thenReturn(messagesFromRepository);

    service.setMessageSource(messageSource);

    Message result = service.messageById("uniqueMessageId-1");

    assertEquals(firstMessage, result);
  }

  @Test
  public void returnsNullWhenNoMatchingId() {
    MessagesService service = new MessagesService();

    Message firstMessage = new Message();
    firstMessage.setId("uniqueMessageId-1");

    Message secondMessage = new Message();
    secondMessage.setId("anotherMessageId-2");

    List<Message> messagesFromRepository = new ArrayList<>();
    messagesFromRepository.add(firstMessage);
    messagesFromRepository.add(secondMessage);

    MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
    when(messageSource.allMessages()).thenReturn(messagesFromRepository);

    service.setMessageSource(messageSource);

    Message result = service.messageById("no-message-with-this-id");

    assertNull(result);
  }

  @Test
  public void throwsIllegalStateExceptionWhenMultipleMessagesMatchId() {
    assertThrows(IllegalStateException.class, () -> {
      MessagesService service = new MessagesService();

      Message firstMessage = new Message();
      firstMessage.setId("not-so-unique-id");

      Message secondMessage = new Message();
      secondMessage.setId("not-so-unique-id");

      List<Message> messagesFromRepository = new ArrayList<>();
      messagesFromRepository.add(firstMessage);
      messagesFromRepository.add(secondMessage);

      MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
      when(messageSource.allMessages()).thenReturn(messagesFromRepository);

      service.setMessageSource(messageSource);

      Message result = service.messageById("not-so-unique-id");
    });
  }

  @Test
  public void requestingMessageWithNullIdThrowsNPE() {
    assertThrows(NullPointerException.class, () -> {
      MessagesService service = new MessagesService();
      service.messageById(null);
    });
  }


  @Test
  public void requestAsUserMessageWithUnknownIdReturnsNull()
    throws ForbiddenMessageException {
    MessagesService service = new MessagesService();

    List<Message> messagesFromRepository = new ArrayList<>();

    MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
    when(messageSource.allMessages()).thenReturn(messagesFromRepository);

    service.setMessageSource(messageSource);

    assertNull(service.messageByIdForUser("id-does-not-match-any-message", new User()));
  }

  /**
   * Test the happy path, that a user successfully reads a current message for which the user is an
   * audience member.
   */
  @Test
  public void userInAudienceSucccessfullyReadsCurrentMessage()
    throws ForbiddenMessageException {
    MessagesService service = new MessagesService();

    MessageFilter yesFilter = mock(MessageFilter.class);
    when(yesFilter.test(any())).thenReturn(true);
    Message matchingMessage = new Message();
    matchingMessage.setFilter(yesFilter);
    matchingMessage.setId("yes-in-audience-of-this-message");

    List<Message> unfilteredMessages = new ArrayList<>();
    unfilteredMessages.add(matchingMessage);

    MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
    when(messageSource.allMessages()).thenReturn(unfilteredMessages);

    service.setMessageSource(messageSource);

    User user = new User();

    Message result = service.messageByIdForUser("yes-in-audience-of-this-message", user);

    assertEquals(matchingMessage, result);
  }

  @Test
  public void userInAudienceCannotReadExpiredMessage() {
    assertThrows(ExpiredMessageException.class, () -> {
      MessagesService service = new MessagesService();

      MessageFilter yesFilter = mock(MessageFilter.class);
      when(yesFilter.test(any())).thenReturn(true);
      when(yesFilter.getExpireDate()).thenReturn("2001-01-01");
      Message matchingMessage = new Message();
      matchingMessage.setFilter(yesFilter);
      matchingMessage.setId("in-audience-of-this-expired-message");

      List<Message> unfilteredMessages = new ArrayList<>();
      unfilteredMessages.add(matchingMessage);

      MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
      when(messageSource.allMessages()).thenReturn(unfilteredMessages);

      service.setMessageSource(messageSource);

      User user = new User();

      Message result = service.messageByIdForUser("in-audience-of-this-expired-message", user);
    });
  }

  @Test
  public void userInAudienceCannotReadPrematureMessage() {
    assertThrows(PrematureMessageException.class, () -> {
      MessagesService service = new MessagesService();

      MessageFilter yesFilter = mock(MessageFilter.class);
      when(yesFilter.test(any())).thenReturn(true);
      when(yesFilter.getGoLiveDate()).thenReturn("2999-01-01");
      Message matchingMessage = new Message();
      matchingMessage.setFilter(yesFilter);
      matchingMessage.setId("yes-in-audience-of-this-message");

      List<Message> unfilteredMessages = new ArrayList<>();
      unfilteredMessages.add(matchingMessage);

      MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
      when(messageSource.allMessages()).thenReturn(unfilteredMessages);

      service.setMessageSource(messageSource);

      User user = new User();

      Message result = service.messageByIdForUser("yes-in-audience-of-this-message", user);
    });
  }

  @Test
  public void userNotInAudienceCannotReadMessage() {
    assertThrows(UserNotInMessageAudienceException.class, () -> {
      MessagesService service = new MessagesService();

      MessageFilter noFilter = mock(MessageFilter.class);
      when(noFilter.test(any())).thenReturn(false);
      Message matchingMessage = new Message();
      matchingMessage.setFilter(noFilter);
      matchingMessage.setId("not-in-audience-of-this-message");

      List<Message> unfilteredMessages = new ArrayList<>();
      unfilteredMessages.add(matchingMessage);

      MessagesFromTextFile messageSource = mock(MessagesFromTextFile.class);
      when(messageSource.allMessages()).thenReturn(unfilteredMessages);

      service.setMessageSource(messageSource);

      User user = new User();

      Message result = service.messageByIdForUser("not-in-audience-of-this-message", user);
    });
  }

}
