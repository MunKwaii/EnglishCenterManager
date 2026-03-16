package vn.edu.ute.view.test;

import vn.edu.ute.view.*;
import javax.swing.*;

public class TestStudentUI {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Quản lý Học viên - MIS Center Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 650);
        frame.setLocationRelativeTo(null);

        // Nhúng StudentPanel vào JFrame theo phong cách setContentPane
        frame.setContentPane(new StudentPanel());

        frame.setVisible(true);
    }
}