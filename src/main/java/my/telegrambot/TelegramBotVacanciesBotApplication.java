package my.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelegramBotVacanciesBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotVacanciesBotApplication.class, args);

        System.out.println("The bot has already started!");
    }

}
