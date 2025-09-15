package ru.netology.data;

import lombok.Value;

public class DataHelper {

    // Данные для валидного пользователя
    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    // Код верификации
    @Value
    public static class VerificationCode {
        String code;
    }

    // Получение валидных данных пользователя
    public static AuthInfo getValidAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    // Получение невалидных данных пользователя
    public static AuthInfo getInvalidAuthInfo() {
        return new AuthInfo("vasya", "wrongpass");
    }

    // Получение данных для блокировки
    public static AuthInfo getBlockedUser() {
        return new AuthInfo("petya", "123qwerty");
    }
}