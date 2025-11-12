package screens;
import core.ScreenManager;
import core.SuperScreen;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ContactsScreen extends SuperScreen {
    private final ScreenManager manager;
    private final JPanel root=new JPanel();
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private final List<Contact> contacts=new ArrayList<>();
    private final List<String[]> medicationList;

    public ContactsScreen(JFrame window,ScreenManager manager,List<String[]> medicationList){
        super(window);
        this.manager=manager;
        this.medicationList=medicationList!=null?medicationList:new ArrayList<>();
        buildUI();
        window.setTitle("Medication Trackers - Contacts");
        seedFromMedicalRecords();
        refreshTable();
    }

    @Override
    public void onEnter(){
        // Implementation of actions to perform when entering the ContactsScreen
        getWindow().setContentPane(root);
        getWindow().revalidate();
    }

    @Override
    public void onExit(){
        // Implementation of actions to perform when exiting the ContactsScreen
    }

    @Override
    public JPanel getPanel(){
        // Return the main panel for ContactsScreen
        return root;
    }

    public void buildUI(){
        root.setLayout(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JPanel topPanel=createTopPanel();
        root.add(topPanel,BorderLayout.NORTH);
        JPanel centerPanel=createCenterPanel();
        root.add(centerPanel,BorderLayout.CENTER);
        JPanel bottomPanel=createBottomPanel();
        root.add(bottomPanel,BorderLayout.SOUTH);
    }

    // Implementation of UI building for ContactsScreen
    private JPanel createTopPanel(){
        JPanel p=new JPanel(new BorderLayout(8,8));
        JLabel title=new JLabel("Contacts");
        title.setFont(title.getFont().deriveFont(Font.BOLD,18f));
        p.add(title,BorderLayout.WEST);
        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        searchField=new JTextField(22);
        JButton searchBtn=new JButton("Search");
        JButton clearBtn=new JButton("Clear");
        searchBtn.addActionListener(e->filterTable());
        clearBtn.addActionListener(e->{searchField.setText("");filterTable();});
        right.add(new JLabel("Search:"));
        right.add(searchField);
        right.add(searchBtn);
        right.add(clearBtn);
        p.add(right,BorderLayout.EAST);
        return p;
    }

    private JPanel createCenterPanel(){
        JPanel p=new JPanel(new BorderLayout());
        String[] cols={"Name","Role","Phone","Medication"};
        model=new DefaultTableModel(cols,0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        table=new JTable(model);
        table.setRowHeight(24);
        p.add(new JScrollPane(table),BorderLayout.CENTER);
        return p;
    }

    private JPanel createBottomPanel(){
        JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        JButton add=new JButton("New Contact");
        JButton edit=new JButton("Edit");
        JButton del=new JButton("Delete");
        JButton back=new JButton("Back");
        add.addActionListener(e->onAdd());
        edit.addActionListener(e->onEdit());
        del.addActionListener(e->onDelete());
        back.addActionListener(e->manager.pop());
        p.add(add);p.add(edit);p.add(del);p.add(back);
        return p;
    }

    private void seedFromMedicalRecords(){
        Set<String> seen=new HashSet<>();
        for(String[] row:medicationList){
            if(row==null||row.length<6)continue;
            String med=nn(row[0]);
            String doc=nn(row[1]);
            String phone=nn(row[4]);
            String key=phone.replaceAll("\\D","");
            if(key.isEmpty()||seen.contains(key))continue;
            seen.add(key);
            String name=doc.isEmpty()?"Prescribing Physician":doc;
            contacts.add(new Contact(name,"Physician",phone,med));
        }
    }

    private void onAdd(){
        ContactForm f=new ContactForm(getWindow(),null);
        Contact c=f.showDialog();
        if(c==null)return;
        String err=validateContact(c,null);
        if(err!=null){
            JOptionPane.showMessageDialog(getWindow(),err,"Validation Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        contacts.add(c);
        refreshTable();
    }

    private void onEdit(){
        int viewRow=table.getSelectedRow();
        if(viewRow<0){
            JOptionPane.showMessageDialog(getWindow(),"Please select a contact.","Info",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int idx=table.convertRowIndexToModel(viewRow);
        Contact cur=contacts.get(idx);
        ContactForm f=new ContactForm(getWindow(),cur);
        Contact upd=f.showDialog();
        if(upd==null)return;
        String err=validateContact(upd,cur);
        if(err!=null){
            JOptionPane.showMessageDialog(getWindow(),err,"Validation Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        cur.copyFrom(upd);
        refreshTable();
    }

    private void onDelete(){
        int viewRow=table.getSelectedRow();
        if(viewRow<0){
            JOptionPane.showMessageDialog(getWindow(),"Please select a contact.","Info",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int idx=table.convertRowIndexToModel(viewRow);
        int ok=JOptionPane.showConfirmDialog(getWindow(),"Delete selected contact?","Confirm",JOptionPane.YES_NO_OPTION);
        if(ok==JOptionPane.YES_OPTION){
            contacts.remove(idx);
            refreshTable();
        }
    }

    private void refreshTable(){
        model.setRowCount(0);
        for(Contact c:contacts)model.addRow(new Object[]{c.name,c.role,c.phone,c.medication});
        filterTable();
    }

    private void filterTable(){
        String q=searchField==null?"":searchField.getText().trim().toLowerCase();
        model.setRowCount(0);
        for(Contact c:contacts){
            String hay=(c.name+" "+c.role+" "+c.phone+" "+c.medication).toLowerCase();
            if(q.isEmpty()||hay.contains(q))model.addRow(new Object[]{c.name,c.role,c.phone,c.medication});
        }
    }

    private String validateContact(Contact c,Contact self){
        if(c.name.isBlank())return"Name is required.";
        if(c.medication.isBlank())return"Medication is required.";
        String digits=c.phone.replaceAll("\\D","");
        if(digits.length()<10||digits.length()>15)return"Phone must have 10–15 digits.";
        for(Contact other:contacts){
            if(other==self)continue;
            String d2=other.phone.replaceAll("\\D","");
            if(!digits.isEmpty()&&digits.equals(d2))return"Contact already exists with this phone.";
        }
        return null;
    }

    private static String nn(String s){return s==null?"":s.trim();}

    private static class Contact{
        String name;String role;String phone;String medication;
        Contact(String name,String role,String phone,String medication){
            this.name=nn(name);this.role=nn(role);this.phone=nn(phone);this.medication=nn(medication);
        }
        void copyFrom(Contact o){
            this.name=o.name;this.role=o.role;this.phone=o.phone;this.medication=o.medication;
        }
    }

    private static class ContactForm{
        private final JDialog dlg;
        private final JTextField name=new JTextField(22);
        private final JComboBox<String> role=new JComboBox<>(new String[]{"Physician","Pharmacy","Other"});
        private final JTextField phone=new JTextField(18);
        private final JTextField medication=new JTextField(22);
        private Contact result;

        ContactForm(JFrame owner,Contact existing){
            dlg=new JDialog(owner,existing==null?"New Contact":"Edit Contact",true);
            JPanel form=new JPanel(new GridBagLayout());
            GridBagConstraints g=new GridBagConstraints();
            g.insets=new Insets(4,4,4,4);
            g.anchor=GridBagConstraints.WEST;
            g.fill=GridBagConstraints.HORIZONTAL;
            int r=0;
            g.gridx=0;g.gridy=r;form.add(new JLabel("Name *"),g);
            g.gridx=1;form.add(name,g);r++;
            g.gridx=0;g.gridy=r;form.add(new JLabel("Role *"),g);
            g.gridx=1;form.add(role,g);r++;
            g.gridx=0;g.gridy=r;form.add(new JLabel("Phone *"),g);
            g.gridx=1;form.add(phone,g);r++;
            g.gridx=0;g.gridy=r;form.add(new JLabel("Medication *"),g);
            g.gridx=1;form.add(medication,g);r++;
            JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton back=new JButton("Back");
            JButton confirm=new JButton("Confirm");
            back.addActionListener(e->{result=null;dlg.dispose();});
            confirm.addActionListener(e->{
                result=new Contact(name.getText(),Objects.toString(role.getSelectedItem(),""),phone.getText(),medication.getText());
                dlg.dispose();
            });
            buttons.add(back);buttons.add(confirm);
            if(existing!=null){
                name.setText(existing.name);
                role.setSelectedItem(existing.role);
                phone.setText(existing.phone);
                medication.setText(existing.medication);
            }
            JPanel outer=new JPanel(new BorderLayout(10,10));
            outer.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            outer.add(form,BorderLayout.CENTER);
            outer.add(buttons,BorderLayout.SOUTH);
            dlg.setContentPane(outer);
            dlg.pack();
            dlg.setLocationRelativeTo(owner);
        }
        Contact showDialog(){
            dlg.setVisible(true);
            return result;
        }
    }
}