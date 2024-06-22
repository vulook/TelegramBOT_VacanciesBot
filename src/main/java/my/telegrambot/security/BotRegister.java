package my.telegrambot.security;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * FileName: BotRegister
 * Author:   Andriy V
 * Date:     22.06.2024 21:37
 * Description:
 */


@Component
public class BotRegister {

    private final VacanciesBot vacanciesBot;

    public BotRegister(VacanciesBot vacanciesBot) {
        this.vacanciesBot = vacanciesBot;
    }

    @PostConstruct
    public void init () throws TelegramApiException {

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(vacanciesBot);

    }

}

