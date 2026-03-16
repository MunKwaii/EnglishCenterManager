package vn.edu.ute.view.test;

import vn.edu.ute.view.*;
import javax.swing.*;
import java.awt.*;

public class TestSchedulePanelUI {
    public static void main(String[] args) {
        // Cài đặt giao diện
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Đảm bảo UI được khởi tạo trên Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Kiểm tra Giao diện Quản lý Lịch học (SchedulePanel)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát chương trình khi đóng cửa sổ
            frame.setSize(1000, 600); // Kích thước cửa sổ
            frame.setLocationRelativeTo(null); // Hiển thị ở giữa màn hình

            // Khởi tạo SchedulePanel và thêm vào giữa khung hình
            // Lưu ý: SchedulePanel sẽ gọi các Service và kết nối DB để lấy dữ liệu thực tế
            SchedulePanel schedulePanel = new SchedulePanel();
            frame.add(schedulePanel, BorderLayout.CENTER);

            // Hiển thị giao diện
            frame.setVisible(true);
        });
    }
}
