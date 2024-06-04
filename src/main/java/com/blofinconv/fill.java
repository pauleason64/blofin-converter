package com.blofinconv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

    public class fill {
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("JTable Example");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);

                // Create a sample data model with 5 columns
                Vector<String> columnNames = new Vector<>();
                columnNames.add("Column 1");
                columnNames.add("Column 2");
                columnNames.add("Column 3");
                columnNames.add("Column 4");
                columnNames.add("Column 5");

                Vector<Vector<Object>> data = new Vector<>();
                // Add sample data rows (you can replace this with your actual data)
                for (int i = 0; i < 10; i++) {
                    Vector<Object> row = new Vector<>();
                    row.add("Row " + (i + 1));
                    row.add("Value 1");
                    row.add("Value 2");
                    row.add("Value 3");
                    row.add("Value 4");
                    data.add(row);
                }

                // Create the JTable
                JTable table = new JTable(new DefaultTableModel(data, columnNames));
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Prevent automatic column resizing

                // Set preferred width for each column
                for (int i = 0; i < columnNames.size(); i++) {
                    table.getColumnModel().getColumn(i).setPreferredWidth(150);
                }

                // Add the JTable to a JScrollPane
                JScrollPane scrollPane = new JScrollPane(table);
                frame.add(scrollPane);

                // Make the JTable fill the frame
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

                // Show the frame
                frame.setVisible(true);
            });
        }
    }


