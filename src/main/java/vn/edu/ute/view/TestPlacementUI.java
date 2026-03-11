package vn.edu.ute.view;

import javax.swing.*;

public class TestPlacementUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Fix the LazyInitializationException for Student entities (just basic setup)
            JFrame frame = new JFrame("Test Placement Management UI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            frame.add(new PlacementTestPanel());

            frame.setVisible(true);
        });
    }
}
