package com.winestoreapp.service;

public interface NotificationService {
    boolean sendNotification(String message, Long recipientId);
}
