package core;

import java.util.Stack;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MedicationTrackersMain {
    private JFrame window;
    private ScreenManager manager;

    public MedicationTrackersMain() {
        window = new JFrame("Medication Trackers");
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        manager = new ScreenManager(Window);
        manager.push(new MenuScreen(window, manager));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MedicationTrackersMain::new);
    }
}