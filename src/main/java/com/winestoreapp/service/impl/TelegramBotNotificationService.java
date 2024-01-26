package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.UserMapper;
import com.winestoreapp.exception.EntityNotFoundException;
import com.winestoreapp.exception.TelegramBotNotificationException;
import com.winestoreapp.model.User;
import com.winestoreapp.repository.RoleRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.service.NotificationService;
import com.winestoreapp.service.TelegramBotCredentialProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotNotificationService
        extends TelegramLongPollingBot
        implements NotificationService {
    private final TelegramBotCredentialProvider telegramBotCredentialProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public String getBotUsername() {
        return telegramBotCredentialProvider.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramBotCredentialProvider.getToken();
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String textFromUSer = update.getMessage().getText();
            Long userChatId = update.getMessage().getChatId();
            switch (textFromUSer) {
                case "/start", "Start application" -> startCommandReceived(
                        userChatId, update.getMessage().getChat().getFirstName());
                default -> startCommandReceived(userChatId, update.getMessage().getText());
            }
        }
    }

    @Override
    public boolean sendNotification(String message, Long recipientId) {
        if (recipientId != null) {
            final User user = userRepository.findById(recipientId).orElseThrow(
                    () -> new EntityNotFoundException("Can't find user by id " + recipientId));
            if (user.getTelegramChatId() == null) {
                log.debug("User with id " + user.getTelegramChatId()
                        + " doesn't have telegram ID. User should login "
                        + "in Bot to getting Telegram notification.");
                return false;
            }
            message = "API NOTIFICATION:\n" + message;
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.getTelegramChatId());
            sendMessage.setText("*" + message + "*");
            sendMessage.setParseMode("MarkdownV2");
            try {
                execute(sendMessage);
                return true;
            } catch (TelegramApiException e) {
                throw new TelegramBotNotificationException("Can't execute message", e);
            }
        }
        return false;
    }

    private void startCommandReceived(Long chatId, String firstName) {
        String message = firstName + """
                , welcome to the Wine Store Bot!
                For identity, input your email and press "enter",      
                or wright down your question administrator will call you after process.
                """;
    }
}
