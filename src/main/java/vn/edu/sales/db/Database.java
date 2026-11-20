package vn.edu.sales.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static volatile String password;
    private Database() { }

    public static void setPassword(String value) { password = value; }

    public static Connection getConnection() throws SQLException {
        String url = setting("SALES_DB_URL",
                "jdbc:sqlserver://localhost:14331;databaseName=QuanLyBanHang;encrypt=true;trustServerCertificate=true");
        String user = setting("SALES_DB_USER", "qlbh_app");
        String pass = setting("SALES_DB_PASSWORD", password == null ? "" : password);
        if (pass.isBlank()) throw new SQLException("Chua nhap mat khau SQL Server. Hay khoi dong lai bang run.bat.");
        return DriverManager.getConnection(url, user, pass);
    }

    private static String setting(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
