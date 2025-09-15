package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class VerificationPage {
    private SelenideElement codeField = $("[data-test-id=code] input");
    private SelenideElement verifyButton = $("[data-test-id=action-verify]");
    private SelenideElement errorNotification = $(".notification");

    public VerificationPage() {
        codeField.shouldBe(visible);
    }

    // Ввод кода верификации
    public DashboardPage validVerify(String verificationCode) {
        codeField.setValue(verificationCode);
        verifyButton.click();
        return new DashboardPage();
    }

    // Ввод невалидного кода
    public VerificationPage invalidVerify(String verificationCode) {
        codeField.setValue(verificationCode);
        verifyButton.click();
        return this;
    }

    // Проверка ошибки
    public VerificationPage verifyErrorNotification() {
        errorNotification.shouldBe(visible);
        return this;
    }
}