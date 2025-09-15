package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class DashboardPage {
    private SelenideElement heading = $("h2");

    public DashboardPage() {
        heading.shouldBe(visible);
        heading.shouldHave(text("Личный кабинет"));
    }
}