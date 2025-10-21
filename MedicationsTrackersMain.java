import java.util.Stack;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MedicationTrackersMain {
    public static void Main(String[] args) {
        JFrame mainFrame = new JFrame("Main Window");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(300, 200);

        JButton openButton = new JButton("Open New Window");
        mainFrame.add(openButton);

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame newWindow = new JFrame("New Window");
                newWindow.setSize(200, 150);
                newWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JLabel label = new JLabel("Home Screen", SwingConstants.CENTER);
                newWindow.add(label);
                newWindow.setVisible(true);
            }
        });

        mainFrame.setVisible(true);

        Stack<SuperScreen> stack = new Stack<>();
        boolean isWindowOpen = true;
        while (isWindowOpen) {
            stack.peek();
        }
    }
}