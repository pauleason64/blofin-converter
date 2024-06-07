package com.blofinconv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class FileConverter {
    private DefaultTableModel tableModel;
    private FileHandler handler=new FileHandler();
    private JTable table;
    private FileHandler.FileAndData handledFile=new FileHandler.FileAndData();
    private String defaultPath = "";
    private String sourceHeader = "Underlying Asset,Order Time,Side,Avg Fill,Price,Filled,Total,Fee,Order Options,Status";
    private int[] sourceHeaderWidths = {150, 300, 30, 150, 150, 150, 150, 150, 150, 30, 30};
    private String destHeader = "Koinly Date,Pair,Side,Amount,Total,Fee Amount,Fee Currency,Order ID,Trade ID";
    private String stTxt = "Status:";
    private String impText = "Import file";
    private JTextField statusBar = new JTextField(stTxt,30);

    JPanel topPanel;
    JPanel centrePanel;
    JPanel bottomPanel;
    // Placeholder class for file conversion logic

    public void run(String[] args) {

        if (args!=null && args[0].startsWith("-dp")) {
            defaultPath=args[0].substring(3);
        }
        JFrame frame = new JFrame("File Converter");
        frame.setLayout(new BorderLayout());
        frame.setSize(new Dimension(1200, 700));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topPanel = configureTopPanel(frame);
        frame.add(topPanel, BorderLayout.PAGE_START);
        centrePanel = configureCentrePanel(frame);
        frame.add(centrePanel, BorderLayout.CENTER);
        bottomPanel = configureBottomPanel(frame);
        frame.add(bottomPanel, BorderLayout.PAGE_END);
        //frame.pack();
        frame.setVisible(true);
        System.out.println("width:" + String.valueOf(frame.getWidth()));
    }

    public JPanel configureTopPanel(JFrame frame) {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(50, 100, 150));
        JLabel msg = new JLabel("Selected File:");
        JTextField txtFileName = new JTextField(40);
        JButton importButton = new JButton(impText);
        JButton cancelButton = new JButton("Cancel");
        JCheckBox overwrite = new JCheckBox("overwrite ");
        JLabel srcTableLabel = new JLabel("Source file:");

        topPanel.add(msg);
        topPanel.add(txtFileName);
        topPanel.add(importButton);
        topPanel.add(cancelButton);
        topPanel.setVisible(true);

        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (handledFile.outRows.size()>0) {
                    String[] options = new String[]{"Merge files", "Save old file 1st", "Cancel"};
                    int result = JOptionPane.showOptionDialog(null, "Previous file not saved", "Alert",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, options, options[2]);

                    if (result == JOptionPane.DEFAULT_OPTION || result == JOptionPane.CLOSED_OPTION || result ==2) {
                        return;
                    } else if (result==1 ) {

                        saveChanges(frame);
                        handledFile.outRows = new ArrayList<>();
                        handledFile.srcRows = new ArrayList<>();
                    }
                }
                //now prompt to load file
                JFileChooser fileChooser = new JFileChooser(defaultPath);
                String newPath;
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    newPath=selectedFile.getParent();
                    if (!newPath.equals(defaultPath)) {
                        //dialog
                        defaultPath=newPath;
                    }
                    txtFileName.setText(selectedFile.getAbsolutePath());
                    setStatus("Ready");
                    statusBar.setForeground(Color.BLACK);
                    importButton.setText(impText);
                }
                handledFile =handler.loadFile(txtFileName.getText());
                handler.convertTradeFile(handledFile,true);
                handledFile.lastFilename=txtFileName.getText();
                ArrayList<String> fileRows=handledFile.outRows;
                String row = "";
                String[] record = new String[11];
                for (int i = handledFile.fileType== FileHandler.BLOFIN ? 0: 1; i < fileRows.size(); i++) {
                    record = fileRows.get(i).split(",");
                    tableModel.addRow(new Object[]{""});
                    for (int j = 0; j < record.length; j++) {
                        tableModel.setValueAt(record[j], tableModel.getRowCount() - 1, j + 1);
                        }
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtFileName.setText("");
                importButton.setText(impText);
                setStatus("Ready");
                statusBar.setForeground(Color.BLACK);
            }
        });

        return topPanel;
    }

    public JPanel configureCentrePanel(JFrame frame) {
        JPanel centrePanel = new JPanel(new BorderLayout());
        centrePanel.setBackground(new Color(50, 50, 50));
        // Create the table model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing for all columns except the first (delete button)
                return column != 0;
            }
        };
        //scrollPane.setSize(new Dimension(1184,frame.getHeight()-80));
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        tableModel.addColumn("Delete");
        ButtonColumn buttonColumn = new ButtonColumn(table, deleteButton, 0);
//    buttonColumn.setMnemonic(KeyEvent.VK_D);

        //table.setPreferredSize(new Dimension(frame.getWidth(),frame.getHeight()));
        // Add a column for the delete button
        //  table.getColumnModel().getColumn(0).setPreferredWidth(sourceHeaderWidths[0]);
        String[] cols = FileHandler.TRADECOLS;
        for (int i = 0; i < cols.length - 2; i++) {
            tableModel.addColumn(cols[i].trim());
            //    table.getColumnModel().getColumn(0).setPreferredWidth(sourceHeaderWidths[i+1]);

        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setVisible(true);
        //table.setSize(1176,frame.getHeight()-80);
        setJTableColumnsWidth(table, 1160, 5, 15, 15, 5, 15, 10, 10, 10, 10);

        // Add the table to a scroll pane
        //centrePanel.add(srcTableLabel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVisible(true);
        centrePanel.add(scrollPane, BorderLayout.CENTER);
        return centrePanel;
    }

    public JPanel configureBottomPanel(JFrame frame) {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(200, 100, 50));
        statusBar.setBackground(new Color(200, 100, 50));
        statusBar.setForeground(new Color(255, 255, 255));
        statusBar.setMinimumSize(new Dimension(300, 20));
        bottomPanel.add(statusBar);

        // Add the "Add Row" button
        JButton addRowButton = new JButton("Add Row");
        addRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBlankRow();
            }
        });

        //centrePanel.add(addRowButton);
        bottomPanel.add(addRowButton);
        bottomPanel.setBackground(new Color(200, 100, 50));

        // Add the "Save changes" button
        JButton saveButton = new JButton("Save changes");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });
        bottomPanel.add(saveButton);
        //bottomPanel.setSize(new Dimension(frame.getSize().width,100));
        bottomPanel.setVisible(true);
        return bottomPanel;
    }

//    public String doConversion(String filePath, boolean overwrite) {
//
//        return new FileHandler().convert(filePath, overwrite);
//    }

    public void setStatus(String txt) {
        statusBar.setText(String.join(stTxt, txt));
    }

    private void saveChanges(JFrame frame) {
        handledFile.isMultipart=false;
        JFileChooser fileChooser = new JFileChooser(defaultPath);
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            handler.writeFile(handledFile);
        }
        // Get the modified data from the table model
        // Write it to a new CSV file (e.g., "original.csv.changed.csv")
        // Remember to handle exceptions and file paths appropriately
        // ...

        // Example: Save to "original.csv.changed.csv"
//        File outputFile = new File("original.csv.changed.csv");
//        try (Writer writer = new FileWriter(outputFile);
//             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
//            for (int row = 0; row < tableModel.getRowCount(); row++) {
//                csvPrinter.printRecord(tableModel.getDataVector().elementAt(row));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    Action deleteButton = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            JTable table = (JTable) e.getSource();
            int modelRow = Integer.valueOf(e.getActionCommand());
            ((DefaultTableModel) table.getModel()).removeRow(modelRow);
        }
    };

    private void addBlankRow() {
        // Add a blank row to the table
        tableModel.addRow(new Object[tableModel.getColumnCount()]);
        // Set focus to the first editable cell
        table.requestFocus();
        table.changeSelection(tableModel.getRowCount() - 1, 1, false, false);
    }

    public static void setJTableColumnsWidth(JTable table, int tablePreferredWidth,
                                             double... percentages) {
        double total = 0;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            total += percentages[i];
        }

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth((int)
                    (tablePreferredWidth * (percentages[i] / total)));
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileConverter().run(args));
    }
}
