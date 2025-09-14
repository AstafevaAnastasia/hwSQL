package ru.netology;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            $(".notification").shouldHave(text("Ошибка"));

            // Закрываем уведомление, если оно мешает
            if (i < 2) {
                $(".notification .close-button").click();
                sleep(1000); // Небольшая пауза между попытками
            }
        }

        // Третья попытка - проверяем блокировку
        $("[data-test-id=login] input").setValue("vasya");
        $("[data-test-id=password] input").setValue("wrongpass");
        $("[data-test-id=action-login]").click();

        // Проверяем сообщение о блокировке
        $(".notification").shouldHave(text("Система заблокирована"));
    }

    private String getLatestVerificationCodeFromDB() {
        String code = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Получаем последний код для пользователя vasya
            String query = "SELECT ac.code " +
                    "FROM auth_codes ac " +
                    "JOIN users u ON ac.user_id = u.id " +
                    "WHERE u.login = 'vasya' " +
                    "ORDER BY ac.created DESC " +
                    "LIMIT 1";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                if (rs.next()) {
                    code = rs.getString("code");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения кода из БД", e);
        }
        return code;
    }
}