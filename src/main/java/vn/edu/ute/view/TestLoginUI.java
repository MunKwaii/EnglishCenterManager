package vn.edu.ute.view;

import javax.swing.*;

public class TestLoginUI {
    public static void main(String[] args) {
        // dòng này để tự động scale giao diện trên màn hình độ phân giải cao
        System.setProperty("sun.java2d.uiScale", "2.0"); 

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}