package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class LoginPage {
    private SelenideElement loginField = $("[data-test-id=login] input");
    private SelenideElement passwordField = $("[data-test-id=password] input");
    private SelenideElement loginButton = $("[data-test-id=action-login]");
    private SelenideElement errorNotification = $(".notification");
    private SelenideElement closeButton = $(".notification .close-button");

    public LoginPage() {
        loginField.shouldBe(visible);
    }

    // Общий метод для ввода данных и клика
    private void login(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
    }

    // Вход с валидными данными
    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        login(info);
        return new VerificationPage();
    }

    // Вход с невалидными данными
    public LoginPage invalidLogin(DataHelper.AuthInfo info) {
        login(info);
        return this;
    }

    // Закрытие уведомления
    public LoginPage closeNotification() {
        try {
            closeButton.click();
            errorNotification.shouldBe(hidden);
        } catch (Exception e) {
            // Если кнопки закрытия нет, просто продолжаем
        }
        return this;
    }

    // Проверка текста ошибки (автоматически проверяет видимость)
    public LoginPage verifyErrorText(String text) {
        errorNotification.shouldBe(visible);
        errorNotification.shouldHave(text(text));
        return this;
    }
}