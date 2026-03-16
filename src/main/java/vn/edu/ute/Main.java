package vn.edu.ute;

import vn.edu.ute.view.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Đặt Look & Feel theo hệ điều hành để giao diện trông tự nhiên hơn
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Nếu lỗi thì dùng L&F mặc định của Java
        }

        // Khởi chạy form Login trên Event Dispatch Thread (EDT) — chuẩn Swing
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}