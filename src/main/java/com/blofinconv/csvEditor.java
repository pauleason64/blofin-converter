package com.blofinconv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
//-import org.apache.commons.csv.*;

    public class csvEditor {
        private JFrame frame;
        private JTable table;
        private DefaultTableModel tableModel;

        public csvEditor() {
            frame = new JFrame("CSV Editor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Create the table model
            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Allow editing for all columns except the first (delete button)
                    return column != 0;
                }
            };
            table = new JTable(tableModel);

            // Add a column for the delete button
            tableModel.addColumn("Delete");

            // Add the table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            // Add the "Save changes" button
            JButton saveButton = new JButton("Save changes");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveChanges();
                }
            });
            frame.add(saveButton, BorderLayout.SOUTH);

            // Add the "Add Row" button
            JButton addRowButton = new JButton("Add Row");
            addRowButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addBlankRow();
                }
            });
            frame.add(addRowButton, BorderLayout.NORTH);

            frame.pack();
            frame.setVisible(true);
        }

        private void loadCsvFile(File file) {
//            try (Reader reader = new FileReader(file);
//                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {
//                for (CSVRecord record : csvParser) {
//                    // Add a blank column for the delete button
//                    tableModel.addRow(new Object[]{""});
//                    // Populate the other columns with data from the CSV
//                    for (int i = 0; i < record.size(); i++) {
//                        tableModel.setValueAt(record.get(i), tableModel.getRowCount() - 1, i + 1);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        private void saveChanges() {
            // Get the modified data from the table model
            // Write it to a new CSV file (e.g., "original.csv.changed.csv")
            // Remember to handle exceptions and file paths appropriately
            // ...

            // Example: Save to "original.csv.changed.csv"
//            File outputFile = new File("original.csv.changed.csv");
//            try (Writer writer = new FileWriter(outputFile);
//                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
//                for (int row = 0; row < tableModel.getRowCount(); row++) {
//                    csvPrinter.printRecord(tableModel.getDataVector().elementAt(row));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        private void addBlankRow() {
            // Add a blank row to the table
            tableModel.addRow(new Object[tableModel.getColumnCount()]);
            // Set focus to the first editable cell
            table.requestFocus();
            table.changeSelection(tableModel.getRowCount() - 1, 1, false, false);
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new csvEditor());
        }
    }

