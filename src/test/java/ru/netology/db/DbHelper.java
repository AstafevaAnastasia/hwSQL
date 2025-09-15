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
            queryRunner.update(connection, "DELETE FROM auth_codes");
            queryRunner.update(connection, "DELETE FROM card_transactions");
            queryRunner.update(connection, "DELETE FROM cards");
            queryRunner.update(connection, "DELETE FROM users");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLatestVerificationCode() {
        try (Connection conn = getConnection()) {
            return queryRunner.query(
                    conn,
                    "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1",
                    new ScalarHandler<String>()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении кода верификации", e);
        }
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
            return queryRunner.query(connection,
                    "SELECT status FROM users WHERE login = ?",
                    new ScalarHandler<String>(), login);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении статуса пользователя", e);
        }
    }
}