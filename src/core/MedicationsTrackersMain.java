package core;

import javax.swing.*;

public class MedicationsTrackersMain {
    private JFrame window;
    private ScreenManager manager;

    public MedicationsTrackersMain() {
        window = new JFrame("Medication Trackers");
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        manager = new ScreenManager(window);
        //manager.push(new MenuScreen(window, manager));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MedicationsTrackersMain::new);
    }

}