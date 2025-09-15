package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class VerificationPage {
    private SelenideElement codeField = $("[data-test-id=code] input");
    private SelenideElement verifyButton = $("[data-test-id=action-verify]");
    private SelenideElement errorNotification = $(".notification");

    public VerificationPage() {
        codeField.shouldBe(visible);
    }

    // Общий метод для ввода кода и клика
    private void verifyCode(String verificationCode) {
        codeField.setValue(verificationCode);
        verifyButton.click();
    }

    // Ввод кода верификации
    public DashboardPage validVerify(String verificationCode) {
        verifyCode(verificationCode);
        return new DashboardPage();
    }

    // Ввод невалидного кода
    public VerificationPage invalidVerify(String verificationCode) {
        verifyCode(verificationCode);
        return this;
    }

    // Проверка ошибки с текстом сообщения
    public VerificationPage verifyErrorNotification(String expectedText) {
        errorNotification.shouldBe(visible);
        errorNotification.shouldHave(text(expectedText));
        return this;
    }
}