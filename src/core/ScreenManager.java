package core;

import javax.swing.*;
import java.util.Stack;

public class ScreenManager {
    private final JFrame window;
    private final Stack<SuperScreen> screens = new Stack<>();

    public ScreenManager(JFrame window) {
        this.window = window;
    }

    public void push(SuperScreen screen) {
        if (!screens.isEmpty()) {
            screens.peek().onExit();
            window.remove(screens.peek().getPanel());
        }
        screens.push(screen);
        window.setContentPane(screen.getPanel());
        screen.onEnter();
        window.revalidate();
        window.repaint();
    }


    public void pop() {
        if (!screens.isEmpty()) {
            screens.peek().onExit();
            window.remove(screens.peek().getPanel());
        }
        if (!screens.isEmpty()) {
            screens.pop();
            SuperScreen screen = screens.peek();
            window.setContentPane(screen.getPanel());
            screen.onEnter();
            window.revalidate();
            window.repaint();
        }
    }

    public SuperScreen getCurrentScreen() {
        return screens.isEmpty() ? null : screens.peek();
    }

}