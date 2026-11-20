package vn.edu.sales;

import vn.edu.sales.db.Database;
import vn.edu.sales.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.Console;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        String password = System.getenv("SALES_DB_PASSWORD");
        if (password == null || password.isBlank()) {
            Console console = System.console();
            if (console != null) {
                char[] chars = console.readPassword("Nhap mat khau SQL cua qlbh_app: ");
                password = chars == null ? "" : new String(chars);
                if (chars != null) Arrays.fill(chars, '\0');
            } else {
                System.out.print("Nhap mat khau SQL cua qlbh_app: ");
                password = new Scanner(System.in).nextLine();
            }
        }
        Database.setPassword(password);
        try (Connection ignored = Database.getConnection()) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
        } catch (Exception ex) {
            String message = ex.getMessage() == null ? ex.toString() : ex.getMessage();
            JOptionPane.showMessageDialog(null,
                    "Khong the ket noi SQL Server.\nKiem tra database QuanLyBanHang, tai khoan qlbh_app va cong 14331.\n\nChi tiet: " + message,
                    "Loi ket noi", JOptionPane.ERROR_MESSAGE);
            System.err.println(message);
            System.exit(1);
        }
    }
}
