package vn.edu.ute.view;

import javax.swing.*;

public class TestLoginUI {
    public static void main(String[] args) {
        // 1. Thiết lập giao diện theo hệ điều hành (Windows/macOS/Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Chạy ứng dụng trong luồng xử lý giao diện
        SwingUtilities.invokeLater(() -> {
            // Khởi tạo Frame Đăng nhập
            LoginFrame login = new LoginFrame();

            // Hiển thị
            login.setVisible(true);
        });
    }
}