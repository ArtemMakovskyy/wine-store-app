package com.winestoreapp.service;

import com.winestoreapp.dto.mapper.UserMapper;
import com.winestoreapp.repository.RoleRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.service.impl.TelegramBotNotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//@Component
@RequiredArgsConstructor
@Getter
public class TelegramBotStarter implements ApplicationRunner {
    private final TelegramBotCredentialProvider credentialProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(
                    new TelegramBotNotificationService(
                            credentialProvider,
                            userRepository,
                            userMapper,
                            roleRepository));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
