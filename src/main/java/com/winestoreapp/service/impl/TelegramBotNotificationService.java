package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.UserMapper;
import com.winestoreapp.exception.EntityNotFoundException;
import com.winestoreapp.exception.TelegramBotNotificationException;
import com.winestoreapp.model.User;
import com.winestoreapp.repository.RoleRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.service.NotificationService;
import com.winestoreapp.service.TelegramBotCredentialProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
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

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String textFromUSer = update.getMessage().getText();
            Long userChatId = update.getMessage().getChatId();
            switch (textFromUSer) {
                case "/start", "Main menu" -> {
                    executingTheStartCommand(
                            userChatId, update.getMessage().getChat().getFirstName());
                    sendPicture(userChatId);
                }
                case "/contacts", "Contacts" -> executingTheContactsCommand(
                        userChatId, update.getMessage().getChat().getFirstName());
                case "/wine_selection", "Select wine color"
                        -> executingTheWineColorSelectionCommand(userChatId);
                case "/help", "Help" -> executingHelpCommand(userChatId);
                case "/red_wine", "Red wine" -> executingRedWineCommand(userChatId);
                case "/white_wine", "White wine" -> executingWhiteWineCommand(userChatId);
                default -> processTextMessage(userChatId, update.getMessage().getText());
            }
        }
    }

    private void processTextMessage(Long userChatId, String text) {
        System.out.println("I will send your request to the manager");
        sendInnerMessageToChat(userChatId, "/start", getMainButtons());
    }

    private void executingTheStartCommand(Long chatId, String firstName) {
        String message = firstName + """
                , welcome to the Wine Store Bot!
                """;
        sendInnerMessageToChat(chatId, message, getMainButtons());
    }

    private void executingTheContactsCommand(Long chatId, String str) {
        String message = """
                Street name, 8
                Phone number: +38050 123 4578
                            """;
        sendInnerMessageToChat(chatId, message, getMainButtons());
    }

    private void executingTheWineColorSelectionCommand(Long chatId) {
        sendInnerMessageToChat(chatId, "Wine colors:", getWineColourButtons());
    }

    private void executingHelpCommand(Long chatId) {
        String helpMessage = """
                Chat bot features:
                1. /start: for starting application.
                2. /help: displays a list of functions.
                3. /exit: Log out. """;

        //        if (userRepository.findByTelegramChatId(chatId).isEmpty()) {
        //            sendInnerMessageToChat(chatId, helpMessage, getRegisterButtons());
        //        } else {
        //            sendInnerMessageToChat(chatId, helpMessage, getWorkButtons());
        //        }
        sendInnerMessageToChat(chatId, helpMessage, getMainButtons());
    }

    private void executingRedWineCommand(Long chatId) {
        sendInnerMessageToChat(chatId, "Red wines:", getRedWineButtons(chatId));
    }

    private void executingWhiteWineCommand(Long chatId) {
        sendInnerMessageToChat(chatId, "White wines:", getRedWineButtons(chatId));
    }

    private ReplyKeyboardMarkup getWineColourButtons() {
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("Red wine");
        firstRow.add("White wine");

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(firstRow);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        markup.setKeyboard(keyboardRows);
        return markup;
    }

    private ReplyKeyboardMarkup getMainButtons() {
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("Contacts");
        firstRow.add("Main menu");
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add("Select wine color");
        secondRow.add("Help");

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(firstRow);
        keyboardRows.add(secondRow);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        markup.setKeyboard(keyboardRows);
        return markup;
    }

    private ReplyKeyboardMarkup getRedWineButtons(Long chatId) {
        KeyboardRow row1 = new KeyboardRow();
        row1.add("fortified white");
        row1.add("sweet white");
        row1.add("semi-sweet white");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("semi-dry white");
        row2.add("dry white");

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(row1);
        keyboardRows.add(row2);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(false);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
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

    private void sendInnerMessageToChat(
            Long chatId,
            String textMessage,
            ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private ReplyKeyboardMarkup getRegisterButtons() {
        KeyboardRow loginLineButtons = new KeyboardRow();
        loginLineButtons.add("Select wine");
        loginLineButtons.add("Main menu");
        loginLineButtons.add("Help");

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(loginLineButtons);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        return keyboardMarkup;
    }

    private void sendPicture(Long chatId) {
        try {
            // Загрузка изображения
            InputFile avatar = new InputFile(new File(
                    "src/main/resources/static/wine_avatar.jpg"), "image.jpg");
            SendPhoto msg = new SendPhoto();
            msg.setChatId(chatId.toString());
            msg.setPhoto(avatar);

            execute(msg);
        } catch (Exception e) {
            log.error("Ошибка при отправке аватара", e);
        }
    }
}
