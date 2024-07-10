package com.peason.blofin2koinly;

import javax.swing.*;
import javax.swing.table.TableColumn;

public  final class TableUtils {

    public static void setJTableColumnsWidth(JTable table, int tablePreferredWidth,
                                             int[] percentages) {
        int total = 0;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            total += percentages[i];
        }

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth((int)
                    (tablePreferredWidth * (percentages[i] / total)));
        }
    }

}
