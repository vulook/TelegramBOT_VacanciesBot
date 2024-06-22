package my.telegrambot.security;

import my.telegrambot.model.Vacancy;
import my.telegrambot.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FileName: VacanciesBot
 * Author:   Andriy V
 * Date:     22.06.2024 21:38
 * Description:
 */


@Component
public class VacanciesBot extends TelegramLongPollingBot {


    private final VacancyService vacancyService;

    private final Map<Long, String> lastShownVacancyLevel = new HashMap<>();

    @Autowired
    public VacanciesBot(VacancyService vacancyService) {
        super("6175660704:AAERvzyuFvowP6vRWe7Hqi552houSABarMA");
        this.vacancyService = vacancyService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {

            if (update.getMessage() != null) {
                handleStartCommand(update);
            }

            if (update.getCallbackQuery() != null) {
                String callbackData = update.getCallbackQuery().getData();

                // for Junior
                if (callbackData.equals("showJuniorVacancies")) {
                    showJuniorVacancies(update);

                    // for Middle
                } else if (callbackData.equals("showMiddleVacancies")) {
                    showMiddleVacancies(update);

                    // for Senior
                } else if (callbackData.equals("showSeniorVacancies")) {
                    showSeniorVacancies(update);

                    // for vacancy
                } else if (callbackData.startsWith("vacancyId=")) {
                    String id = callbackData.split("=")[1];
                    showVacancyDescription(id, update);

                    // for back to menu (Vacancies)
                } else if (callbackData.equals("backToVacancies")) {
                    handleBackToVacanciesCommand(update);

                    // for back to menu (Start Menu)
                } else if (callbackData.equals("backToStartMenu")) {
                    handleBackToStartMenuCommand(update);
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("Can not message to user!", ex);
        }

    }

    private void handleStartCommand(Update update) {
        String text = update.getMessage().getText();
        System.out.println("Received text is " + text);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        //sendMessage.setText("Your message '" + text + "' was received!");
        sendMessage.setText("Welcome to vacancies bot! Please, choose your title");
        sendMessage.setReplyMarkup(getStartMenu());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    // back to Vacancies
    private void handleBackToVacanciesCommand(Update update) throws TelegramApiException {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String level = lastShownVacancyLevel.get(chatId);

        switch (level) {
            case "junior" -> showJuniorVacancies(update);
            case "middle" -> showMiddleVacancies(update);
            case "senior" -> showSeniorVacancies(update);
            default -> throw new IllegalStateException("Unexpected value: " + level);
        }
    }

    // back to Start Menu
    private void handleBackToStartMenuCommand(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\n");
        sendMessage.setText("Choose title: ");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(getStartMenu());
        execute(sendMessage);
    }

    // show Junior Vacancies
    private void showJuniorVacancies(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\n");
        sendMessage.setText("Please choose the Junior vacancy: ");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getJuniorVacanciesMenu());
        execute(sendMessage);

        lastShownVacancyLevel.put(chatId, "junior");

    }

    private ReplyKeyboard getVacanciesMenu(List<Vacancy> vacancies) {

        List<InlineKeyboardButton> listButton = new ArrayList<>();
        for (Vacancy vacancy : vacancies) {
            InlineKeyboardButton vacancyButton = new InlineKeyboardButton();
            vacancyButton.setText(vacancy.getTitle());
            vacancyButton.setCallbackData("vacancyId=" + vacancy.getId());
            listButton.add(vacancyButton);
        }

        InlineKeyboardMarkup keyboardForJunior = new InlineKeyboardMarkup();
        keyboardForJunior.setKeyboard(List.of(listButton));

        return keyboardForJunior;

    }

    private ReplyKeyboard getJuniorVacanciesMenu() {
        List<Vacancy> vacancies = vacancyService.getJuniorVacancies();
        return getVacanciesMenu(vacancies);
    }

    // show Middle Vacancies
    private void showMiddleVacancies(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\n");
        sendMessage.setText("Please choose the Middle vacancy: ");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getMiddleVacanciesMenu());
        execute(sendMessage);

        lastShownVacancyLevel.put(chatId, "middle");

    }

    private ReplyKeyboard getMiddleVacanciesMenu() {
        List<Vacancy> vacancies = vacancyService.getMiddleVacancies();
        return getVacanciesMenu(vacancies);
    }

    // show Senior Vacancies
    private void showSeniorVacancies(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\n");
        sendMessage.setText("Please choose the Senior vacancy: ");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getSeniorVacanciesMenu());
        execute(sendMessage);

        lastShownVacancyLevel.put(chatId, "senior");

    }

    private ReplyKeyboard getSeniorVacanciesMenu() {
        List<Vacancy> vacancies = vacancyService.getSeniorVacancies();
        return getVacanciesMenu(vacancies);
    }

    // Get show Vacancy Description
    private void showVacancyDescription(String id, Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Vacancy vacancy = vacancyService.get(id);

        String description = "\n" +
                "Id: " + vacancy.getId() +
                "\nTitle: " + vacancy.getTitle() +
                "\nShort description: " + vacancy.getShortDescription() +
                "\nLong description: " + vacancy.getLongDescription() +
                "\nCompany: " + vacancy.getCompany() +
                "\nSalary: " + vacancy.getSalary() +
                "\nLink: " + vacancy.getLink();

        sendMessage.setText(description);

        // back to menu
        sendMessage.setReplyMarkup(getBackToMenu());
        execute(sendMessage);
    }

    // Buttons for back to Menu
    private ReplyKeyboard getBackToMenu() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton backToVacanciesButton = new InlineKeyboardButton();
        backToVacanciesButton.setText("Back to vacancies");
        backToVacanciesButton.setCallbackData("backToVacancies");
        row.add(backToVacanciesButton);

        InlineKeyboardButton backToStartMenuButton = new InlineKeyboardButton();
        backToStartMenuButton.setText("Back to start menu");
        backToStartMenuButton.setCallbackData("backToStartMenu");
        row.add(backToStartMenuButton);

        InlineKeyboardButton getChatGPTButton = new InlineKeyboardButton();
        getChatGPTButton.setText("Get cover letter ");
        getChatGPTButton.setUrl("https://chat.openai.com/");
        row.add(getChatGPTButton);

        return new InlineKeyboardMarkup(List.of(row));
    }

    // Get Start Menu
    private ReplyKeyboard getStartMenu() {
        List<InlineKeyboardButton> rowListButton = new ArrayList<>();

        InlineKeyboardButton junior = new InlineKeyboardButton();
        junior.setText("Junior");
        junior.setCallbackData("showJuniorVacancies");
        rowListButton.add(junior);

        InlineKeyboardButton middle = new InlineKeyboardButton();
        middle.setText("Middle");
        middle.setCallbackData("showMiddleVacancies");
        rowListButton.add(middle);

        InlineKeyboardButton senior = new InlineKeyboardButton();
        senior.setText("Senior");
        senior.setCallbackData("showSeniorVacancies");
        rowListButton.add(senior);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(List.of(rowListButton));

        return keyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return "aj vacancies bot";
    }

}
