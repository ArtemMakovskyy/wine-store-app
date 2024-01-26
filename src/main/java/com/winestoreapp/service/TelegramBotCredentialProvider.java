package com.winestoreapp.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class TelegramBotCredentialProvider {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String token;
}
