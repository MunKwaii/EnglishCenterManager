package vn.edu.ute.view.test;

import vn.edu.ute.view.*;
import javax.swing.*;

public class TestCourseUI {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        JFrame frame = new JFrame("Quản lý Khóa học - Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); // Giữa màn hình

        // Nhúng CoursePanel vào JFrame
        frame.setContentPane(new CoursePanel());

        frame.setVisible(true);
    }
}