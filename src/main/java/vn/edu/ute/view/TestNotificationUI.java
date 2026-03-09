package vn.edu.ute.view;

import javax.swing.*;

public class TestNotificationUI {
    public static void main(String[] args) {
        // Thiết lập giao diện theo hệ điều hành cho đồng bộ
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi tạo cửa sổ chính
        JFrame frame = new JFrame("Hệ thống Thông báo - MIS Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Kích thước chuẩn để hiển thị cả Form soạn thảo và Bảng danh sách
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null); // Hiển thị chính giữa màn hình

        // Nhúng NotificationPanel vào JFrame theo phong cách setContentPane
        frame.setContentPane(new NotificationPanel());

        // Hiển thị giao diện
        frame.setVisible(true);
    }
}