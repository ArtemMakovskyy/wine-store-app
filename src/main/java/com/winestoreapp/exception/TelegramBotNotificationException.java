package com.winestoreapp.exception;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBotNotificationException extends RuntimeException {
    public TelegramBotNotificationException(String message, TelegramApiException e) {
        super(message);
    }
}
