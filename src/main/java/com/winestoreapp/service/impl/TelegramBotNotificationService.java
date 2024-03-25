package com.winestoreapp.service.impl;

import com.winestoreapp.exception.EntityNotFoundException;
import com.winestoreapp.model.Order;
import com.winestoreapp.model.RoleName;
import com.winestoreapp.model.User;
import com.winestoreapp.repository.OrderRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.service.NotificationService;
import com.winestoreapp.service.TelegramBotCredentialProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "telegram.bot.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class TelegramBotNotificationService
        extends TelegramLongPollingBot
        implements NotificationService {
    private static final String STORE_ADDRESS = """
            Street name, 8
            Phone number: +38050 123 4578
                        """;
    private static final String ORDER_MARKER = "ORDER_";
    private static final int MINIMUM_ORDER_LENGTH = 10;
    private static final String PATH_TO_IMAGE
            = "src/main/resources/static/images/telegram/wine_avatar.jpg";
    private static final String WINE_AVATAR_FILE_NAME = "wine_avatar.jpg";
    private final TelegramBotCredentialProvider telegramBotCredentialProvider;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

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
                    sendPicture(userChatId);
                    executingTheStartCommand(
                            userChatId, update.getMessage().getChat().getFirstName());
                }
                case "/contacts", "Contacts" -> executingTheContactsCommand(userChatId);
                case "/wine_selection", "Select wine by color"
                        -> executingTheWineColorSelectionCommand(userChatId);
                case "/help", "Help" -> executingHelpCommand(userChatId);
                case "/red_wine", "Red wine" -> executingRedWineCommand(userChatId);
                case "/white_wine", "White wine" -> executingWhiteWineCommand(userChatId);
                default -> processTextMessage(userChatId, textFromUSer, update);
            }
        }
    }

    @Override
    public boolean sendNotification(String message, Long recipientId) {
        if (recipientId != null) {
            sendInnerMessageToChat(recipientId, message, getMainButtons());
            log.info("A notification was sent to a user on Telegram. " + message);
            return true;
        }
        return false;
    }

    private void executingTheStartCommand(Long chatId, String firstName) {
        String message = firstName + """
                , welcome to the Wine Store Bot😀❕
                                
                     🔆Tips:🔆
                  ✔️ You can enter your order number to register. After you will get 
                  information about the state of your orders.
                  ✔️ You can ask a question to the manager and get an answer soon.
                  ✔️ You can use the buttons to receive information from our telegram bot.
                """;
        sendInnerMessageToChat(chatId, message, getMainButtons());
    }

    private void processTextMessage(Long chatId, String messageFromUser, Update update) {
        if (messageFromUser.length() >= MINIMUM_ORDER_LENGTH
                && messageFromUser.startsWith(ORDER_MARKER)) {
            userRegisterByOrderNumber(chatId, messageFromUser);
        } else {
            sendMessageFromUserToManager(chatId, messageFromUser, update);
        }
    }

    private void sendMessageFromUserToManager(Long chatId, String messageFromUser, Update update) {
        final List<User> usersByRole = userRepository.findUsersByRole(RoleName.ROLE_MANAGER);
        if (usersByRole.isEmpty()) {
            String message = "Unfortunately there are no managers at the moment. Write"
                    + " a message later.";
            sendInnerMessageToChat(chatId, message, getMainButtons());
            log.info(message);
        } else {
            sendInnerMessageToChat(
                    getManagerTelegramChatId(usersByRole),
                    messageFromUser + createTelegramLinkToUser(update),
                    getMainButtons());
            sendInnerMessageToChat(
                    chatId,
                    "The manager will process the message then contact you.",
                    getMainButtons());
            log.info("Message " + messageFromUser + "  was send to manager");
        }
    }

    private Long getManagerTelegramChatId(List<User> users) {
        final User user = users.stream()
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("Can't get user"));
        return user.getTelegramChatId();
    }

    private String createTelegramLinkToUser(Update update) {
        return "Link to user: @" + update.getMessage().getFrom().getUserName();
    }

    private void userRegisterByOrderNumber(Long chatId, String orderNumber) {
        log.info("Process telegram user registration");
        final Optional<Order> orderByOrderNumber
                = orderRepository.findOrderByOrderNumber(orderNumber);
        if (orderByOrderNumber.isEmpty()) {
            sendInnerMessageToChat(chatId, "You are wrong order number: " + orderNumber
                    + ". Please enter the correct order number, or you can ask, or "
                    + "use the menu.", getMainButtons());
            log.info("Wrong order number was entered in telegram bot" + orderNumber);
        } else {
            log.info("Correct order number was entered in telegram bot: " + orderNumber);
            final User userFromOrder = orderByOrderNumber.orElseThrow(
                    () -> new EntityNotFoundException("Can't get order from order number: "
                            + orderNumber)).getUser();
            final Optional<User> userByTelegramChatId
                    = userRepository.findUserByTelegramChatId(chatId);
            if (userByTelegramChatId.isEmpty()) {
                userFromOrder.setTelegramChatId(chatId);
                userRepository.save(userFromOrder);
                sendInnerMessageToChat(chatId,
                        "Congratulations you are registered!", getMainButtons());
                log.info("No ID match was found in the database. Chat id " + chatId
                        + ", was added to user with id:"
                        + userFromOrder.getId());
            } else if (userByTelegramChatId.get().getId() != userFromOrder.getId()) {
                final User userByTelegramId = userByTelegramChatId.orElseThrow(
                        () -> new EntityNotFoundException("Can't find user by id: "
                                + userByTelegramChatId.get().getId()));
                userByTelegramId.setTelegramChatId(null);
                userRepository.save(userByTelegramId);
                userFromOrder.setTelegramChatId(chatId);
                userRepository.save(userFromOrder);
                sendInnerMessageToChat(chatId,
                        "Your telegram chat has been relinked to the current user.",
                        getMainButtons());
                log.info("Telegram ID was found from another user with id "
                        + userByTelegramId.getId()
                        + ". The Telegram ID has been transferred to the current user with "
                        + "id " + userFromOrder.getId() + ".");
            }
            sendInnerMessageToChat(chatId,
                    "You are already registered!", getMainButtons());
        }
    }

    private void executingTheContactsCommand(Long chatId) {
        sendInnerMessageToChat(chatId, STORE_ADDRESS, getMainButtons());
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
        secondRow.add("Select wine by color");
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

    private void sendPicture(Long chatId) {
        try {
            InputFile avatarImage = new InputFile(
                    new File(PATH_TO_IMAGE), WINE_AVATAR_FILE_NAME);
            SendPhoto msg = new SendPhoto();
            msg.setChatId(chatId.toString());
            msg.setPhoto(avatarImage);
            execute(msg);
        } catch (Exception e) {
            log.error("Error sending pictures in Telegram", e);
        }
    }
}
