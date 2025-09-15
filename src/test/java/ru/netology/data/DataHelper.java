package ru.netology.data;

public class DataHelper {

    // Данные для валидного пользователя
    public static class AuthInfo {
        private String login;
        private String password;

        public AuthInfo(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }
    }

    // Код верификации
    public static class VerificationCode {
        private String code;

        public VerificationCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
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