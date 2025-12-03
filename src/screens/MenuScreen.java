package screens;

import core.ScreenManager;
import core.SuperScreen;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MenuScreen extends SuperScreen{
    private JPanel panel;
    private final ScreenManager manager;
    private ArrayList<String[]> medicationList=new ArrayList<>();

    public MenuScreen(JFrame window,ScreenManager manager){
        super(window);
        this.manager=manager;

        panel=new JPanel(new BorderLayout());
        // existing buttons
        JButton startButton=new JButton("Contacts Screen");
        JButton importMedFileButton=new JButton("Import Medication File");
        JButton exitButton=new JButton("Exit");
        // new button for UC01
        JButton notificationsBtn=new JButton("Customer Notifications (UC01)");
        JButton medicalPageButton=new JButton("Medical Page (UC02)");

        notificationsBtn.addActionListener(e->manager.push(new CustomerNotificationsScreen(getWindow(),manager,medicationList)));
        medicalPageButton.addActionListener(e->manager.push(new MedicalPageScreen(getWindow(),manager)));
        startButton.addActionListener(e->manager.push(new ContactsScreen(getWindow(),manager,medicationList)));

        importMedFileButton.addActionListener(e->{
            JFileChooser fileChooser=new JFileChooser();
            int returnValue=fileChooser.showOpenDialog(null);
            if(returnValue==JFileChooser.APPROVE_OPTION){
                File selectedFile=fileChooser.getSelectedFile();
                medicationList=parseMedFile(selectedFile);
                for(String[] med:medicationList){
                    System.out.println("Medication: "+med[0]+", Doctor: "+med[1]+
                            ", Dosage: "+med[2]+", Time Interval: "+med[3]+", Doctor Phone Number: "+med[4]+", Doctor Email: "+med[5]);
                }
            }
        });

        JPanel topPanel=new JPanel(new GridLayout(1,2,10,0));
        topPanel.add(notificationsBtn);
        topPanel.add(medicalPageButton);

        panel.add(topPanel,BorderLayout.NORTH);
        panel.add(startButton,BorderLayout.CENTER);
        panel.add(importMedFileButton,BorderLayout.EAST);
        panel.add(exitButton,BorderLayout.SOUTH);
    }

    @Override
    public void onEnter(){
        System.out.println("Entering MenuScreen");
        getWindow().setContentPane(panel);
        getWindow().revalidate();
    }

    @Override
    public void onExit(){
        System.out.println("Exiting MenuScreen");
    }

    @Override
    public JPanel getPanel(){
        return panel;
    }

    public ArrayList<String[]> parseMedFile(File medFile){
        ArrayList<String[]> medList=new ArrayList<>();
        try(BufferedReader br=new BufferedReader(new FileReader(medFile))){
            String line;
            while((line=br.readLine())!=null){
                line=line.trim();
                if(line.isEmpty())continue;
                String[] fields=line.split(",");
                if(fields.length>=6){
                    String[] medData=new String[6];
                    medData[0]=fields[0].trim();
                    medData[1]=fields[1].trim();
                    medData[2]=fields[2].trim();
                    medData[3]=fields[3].trim();
                    medData[4]=fields[4].trim();
                    medData[5]=fields[5].trim();
                    medList.add(medData);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        if(!medList.isEmpty())medList.remove(0);
        return medList;
    }
}