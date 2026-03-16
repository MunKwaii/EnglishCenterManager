package vn.edu.ute.view.test;

import vn.edu.ute.view.*;
import javax.swing.*;

public class TestBranchUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Test Branch Management UI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            frame.add(new BranchPanel());

            frame.setVisible(true);
        });
    }
}
