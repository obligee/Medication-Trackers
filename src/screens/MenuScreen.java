package screens;

import core.SuperScreen;
import core.ScreenManager;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


// button for screen
import screens.CustomerNotificationsScreen;
public class MenuScreen extends SuperScreen {
    private JPanel panel;
    private final ScreenManager manager;
    private ArrayList<String[]> medicationList;

    public MenuScreen(JFrame window, ScreenManager manager) {
        super(window);
        this.manager = manager;

        panel = new JPanel(new BorderLayout());
        // existing buttons…
        JButton startButton = new JButton("Contacts Screen");
        JButton exitButton  = new JButton("Exit");
        // ✅ new button for UC01
        JButton notificationsBtn = new JButton("Customer Notifications (UC01)");
        notificationsBtn.addActionListener(e ->
                manager.push(new CustomerNotificationsScreen(getWindow(), manager))
        );
        // simple layout — put UC01 on top, existing center/bottom remain
        panel.add(notificationsBtn, BorderLayout.NORTH);
        panel.add(startButton, BorderLayout.CENTER);
        panel.add(exitButton, BorderLayout.SOUTH);
    }
    @Override public void onEnter() { System.out.println("Entering MenuScreen"); getWindow().setContentPane(panel); getWindow().revalidate(); }
    @Override public void onExit()  { System.out.println("Exiting MenuScreen"); }
    @Override public JPanel getPanel() { return panel; }

    public ArrayList<String[]> parseMedFile(File medFile) {
        ArrayList<String[]> medList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(medFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                medList.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return medList;
    }
}