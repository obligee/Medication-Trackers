package screens;
import core.ScreenManager;
import core.SuperScreen;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class MenuScreen extends SuperScreen {
    private JPanel panel;
    private final ScreenManager manager;
    private ArrayList<String[]> medicationList=new ArrayList<>();

    public MenuScreen(JFrame window,ScreenManager manager){
        super(window);
        this.manager=manager;
        panel=new JPanel(new BorderLayout());

        // ✅ new button for UC01
        JButton notificationsBtn=new JButton("Customer Notifications (UC01)");
        notificationsBtn.addActionListener(e->manager.push(new CustomerNotificationsScreen(getWindow(),manager,medicationList)));

        // existing buttons…
        JButton startButton=new JButton("Contacts Screen");
        startButton.addActionListener(e->manager.push(new ContactsScreen(getWindow(),manager,medicationList)));

        JButton importMedFileButton=new JButton("Import Medication File");
        importMedFileButton.addActionListener(e->{
            JFileChooser fileChooser=new JFileChooser();
            int returnValue=fileChooser.showOpenDialog(getWindow());
            if(returnValue==JFileChooser.APPROVE_OPTION){
                File selectedFile=fileChooser.getSelectedFile();
                medicationList=parseMedFile(selectedFile);
                JOptionPane.showMessageDialog(getWindow(),"Imported "+medicationList.size()+" medication rows.");
                for(String[] med:medicationList){
                    System.out.println("Medication:"+med[0]+", Doctor:"+med[1]+", Dosage:"+med[2]+", Time:"+med[3]+", Phone:"+med[4]+", Email:"+med[5]);
                }
            }
        });

        JButton exitButton=new JButton("Exit");
        exitButton.addActionListener(e->System.exit(0));

        JPanel leftStrip=new JPanel();
        leftStrip.setLayout(new BoxLayout(leftStrip,BoxLayout.Y_AXIS));
        leftStrip.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        leftStrip.add(notificationsBtn);
        leftStrip.add(Box.createVerticalStrut(8));
        leftStrip.add(startButton);
        leftStrip.add(Box.createVerticalStrut(8));
        leftStrip.add(importMedFileButton);
        leftStrip.add(Box.createVerticalStrut(8));
        leftStrip.add(exitButton);
        panel.add(leftStrip,BorderLayout.WEST);
    }

    @Override public void onEnter(){
        System.out.println("Entering MenuScreen");
        getWindow().setContentPane(panel);
        getWindow().revalidate();
    }

    @Override public void onExit(){
        System.out.println("Exiting MenuScreen");
    }

    @Override public JPanel getPanel(){return panel;}

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
            JOptionPane.showMessageDialog(getWindow(),"Failed to read file: "+e.getMessage(),"Import Error",JOptionPane.ERROR_MESSAGE);
        }
        if(!medList.isEmpty()){
            String first=medList.get(0)[0].toLowerCase();
            if(first.contains("medication")||first.contains("med"))medList.remove(0);
        }
        return medList;
    }
}