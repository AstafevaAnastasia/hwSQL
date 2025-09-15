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

    // Вход с валидными данными
    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
        return new VerificationPage();
    }

    // Вход с невалидными данными
    public LoginPage invalidLogin(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
        return this;
    }

    // Проверка отображения ошибки
    public LoginPage verifyErrorNotificationVisible() {
        errorNotification.shouldBe(visible);
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

    // Проверка текста ошибки
    public LoginPage verifyErrorText(String text) {
        errorNotification.shouldHave(text(text));
        return this;
    }
}