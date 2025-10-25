package screens;


import core.SuperScreen;
import core.ScreenManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuScreen extends SuperScreen {
    private JPanel panel;
    private ScreenManager manager;

    public MenuScreen(JFrame window, ScreenManager manager) {
        super(window);
        this.manager = manager;

        panel = new JPanel(new BorderLayout());
        JButton startButton = new JButton("Contacts Screen");
        JButton exitButton = new JButton("Exit");

        panel.add(startButton, BorderLayout.CENTER);
        panel.add(exitButton, BorderLayout.SOUTH);
    }

    @Override
    public void onEnter() {
        System.out.println("Entering MenuScreen");
    }

    @Override
    public void onExit() {
        System.out.println("Exiting MenuScreen");
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}