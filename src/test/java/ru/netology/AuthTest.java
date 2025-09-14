package ru.netology;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import java.sql.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class AuthTest {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/app?serverTimezone=UTC";
    private static final String DB_USER = "app";
    private static final String DB_PASS = "pass";

    @BeforeEach
    void setUp() {
        Selenide.open("http://localhost:9999");
    }

    @Test
    void shouldLoginWithValidCodeFromDatabase() {
        // Вводим логин и пароль
        $("[data-test-id=login] input").setValue("vasya");
        $("[data-test-id=password] input").setValue("qwerty123");
        $("[data-test-id=action-login]").click();

        // Ждем появления поля для кода
        $("[data-test-id=code] input").shouldBe(visible);

        // Получаем код из базы данных
        String verificationCode = getLatestVerificationCodeFromDB();

        // Вводим код из базы
        $("[data-test-id=code] input").setValue(verificationCode);
        $("[data-test-id=action-verify]").click();

        // Проверяем успешный вход
        $("h2").shouldHave(text("Личный кабинет"));
    }

    @Test
    void shouldBlockAfterThreeFailedLoginAttempts() {
        for (int i = 0; i < 3; i++) {
            $("[data-test-id=login] input").setValue("vasya");
            $("[data-test-id=password] input").setValue("wrongpass");
            $("[data-test-id=action-login]").click();

            // Ждем сообщения об ошибке
            $(".notification").shouldHave(visible);

            // Закрываем уведомление
            if (i < 2) {
                try {
                    $(".notification .close-button").click();
                } catch (Exception e) {
                    // Если кнопки закрытия нет, просто продолжаем
                }
                sleep(1000);
            }
        }

        // Проверяем сообщение о блокировке
        $(".notification").shouldHave(text("Система заблокирована"));
    }

    private String getLatestVerificationCodeFromDB() {
        String code = null;
        int attempts = 0;
        while (code == null && attempts < 10) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String query = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1";

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {

                    if (rs.next()) {
                        code = rs.getString("code");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Попытка " + (attempts + 1) + ": " + e.getMessage());
            }

            if (code == null) {
                attempts++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (code == null) {
            throw new RuntimeException("Не удалось получить код подтверждения из БД");
        }

        return code;
    }
}