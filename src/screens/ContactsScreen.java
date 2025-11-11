package screens;

import core.ScreenManager;
import core.SuperScreen;

import javax.swing.*;


public class ContactsScreen extends SuperScreen {
    private final ScreenManager manager;
    private final JPanel root = new JPanel();

    public ContactsScreen(JFrame window, ScreenManager manager) {
        super(window);
        this.manager = manager;
        buildUI();
        window.setTitle("Medication Trackers - Contacts");
    }

    public void buildUI() {
        root.setLayout(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //
        Jpanel topPanel = createTopPanel();
        root.add(topPanel, BorderLayout.NORTH)

        // Implementation of UI building for ContactsScreen

    }

    public void onEnter() {
        // Implementation of actions to perform when entering the ContactsScreen
    }

    public void onExit() {
        // Implementation of actions to perform when exiting the ContactsScreen
    }

    public JPanel getPanel() {
        // Return the main panel for ContactsScreen
        return root;
    }
}
