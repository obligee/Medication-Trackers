package core;

import javax.swing.*;


public abstract class SuperScreen {
    private final JFrame window;

    public SuperScreen(JFrame window) {
        this.window = window;
    }

    // ✅ needed by other screens (e.g., notifications)
    public JFrame getWindow() {
        return window;
    }

    public abstract void onEnter();
    public abstract void onExit();
    public abstract JPanel getPanel();
}