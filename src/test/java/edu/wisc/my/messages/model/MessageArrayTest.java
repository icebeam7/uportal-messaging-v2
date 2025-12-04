package edu.wisc.my.messages.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MessageArrayTest {

  @Test
  public void createMessageArrayFromList() {

    List<Message> someMessages = new ArrayList<>();

    Message aMessage = new Message();
    Message anotherMessage = new Message();
    Message yetAnotherMessage = new Message();

    someMessages.add(aMessage);
    someMessages.add(anotherMessage);
    someMessages.add(yetAnotherMessage);

    MessageArray messageArray = new MessageArray(someMessages);

    assertArrayEquals(someMessages.toArray(), messageArray.getMessages());
  }
}
