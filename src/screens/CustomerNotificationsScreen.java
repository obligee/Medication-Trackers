package screens;

import core.SuperScreen;
import core.ScreenManager;
import core.NotificationValidator;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomerNotificationsScreen extends SuperScreen {
    private final ScreenManager manager;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    // screen 1 controls
    private JLabel fileLabel;
    private JRadioButton allowRb;
    private JRadioButton disableRb;
    // screen 2 controls
    private JTable medsTable;
    private DefaultTableModel medsModel;
    // screen 3 controls
    private JTextArea summaryBox;
    public CustomerNotificationsScreen(JFrame window, ScreenManager manager) {
        super(window);
        this.manager = manager;
        buildUI();
        window.setTitle("Medication Trackers — Customer Notifications");
    }
    // ui
    private void buildUI() {
        root.add(buildScreen1(), "s1");
        root.add(buildScreen2(), "s2");
        root.add(buildScreen3(), "s3");
    }

    private JPanel buildScreen1() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel h = new JLabel("Upload Medical Information & Choose Notifications");
        h.setFont(h.getFont().deriveFont(Font.BOLD, 18f));
        p.add(h, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // upload row
        JPanel up = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton uploadBtn = new JButton("Upload Medical File (PDF)...");
        uploadBtn.addActionListener(e -> onUpload());
        fileLabel = new JLabel("No file selected");
        up.add(uploadBtn); up.add(fileLabel);
        center.add(up);

        // allow and disable
        center.add(new JLabel("Do you want to receive notifications?"));
        allowRb = new JRadioButton("Allow Notifications");
        disableRb = new JRadioButton("Disable Notifications");
        ButtonGroup g = new ButtonGroup(); g.add(allowRb); g.add(disableRb);
        center.add(allowRb); center.add(disableRb);

        p.add(center, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> manager.pop());
        JButton cont = new JButton("Continue");
        cont.addActionListener(e -> onContinueFromScreen1());
        actions.add(back); actions.add(cont);
        p.add(actions, BorderLayout.SOUTH);

        JPanel outer = new JPanel(new BorderLayout()); outer.add(p, BorderLayout.CENTER);
        return outer;
    }
    private JPanel buildScreen2() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        JLabel h = new JLabel("Select Notifications Per Medication");
        h.setFont(h.getFont().deriveFont(Font.BOLD, 18f));
        p.add(h, BorderLayout.NORTH);
        String[] cols = {"Medication Name", "Intake Time", "Notify? (Yes/No)"};
        medsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 2; }
        };
        medsTable = new JTable(medsModel);
        medsTable.setRowHeight(26);
        JComboBox<String> choice = new JComboBox<>(new String[]{"—", "Yes", "No"});
        medsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(choice));
        p.add(new JScrollPane(medsTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Back");
        back.addActionListener(e -> cards.show(getPanel(), "s1"));
        JButton confirm = new JButton("Confirm");
        confirm.addActionListener(e -> onConfirm());
        actions.add(back); actions.add(confirm);
        p.add(actions, BorderLayout.SOUTH);

        JPanel outer = new JPanel(new BorderLayout()); outer.add(p, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildScreen3() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel h = new JLabel("Summary");
        h.setFont(h.getFont().deriveFont(Font.BOLD, 18f));
        p.add(h, BorderLayout.NORTH);

        summaryBox = new JTextArea(12, 60);
        summaryBox.setEditable(false);
        p.add(new JScrollPane(summaryBox), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton done = new JButton("Done");
        done.addActionListener(e -> manager.pop());
        actions.add(done);
        p.add(actions, BorderLayout.SOUTH);

        JPanel outer = new JPanel(new BorderLayout()); outer.add(p, BorderLayout.CENTER);
        return outer;
    }
    // behabior
    private void onUpload() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(getWindow());
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            fileLabel.setText(f.getName());
        }
    }
    private void onContinueFromScreen1() {
        if (!allowRb.isSelected() && !disableRb.isSelected()) {
            JOptionPane.showMessageDialog(getWindow(),
                    "Please choose Allow or Disable Notifications.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (disableRb.isSelected()) {
            // A01: disable path – finish or go back
            int opt = JOptionPane.showOptionDialog(getWindow(),
                    "Notifications will be disabled. Finish or go Back?",
                    "Disable Notifications",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, new String[]{"Finish", "Back"}, "Finish");
            if (opt == 0) {
                summaryBox.setText("Notifications: DISABLED\nFile: " + fileLabel.getText());
                cards.show(getPanel(), "s3");
            }
            return;
        }
        // Allow → load a tiny mock list (PRO02)
        populateMockMeds();
        cards.show(getPanel(), "s2");
    }
    private void populateMockMeds() {
        medsModel.setRowCount(0);
        medsModel.addRow(new Object[]{"Atorvastatin 20 mg", "08:00", "—"});
        medsModel.addRow(new Object[]{"Metformin 500 mg",   "20:00", "—"});
        medsModel.addRow(new Object[]{"Lisinopril 10 mg",   "09:00", "—"});
    }
    private void onConfirm() {
        if (medsTable.isEditing()) {
            TableCellEditor ed = medsTable.getCellEditor();
            if (ed != null) ed.stopCellEditing();
        }
        //validator
        List<String> picks = new ArrayList<>();
        for (int r = 0; r < medsModel.getRowCount(); r++) {
            Object v = medsModel.getValueAt(r, 2);
            picks.add(v == null ? "" : v.toString());
        }
        // BR01/E01:all must be Yes or No
        if (!NotificationValidator.allSelectedYesOrNo(picks)) {
            for (int r = 0; r < medsModel.getRowCount(); r++) {
                Object v = medsModel.getValueAt(r, 2);
                String s = v == null ? "" : v.toString().trim();
                if (!s.equalsIgnoreCase("Yes") && !s.equalsIgnoreCase("No")) {
                    JOptionPane.showMessageDialog(getWindow(),
                            "Yes or No must be selected to Confirm!\nMissing for: " +
                                    medsModel.getValueAt(r, 0),
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        // PRO03 complete
        StringBuilder sb = new StringBuilder();
        sb.append("Notifications: ENABLED\nFile: ").append(fileLabel.getText()).append("\n\n");
        for (int r = 0; r < medsModel.getRowCount(); r++) {
            sb.append("- ").append(medsModel.getValueAt(r,0))
                    .append(" @ ").append(medsModel.getValueAt(r,1))
                    .append(" → ").append(medsModel.getValueAt(r,2)).append("\n");
        }
        summaryBox.setText(sb.toString());
        cards.show(getPanel(), "s3");
    }

    @Override public void onEnter() { getWindow().setContentPane(root); getWindow().revalidate(); }
    @Override public void onExit()  {}
    @Override public JPanel getPanel() { return root; }
}