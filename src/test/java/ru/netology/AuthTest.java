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

    @BeforeAll
    static void setUpAll() {
        DbHelper.clearDatabase();
    }

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
        // Arrange
        DataHelper.AuthInfo validUser = DataHelper.getValidAuthInfo();

        // Act
        LoginPage loginPage = new LoginPage();
        VerificationPage verificationPage = loginPage.validLogin(validUser);

        String verificationCode = DbHelper.getLatestVerificationCode();
        DashboardPage dashboardPage = verificationPage.validVerify(verificationCode);

        // Assert
        assertEquals("Личный кабинет", dashboardPage.getHeadingText());
    }

    @Test
    void shouldBlockAfterThreeFailedLoginAttempts() {
        // Arrange
        DataHelper.AuthInfo invalidUser = DataHelper.getInvalidAuthInfo();
        LoginPage loginPage = new LoginPage();

        // Act & Assert
        for (int i = 0; i < 3; i++) {
            loginPage = loginPage.invalidLogin(invalidUser)
                    .verifyErrorNotificationVisible();

            if (i < 2) {
                loginPage.closeNotification();
            }
        }

        loginPage.verifyErrorText("Система заблокирована");

        // Проверяем, что пользователь заблокирован в БД
        String userStatus = DbHelper.getUserStatus(invalidUser.getLogin());
        assertEquals("blocked", userStatus);
    }
}