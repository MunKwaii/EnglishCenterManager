package vn.edu.ute.view.test;

import vn.edu.ute.view.*;
import javax.swing.*;

public class TestTeacherUI {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Quản lý Giáo viên - Test Mode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);

        frame.setContentPane(new TeacherPanel());

        frame.setVisible(true);
    }
}