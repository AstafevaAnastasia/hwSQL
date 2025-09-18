package ru.netology;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.db.DbHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.VerificationPage;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    @AfterAll
    static void tearDownAll() {
        DbHelper.clearDatabase();
    }

    @BeforeEach
    void setUp() {
        Selenide.open("http://localhost:9999");
    }

    @Test
    void shouldLoginWithValidCodeFromDatabase() {
        DataHelper.AuthInfo validUser = DataHelper.getValidAuthInfo();

        LoginPage loginPage = new LoginPage();
        VerificationPage verificationPage = loginPage.validLogin(validUser);

        String verificationCode = DbHelper.getLatestVerificationCode();
        DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldBlockAfterThreeFailedLoginAttempts() {
        DataHelper.AuthInfo invalidUser = DataHelper.getInvalidAuthInfo();
        LoginPage loginPage = new LoginPage();

        for (int i = 0; i < 3; i++) {
            loginPage = loginPage.invalidLogin(invalidUser)
                    .verifyErrorText("Ошибка! Неверно указан логин или пароль");
        }

        loginPage.verifyErrorText("Система заблокирована");

        String userStatus = DbHelper.getUserStatus(invalidUser.getLogin());
        assertEquals("blocked", userStatus);
    }
}