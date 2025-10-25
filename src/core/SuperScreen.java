package core;


import javax.swing.*;


public abstract class SuperScreen {
    protected JFrame window;

    public SuperScreen(JFrame window) {
        this.window = window;
    }

    public abstract void onEnter();
    public abstract void onExit();
    public abstract JPanel getPanel();
}