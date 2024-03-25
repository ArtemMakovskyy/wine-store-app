package com.winestoreapp.service;

import com.winestoreapp.repository.OrderRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.service.impl.TelegramBotNotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@ConditionalOnProperty(name = "telegram.bot.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Getter
public class TelegramBotStarter implements ApplicationRunner {
    private final TelegramBotCredentialProvider credentialProvider;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(
                    new TelegramBotNotificationService(
                            credentialProvider,
                            userRepository,
                            orderRepository));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
