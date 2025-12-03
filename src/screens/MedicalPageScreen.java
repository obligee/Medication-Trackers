package screens;

import core.ScreenManager;
import core.SuperScreen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MedicalPageScreen extends SuperScreen{
    private final ScreenManager manager;
    private final JPanel root=new JPanel(new BorderLayout(10,10));

    private JTextField nameField;
    private JTextField addressField;
    private JTextField stateField;
    private JTextField ssnField;
    private JTextField primaryDoctorField;
    private JTextField medicationsField;

    private JLabel historyFileLabel;
    private JLabel treatmentFileLabel;

    private final List<String> historyLines=new ArrayList<>();
    private final List<String> treatmentLines=new ArrayList<>();

    private MedicalProfile savedProfile;

    public MedicalPageScreen(JFrame window,ScreenManager manager){
        super(window);
        this.manager=manager;
        buildUI();
        window.setTitle("Medication Trackers - Medical Page");
    }

    private void buildUI(){
        root.setBorder(new EmptyBorder(15,15,15,15));

        JLabel title=new JLabel("Medical Page");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD,18f));
        root.add(title,BorderLayout.NORTH);

        JPanel form=new JPanel(new GridLayout(0,2,5,5));

        nameField=new JTextField();
        addressField=new JTextField();
        stateField=new JTextField();
        ssnField=new JTextField();
        primaryDoctorField=new JTextField();
        medicationsField=new JTextField();

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Address:"));
        form.add(addressField);
        form.add(new JLabel("State:"));
        form.add(stateField);
        form.add(new JLabel("SSN (XXX-XX-XXXX):"));
        form.add(ssnField);
        form.add(new JLabel("Primary Doctor(s):"));
        form.add(primaryDoctorField);
        form.add(new JLabel("Medications:"));
        form.add(medicationsField);
        // medical history csv
        JButton historyBtn=new JButton("Upload History CSV");
        historyBtn.addActionListener(e->onUploadHistory());
        historyFileLabel=new JLabel("No file chosen");
        JPanel historyPanel=new JPanel(new BorderLayout(5,0));
        historyPanel.add(historyBtn,BorderLayout.WEST);
        historyPanel.add(historyFileLabel,BorderLayout.CENTER);
        form.add(new JLabel("Medical History CSV:"));
        form.add(historyPanel);
        // treatment plans csv
        JButton treatmentBtn=new JButton("Upload Treatment CSV");
        treatmentBtn.addActionListener(e->onUploadTreatment());
        treatmentFileLabel=new JLabel("No file chosen");
        JPanel treatmentPanel=new JPanel(new BorderLayout(5,0));
        treatmentPanel.add(treatmentBtn,BorderLayout.WEST);
        treatmentPanel.add(treatmentFileLabel,BorderLayout.CENTER);
        form.add(new JLabel("Treatment Plans CSV:"));
        form.add(treatmentPanel);
        root.add(form,BorderLayout.CENTER);
        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        JButton confirmBtn=new JButton("Confirm Information");
        JButton backBtn=new JButton("Go Back");
        confirmBtn.addActionListener(e->onConfirm());
        backBtn.addActionListener(e->onBack());
        bottom.add(confirmBtn);
        bottom.add(backBtn);
        root.add(bottom,BorderLayout.SOUTH);
    }
    private void onUploadHistory(){
        JFileChooser fc=new JFileChooser();
        int res=fc.showOpenDialog(getWindow());
        if(res==JFileChooser.APPROVE_OPTION){
            loadCsv(fc.getSelectedFile(),historyLines,historyFileLabel);
        }
    }
    private void onUploadTreatment(){
        JFileChooser fc=new JFileChooser();
        int res=fc.showOpenDialog(getWindow());
        if(res==JFileChooser.APPROVE_OPTION){
            loadCsv(fc.getSelectedFile(),treatmentLines,treatmentFileLabel);
        }
    }
    private void loadCsv(File file,List<String> target,JLabel label){
        target.clear();
        try(BufferedReader br=new BufferedReader(new FileReader(file))){
            String line;
            while((line=br.readLine())!=null){
                line=line.trim();
                if(!line.isEmpty())target.add(line);
            }
            label.setText(file.getName());
        }catch(IOException ex){
            JOptionPane.showMessageDialog(getWindow(),"Error reading file: "+ex.getMessage(),"File Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onConfirm() {
        String error = validateForm();
        if (error != null) {
            JOptionPane.showMessageDialog(
                    getWindow(),
                    "User information is invalid:\n" + error,
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // save profile (same fields you already had)
        savedProfile = new MedicalProfile(
                nameField.getText().trim(),
                addressField.getText().trim(),
                stateField.getText().trim(),
                ssnField.getText().trim(),
                primaryDoctorField.getText().trim(),
                medicationsField.getText().trim()
        );

        // build full summary
        StringBuilder summary = new StringBuilder();
        summary.append("Information saved for this user.\n\n");
        summary.append("Name: ").append(nameField.getText().trim()).append("\n");
        summary.append("Address: ").append(addressField.getText().trim()).append("\n");
        summary.append("State: ").append(stateField.getText().trim()).append("\n");
        summary.append("SSN: ").append(ssnField.getText().trim()).append("\n");
        summary.append("Primary Doctor(s): ").append(primaryDoctorField.getText().trim()).append("\n");
        summary.append("Medications: ").append(medicationsField.getText().trim()).append("\n\n");
        summary.append("Medical History CSV: ").append(historyFileLabel.getText()).append("\n");
        summary.append("Treatment Plans CSV: ").append(treatmentFileLabel.getText()).append("\n");

        JOptionPane.showMessageDialog(
                getWindow(),
                summary.toString(),
                "Medical Page",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String validateForm(){
        if(nameField.getText().trim().isEmpty())return "Name is required.";
        if(addressField.getText().trim().isEmpty())return "Address is required.";
        if(stateField.getText().trim().length()!=2)return "State must be 2 letters.";
        String ssn=ssnField.getText().trim();
        if(ssn.length() != 11) return "SSN must be 11 characters: XXX-XX-XXXX.";
        if(ssn.charAt(3) != '-' || ssn.charAt(6) != '-')
            return "SSN must contain dashes like XXX-XX-XXXX.";
        for(int i=0; i<ssn.length(); i++){
            if(i == 3 || i == 6) continue;
            if(!Character.isDigit(ssn.charAt(i)))
                return "SSN must only contain numbers except for the dashes.";
        }

        if(primaryDoctorField.getText().trim().isEmpty())return "Primary doctor is required.";
        if(medicationsField.getText().isEmpty())return "Medications field is required.";
        if(historyLines.isEmpty())return "Medical history CSV must be uploaded.";
        if(treatmentLines.isEmpty())return "Treatment plans CSV must be uploaded.";
        if(savedProfile!=null&&!savedProfile.ssn.equals(ssn))return "There can only be one SSN associated with this user.";
        return null;
    }
    private void onBack(){
        manager.pop();
    }
    @Override
    public void onEnter(){
        getWindow().setContentPane(root);
        getWindow().revalidate();
    }
    @Override
    public void onExit(){
        // the actions that perform when exiting the MedicalPageScreen
    }
    @Override
    public JPanel getPanel(){
        // Return the main panel for MedicalPageScreen
        return root;
    }
    private static class MedicalProfile{
        final String name;
        final String address;
        final String state;
        final String ssn;
        final String primaryDoctor;
        final String medications;
        MedicalProfile(String name,String address,String state,String ssn,String primaryDoctor,String medications){
            this.name=name;
            this.address=address;
            this.state=state;
            this.ssn=ssn;
            this.primaryDoctor=primaryDoctor;
            this.medications=medications;
        }
    }
}