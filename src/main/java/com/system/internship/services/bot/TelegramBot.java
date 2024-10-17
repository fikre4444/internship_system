package com.system.internship.services.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
  private final TelegramClient telegramClient;

  private String botName;

  private String botToken;

  private final Map<Long, List<String>> userMessages = new HashMap<>();

  public TelegramBot(@Value("${telegrambot.bot-name}") String botName,
      @Value("${telegrambot.bot-token}") String botToken) {
    this.botName = botName;
    this.botToken = botToken;
    this.telegramClient = new OkHttpTelegramClient(botToken);
  }

  @Override
  public String getBotToken() {
    return botToken;
  }

  @Override
  public LongPollingUpdateConsumer getUpdatesConsumer() {
    return this;
  }

  @Override
  public void consume(Update update) {
    // Check if the update has a message and the message has text
    if (update.hasMessage() && update.getMessage().hasText()) {
      String messageText = update.getMessage().getText();
      long chatId = update.getMessage().getChatId();

      // Get user's previous messages or initialize a new list if none exist
      List<String> messages = userMessages.getOrDefault(chatId, new ArrayList<>());
      messages.add(messageText); // Add the new message to the user's list
      userMessages.put(chatId, messages); // Update the map
      System.out.println("some message was sent, " + messageText);
      // Build the response by joining all messages
      String responseText = String.join(", ", messages);

      SendMessage message = SendMessage // Create a message object
          .builder()
          .chatId(chatId)
          .text("Conversation so far: " + responseText)
          .build();
      try {
        telegramClient.execute(message); // Send our message object to the user
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    }
  }
}
