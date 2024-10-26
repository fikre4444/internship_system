package com.system.internship.services.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import com.system.internship.domain.Account;
import com.system.internship.domain.ChatId;
import com.system.internship.dto.LoginDto;
import com.system.internship.repository.AccountRepository;
import com.system.internship.repository.ChatIdRepository;
import com.system.internship.services.AccountService;
import com.system.internship.services.AuthService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
  private final TelegramClient telegramClient;
  private final Map<Long, Boolean> awaitingCredentials = new HashMap<>();
  // Tracks if the user is in the /register process

  private final String botName;
  private final String botToken;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AccountService accountService;

  @Autowired
  private ChatIdRepository chatIdRepo;

  @Autowired
  private AuthService authService;

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
    if (update.hasMessage() && update.getMessage().hasText()) {
      String messageText = update.getMessage().getText();
      long chatId = update.getMessage().getChatId();

      // Check if the user is in the /register process
      if (awaitingCredentials.getOrDefault(chatId, false)) {
        handleRegistration(chatId, messageText);
        return;
      }

      // Process commands
      switch (messageText) {
        case "/start":
          sendMessage(chatId, "Hi! Choose a command from the menu.");
          break;
        case "/description":
          sendMessage(chatId,
              "Hello User, this is a bot used for the Mekelle University Internship Managment System, as a Notification System. If you are Registered in the Internship Management system, please register here by using your username and password of the IMS.");
          break;
        case "/register":
          if (!isChatIdRegistered(chatId)) {
            awaitingCredentials.put(chatId, true); // Set the flag to expect credentials
            sendMessage(chatId,
                "Provide your username and password in the format 'username password'. don't forget to put space between your username and password.");
          } else {
            sendMessage(chatId, "You have already been registered.");
          }
          break;
        default:
          sendMessage(chatId, "Unknown command.");
          break;
      }
    }
  }

  private void handleRegistration(long chatId, String credentials) {
    // Assuming credentials are in "username password" format
    String[] parts = credentials.split(" ");
    if (parts.length == 2) {
      String username = parts[0];
      String password = parts[1];

      // Here you would validate username and password (placeholder for actual
      // validation logic)
      if (isValidUser(username, password)) {
        Account account = accountRepository.findByUsername(username).get();
        // here you associate the chatId with the account.
        ChatId chatid = ChatId.builder().chatId(chatId).associatedAccount(account).build();
        account.setChatId(chatid);
        chatIdRepo.save(chatid);
        // accountRepository.save(account);

        String message = "Successfully Registered! Hello " + account.getFirstName()
            + " from now on you can get notifications through this bot, and for more details you can go to the website and into your account.";
        sendMessage(chatId, message);
      } else {
        sendMessage(chatId, "Incorrect Credentials, goodbye.");
      }
    } else {
      sendMessage(chatId, "Please enter credentials in the format 'username password'.");
    }

    awaitingCredentials.put(chatId, false); // Reset the flag after handling registration
  }

  private boolean isValidUser(String username, String password) {
    LoginDto loginDto = LoginDto.builder().username(username).password(password).build();
    try {
      String jwt = authService.authenticateAccount(loginDto);
      if (jwt.length() > 0) {
        return true;
      }
    } catch (Exception exception) {
      return false;
    }
    return false;
  }

  private boolean isChatIdRegistered(long chatId) {
    Optional<ChatId> chatIdOpt = chatIdRepo.findByChatId(chatId);
    if (chatIdOpt.isPresent()) {
      return true;
    }
    return false;
  }

  public void sendMessage(long chatId, String text) {
    SendMessage message = SendMessage.builder()
        .chatId(chatId)
        .text(text)
        .parseMode("HTML")
        .build();
    try {
      telegramClient.execute(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}
