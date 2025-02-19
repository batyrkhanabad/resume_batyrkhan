package org.example.myresume;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResumeBot extends TelegramLongPollingBot {

    private final String BOT_USERNAME = "batyr_CV_bot";
    private final String BOT_TOKEN = "7877294333:AAHpVR7bw6zfLvyxQ3lsZ2Uye7B_evBzmes";
    private String GROUP_CHAT_ID = "-1002341352106";

    private Map<Long, String> users = new HashMap<>();

    private boolean resumeSent = false;
    private boolean coverLetterSent = false;

    private int resumeCount = 0;
    private int feedbackCount = 0;
    private Map<Long, Integer> userRatings = new HashMap<>();

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
        long chatId;
        String userName;

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            userName = update.getMessage().getFrom().getUserName();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userName = update.getCallbackQuery().getFrom().getUserName();
        } else {
            return;
        }

        users.put(chatId, userName);

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            switch (messageText) {
                case "/start":
                case "🔄 Очистить историю":
                    users.clear();
                    sendMainMenu(chatId);
                    break;
                case "📄 Резюме":
                    sendDocument(chatId, "Абад Батырхан Асылханулы (2).pdf", "Мое резюме 📄");
                    resumeSent = true;
                    checkIfBothSent(chatId);
                    break;
                case "✉️ Сопроводительное письмо":
                    sendDocument(chatId, "Сопроводительное письмо Абад Батырхан .pdf", "Мое сопроводительное письмо ✉️");
                    coverLetterSent = true;
                    checkIfBothSent(chatId);
                    break;
                case "📞 Контакты":
                    sendTextMessage(chatId, "Мои контакты:\nTelegram: @batyrkhanabad\nWhatsapp: https://api.whatsapp.com/send/?phone=77763092266\nEmail: batyrkhanabad@gmail.com\nLinkedIn: https://www.linkedin.com/in/batyrkhan-abad-996406331/");
                    break;
                default:
                    sendTextMessage(chatId, "Неизвестная команда. Введите /start для получения списка доступных команд.");
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();

            switch (callbackData) {
                case "get_resume":
                    sendDocument(chatId, "Абад Батырхан Асылханулы (2).pdf", "Мое резюме 📄");
                    resumeSent = true;
                    checkIfBothSent(chatId);
                    break;
                case "get_coverletter":
                    sendDocument(chatId, "Сопроводительное письмо Абад Батырхан .pdf", "Мое сопроводительное письмо ✉️");
                    coverLetterSent = true;
                    checkIfBothSent(chatId);
                    break;
                case "get_contact":
                    sendTextMessage(chatId, "Мои контакты:\nTelegram: @batyrkhanabad\nWhatsapp: https://api.whatsapp.com/send/?phone=77763092266&text&type=phone_number&app_absent=0\nEmail: batyrkhanabad@gmail.com\nLinkedIn: https://www.linkedin.com/in/batyrkhan-abad-996406331/");
                    break;
                case "yes_skills":
                    sendTextMessage(chatId, "Спасибо, @" + userName + "! Я свяжусь с вами в ближайшее время.");

                    String userYesInfo = "Пользователь @" + userName + " (Chat ID: " + chatId + ") выбрал 'Да ✅' – ему понравилось резюме.";
                    sendTextMessage(Long.parseLong(GROUP_CHAT_ID), userYesInfo);
                    break;

                case "no_skills":
                    sendTextMessage(chatId, "Спасибо, @" + userName + ", за уделенное время! Буду рад возможности связаться в будущем.");

                    String userNotInfo = "Пользователь @" + userName + " (Chat ID: " + chatId + ") выбрал 'Нет ❌' – резюме его не заинтересовало.";
                    sendTextMessage(Long.parseLong(GROUP_CHAT_ID), userNotInfo);
                    break;
            }
        }
    }

    private void sendMainMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите действие:");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("📄 Резюме");
        row1.add("✉️ Сопроводительное письмо");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("📞 Контакты");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void sendTextMessage(long chatId, String text) {
        try {
            execute(new SendMessage(String.valueOf(chatId), text));
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

    private void checkIfBothSent(long chatId) {
        if (resumeSent && coverLetterSent) {
            sendQuestionWithButtons(chatId);
            resumeSent = false;
            coverLetterSent = false;
        }
    }

    private void sendQuestionWithButtons(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Подходят ли мои навыки для вашей компании?");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton yesButton = new InlineKeyboardButton("Да ✅");
        yesButton.setCallbackData("yes_skills");

        InlineKeyboardButton noButton = new InlineKeyboardButton("Нет ❌");
        noButton.setCallbackData("no_skills");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(yesButton);
        row.add(noButton);

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}