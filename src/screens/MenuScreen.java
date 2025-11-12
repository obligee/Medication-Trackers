package screens;

import core.SuperScreen;
import core.ScreenManager;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
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
        JButton importMedFileButton = new JButton("Import Medication File");
        JButton exitButton  = new JButton("Exit");
        // ✅ new button for UC01
        JButton notificationsBtn = new JButton("Customer Notifications (UC01)");
        notificationsBtn.addActionListener(e ->
                manager.push(new CustomerNotificationsScreen(getWindow(), manager))
        );
        // simple layout — put UC01 on top, existing center/bottom remain
        importMedFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                medicationList = parseMedFile(selectedFile);
                // For demonstration, print the parsed medication list
                for (String[] med : medicationList) {
                    System.out.println("Medication: " + med[0] + ", Doctor: " + med[1] +
                            ", Dosage: " + med[2] + ", Time Interval: " + med[3]);
                }
            }
        });
        panel.add(notificationsBtn, BorderLayout.NORTH);
        panel.add(startButton, BorderLayout.CENTER);
        panel.add(importMedFileButton, BorderLayout.EAST);
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
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }
                // Split the CSV line by comma
                String[] fields = line.split(",");
                // Ensure we have exactly 4 fields (medication, doctor, dosage, time interval)
                if (fields.length >= 4) {
                    String[] medData = new String[4];
                    medData[0] = fields[0].trim(); // medication name
                    medData[1] = fields[1].trim(); // doctor name
                    medData[2] = fields[2].trim(); // dosage
                    medData[3] = fields[3].trim(); // time interval
                    medList.add(medData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        medList.remove(0); // Remove header row
        return medList;
    }
}