package org.example.myresume;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.io.InputStream;

public class ResumeBot extends TelegramLongPollingBot {

    private final String BOT_USERNAME = "resume_batyrkhan_bot";
    private final String BOT_TOKEN = "7574841088:AAHz3fY9OviCgm3gO_Lkqoem170M-eITzk4";


    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    sendTextMessage(chatId, "Привет! Я бот для отправки резюме и сопроводительного письма.\n\n" +
                            "Доступные команды:\n" +
                            "/resume - получить резюме\n" +
                            "/coverletter - получить сопроводительное письмо");
                    break;
                case "/resume":
                    sendDocument(chatId, "Батырхан Абад Асылханулы Резюме (1) — копия.pdf", "Вот твое резюме 📄");
                    break;
                case "/coverletter":
                    sendDocument(chatId, "Сопроводительное письмо Абад Батырхан 2 — копия.pdf", "Вот твое сопроводительное письмо ✉️");
                    break;
                default:
                    sendTextMessage(chatId, "Неизвестная команда. Используй /start для списка команд.");
            }
        }
    }

    private void sendTextMessage(long chatId, String text) {
        try {
            execute(new org.telegram.telegrambots.meta.api.methods.send.SendMessage(String.valueOf(chatId), text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDocument(long chatId, String fileName, String caption) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                sendTextMessage(chatId, "Ошибка! Файл " + fileName + " не найден.");
                return;
            }

            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(new InputFile(inputStream, fileName));
            sendDocument.setCaption(caption);

            execute(sendDocument);
        } catch (Exception e) {
            e.printStackTrace();
            sendTextMessage(chatId, "Ошибка при отправке файла.");
        }
    }
}
