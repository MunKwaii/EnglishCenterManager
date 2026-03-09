package vn.edu.ute.view;

import javax.swing.*;

public class TestClassUI {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Quản lý Lớp học - Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null); // Giữa màn hình

        // Nhúng ClassPanel vào JFrame
        frame.setContentPane(new ClassPanel());

        frame.setVisible(true);
    }
}
