package ru.netology.db;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DbHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/app?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "app";
    private static final String DB_PASS = "pass";
    private static QueryRunner queryRunner = new QueryRunner();

    // Получение соединения с БД
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // Очистка базы данных
    public static void clearDatabase() {
        try (Connection connection = getConnection()) {
            queryRunner.update(connection, "SET FOREIGN_KEY_CHECKS = 0");
            queryRunner.update(connection, "DELETE FROM auth_codes");
            queryRunner.update(connection, "DELETE FROM card_transactions");
            queryRunner.update(connection, "DELETE FROM cards");
            queryRunner.update(connection, "DELETE FROM users");
            queryRunner.update(connection, "SET FOREIGN_KEY_CHECKS = 1");
            System.out.println("База данных успешно очищена");
        } catch (SQLException e) {
            System.err.println("Ошибка при очистке базы данных: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Получение последнего кода верификации
    public static String getLatestVerificationCode() {
        long startTime = System.currentTimeMillis();
        long timeout = 15000; // 15 секунд

        while (System.currentTimeMillis() - startTime < timeout) {
            try (Connection conn = getConnection()) {
                List<Map<String, Object>> results = queryRunner.query(
                        conn,
                        "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1",
                        new MapListHandler()
                );

                if (!results.isEmpty()) {
                    return (String) results.get(0).get("code");
                }

            } catch (SQLException e) {
                System.out.println("Ошибка при запросе кода: " + e.getMessage());
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new RuntimeException("Код верификации не появился в базе данных в течение 15 секунд");
    }

    // Блокировка пользователя
    public static void blockUser(String login) {
        try (Connection connection = getConnection()) {
            queryRunner.update(connection,
                    "UPDATE users SET status = 'blocked' WHERE login = ?", login);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при блокировке пользователя", e);
        }
    }

    // Проверка статуса пользователя
    public static String getUserStatus(String login) {
        try (Connection connection = getConnection()) {
            Object result = queryRunner.query(connection,
                    "SELECT status FROM users WHERE login = ?",
                    new ScalarHandler<>(), login);
            return result != null ? result.toString() : null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении статуса пользователя", e);
        }
    }
}