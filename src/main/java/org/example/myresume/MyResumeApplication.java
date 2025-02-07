package org.example.myresume;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class MyResumeApplication {

    public static void main(String[] args) {
        try {
            System.out.println("Запускаем TelegramBotsApi...");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            System.out.println("Регистрируем бота...");
            botsApi.registerBot(new ResumeBot());

            System.out.println("Бот успешно запущен!");
        } catch (Exception e) {
            System.out.println("Ошибка при запуске бота: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
