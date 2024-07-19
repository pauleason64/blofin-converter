package com.peason.blofin2koinly;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.logging.*;

public class FileConverter {
    static Logger logger = Logger.getLogger(FileConverter.class.getName());
    static String separator = FileSystems.getDefault().getSeparator();
    static boolean batchMode = false;
    static boolean batchMergeOutput =false;
    static boolean ignoreNonEmptyDestFolder = false;
    static boolean deleteFileAfterCopy = false;
    static String defaultSourceFilePath = "";
    static String defaultDestFilePath = "";
    static String defaultLogFilePath ="";
    static String mergeFileName= "blofin-merged.csv";

    private DefaultTableModel tableModel;
    private FileHandler handler=new FileHandler();
    private JTextField txtFileName;
    private JTable table;
    private FileHandler.FileAndData handledFileAndData =new FileHandler.FileAndData();
    private int[] sourceHeaderWidths = {150, 300, 30, 150, 150, 150, 150, 150, 150, 30, 30};
    private String stTxt = "Status:";
    private String impText = "Import file";
    private JTextField statusBar = new JTextField(stTxt,30);

    JPanel topPanel;
    JPanel centrePanel;
    JPanel bottomPanel;
    // Placeholder class for file conversion logic

    public void run() {

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
        frame.setVisible(true);
    }

    public JPanel configureTopPanel(JFrame frame) {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(50, 100, 150));
        JLabel msg = new JLabel("Selected File:");
        txtFileName = new JTextField(40);
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
                   if (handledFileAndData.existingSrcRows.size() > 0) {
                       String[] options = new String[]{"Merge files", "Save old file 1st", "Cancel"};
                       int result = JOptionPane.showOptionDialog(null, "Previous file not saved", "Alert",
                               JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                               null, options, options[2]);

                       if (result == JOptionPane.DEFAULT_OPTION || result == JOptionPane.CLOSED_OPTION || result == 2) {
                           return;
                       } else if (result == 1) {

                           validateAndSave(frame);
                           table = new JTable(tableModel);
                           table.invalidate();
                           table.updateUI();
                       }
                   }
                   //now prompt to load file
                   JFileChooser fileChooser = new JFileChooser(defaultSourceFilePath);
                   String newPath;
                   int result = fileChooser.showOpenDialog(frame);
                   if (result == JFileChooser.APPROVE_OPTION) {
                       File selectedFile = fileChooser.getSelectedFile();
                       newPath = selectedFile.getParent();
                       if (!newPath.equals(defaultSourceFilePath)) defaultSourceFilePath = newPath;
                       txtFileName.setText(selectedFile.getAbsolutePath());
                       setStatus("Ready");
                       statusBar.setForeground(Color.BLACK);
                       importButton.setText(impText);
                       //if user chose merge, pass the existing object to the loader
                       //todo Fix bug with using old file data and fix issue with table not clearing
                       processSelectedFile(selectedFile);
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

    private void processSelectedFile(File selectedFile) {
        if (handledFileAndData.existingSrcRows.size()==0) {
            handledFileAndData = handler.loadFile(selectedFile.getAbsolutePath());
        } else {
            handledFileAndData = handler.loadFile(selectedFile.getAbsolutePath(), handledFileAndData);
        }
        logger.info("Processing file :" + selectedFile.getAbsolutePath());
        //update the table model - this should be clean data with headers removed
        if (!batchMode) ((DefaultTableModel)table.getModel()).setRowCount(0);
        handler.convertTradeFile(handledFileAndData,true);
        handledFileAndData.lastFilename=selectedFile.getAbsolutePath();
        if (!batchMode) {
            ArrayList<String> rowsForTable = handledFileAndData.outRows;
            String row = "";
            String[] record = new String[11];
            for (int i = handledFileAndData.fileType == FileHandler.BLOFIN ? 0 : 1; i < rowsForTable.size(); i++) {
                record = rowsForTable.get(i).split(",");
                tableModel.addRow(new Object[]{""});
                for (int j = 0; j < record.length; j++) {
                    tableModel.setValueAt(record[j], tableModel.getRowCount() - 1, j + 1);
                }
            }
            if (handledFileAndData.newSrcRows.size() == 0) {
                setStatus("No qualifying records found in file:" + selectedFile.getName());
                return;
            }
        }
        handledFileAndData.existingSrcRows.addAll(handledFileAndData.newSrcRows);
        handledFileAndData.newSrcRows=new ArrayList<>();
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
//        tableModel.addColumn("Delete");
//        ButtonColumn buttonColumn = new ButtonColumn(table, deleteButton, 0);
        String[] cols = FileHandler.TRADECOLS;

        for (int i = 0; i < cols.length - 2; i++) {
            tableModel.addColumn(cols[i].trim());
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setVisible(true);
        TableUtils.setJTableColumnsWidth(table, 1160, new int[] {5, 15, 15, 5, 15, 10, 10, 10, 10});

        // Add the table to a scroll pane
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

        bottomPanel.add(addRowButton);
        bottomPanel.setBackground(new Color(200, 100, 50));

        // Add the "Save changes" button
        JButton saveButton = new JButton("Save changes");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateAndSave(frame);
                table=new JTable(tableModel);
                table.invalidate();
                table.updateUI();
            }
        });
        bottomPanel.add(saveButton);
        //bottomPanel.setSize(new Dimension(frame.getSize().width,100));
        bottomPanel.setVisible(true);
        return bottomPanel;
    }

    public void setStatus(String txt) {
        if (batchMode) {
            logger.info(stTxt);
            return;
        }
        statusBar.setText(String.join(stTxt, txt));
    }

    private void resetFileHandler() {

        handledFileAndData.isMultipart=false;
        handledFileAndData.outRows= new ArrayList<>();
        handledFileAndData.existingSrcRows =new ArrayList<>();
        handledFileAndData.newSrcRows =new ArrayList<>();
        handledFileAndData.lastFilename="";
        handledFileAndData.isDirty=false;

    }

    private int validateAndSave(JFrame frame) {
        int  result = -1, resultOK = 0, resultCancel = -2;
        String outfileName = handler.fileNameWithoutExt(handledFileAndData.lastFilename).concat("converted.csv");
        handledFileAndData.isMultipart = false;
        while (result != resultOK && result != resultCancel) {
            JFileChooser fileChooser = new JFileChooser(outfileName);
            fileChooser.setSelectedFile(new File(outfileName));
            result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.CANCEL_OPTION || result != JFileChooser.APPROVE_OPTION) {
                result = resultCancel;
                continue;
            }
            outfileName = fileChooser.getSelectedFile().getAbsolutePath();
            if (new File(outfileName).exists()) {
                result = JOptionPane.showConfirmDialog(frame, "File " + fileChooser.getSelectedFile().getName() + "exists, overwrite?",
                        "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.NO_OPTION) {
                    continue; //ask again
                } else if (result == JOptionPane.CLOSED_OPTION) {
                    result = resultCancel;
                }
            }
            //todo - handler errors
            handler.writeFile(handledFileAndData, outfileName);
            defaultDestFilePath= new File(outfileName).getParent().toString();
            logger.info("default destination path updated to: "+ defaultDestFilePath);
            setStatus(fileChooser.getSelectedFile().getName() + " Saved.");
            resetFileHandler();
            result = resultOK;
        }
        if (result == resultCancel) setStatus("Canceled. File not saved");
        return result;
    }

    private Action deleteButton = new AbstractAction() {
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



    private boolean processArgs(String[] args) {
        String arg;
        for (int i=0; i<args.length; i++) {
            arg = args[i];
            if (arg.startsWith("-b")) {
                batchMode = true;
                if (arg.contains("i")) ignoreNonEmptyDestFolder = true;
                continue;
            }
            if (arg.startsWith("-del")) {
                deleteFileAfterCopy = true;
                logger.info("deleting files after use per args:");
                continue;
            }

            if (arg.startsWith("-m")) {
                batchMergeOutput = true;
                mergeFileName = arg.length() > 2 ? arg.substring(2) : mergeFileName;
                continue;
            }

            if (arg.startsWith("-sp")) {
                defaultSourceFilePath = arg.substring(3);
                logger.info("default source from args:" + defaultSourceFilePath);
                continue;
            }
            if (arg.startsWith("-dp")) {
                defaultDestFilePath = arg.substring(3);
                logger.info("default destination from args:" + defaultDestFilePath);
                continue;

            }
            if (arg.startsWith("-lp")) {
                defaultLogFilePath = arg.substring(3);
                logger.info("default logfile path from args:" + defaultLogFilePath);
                continue;
            }
        }
        return  true;
    }

    private void processBatch() { //throws IOException
        File src = new File(defaultSourceFilePath);
        File dst = new File(defaultDestFilePath);
        logger.info("Starting new batch");
        if (!src.isDirectory()) {
            logger.severe("Source :" + src.getAbsolutePath() + " is not a directory");
            return;
        }
        if (!dst.isDirectory()) {
            logger.severe("Dest :" + dst.getAbsolutePath() + " is not a directory");
            return;
        }
        File[] srcFiles = src.listFiles();
        if (srcFiles.length == 0) {
            logger.severe("Source :" + src.getAbsolutePath() + " is empty");
            return;
        }
        File[] dstFiles = dst.listFiles();
        if (dstFiles.length > 0 && !ignoreNonEmptyDestFolder) {
            logger.severe("Dest :" + dst.getAbsolutePath() + " is not empty and ignore flag not set");
            return;
        }
        int count = 0;
        String saveFileName="";
        for (File file : srcFiles) {
            if (file.isDirectory() == false) {
                processSelectedFile(file);
                if (batchMergeOutput) { count++; continue; }
                saveFileName = new File(handledFileAndData.lastFilename).getName().concat(".koinly.csv");
                handler.writeFile(handledFileAndData, defaultDestFilePath+
                        FileSystems.getDefault().getSeparator()+saveFileName);
                if (deleteFileAfterCopy) {
                    logger.info("deleting file: " + file.getName());
                    file.delete();
                }
                resetFileHandler();
                count++;
            }
            //now write the merged file is that option was chosen
            if (batchMergeOutput) {
                handler.writeFile(handledFileAndData, defaultDestFilePath+
                        FileSystems.getDefault().getSeparator()+mergeFileName);

            }
        }
        if (count == 0) {
            logger.severe("Source :" + src.getAbsolutePath().toString() + " contained no useable files");
        } else {
            logger.info("processing of batch complete");
        }
    }

    private  void configureLogging() {

        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("b2klogging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
        logger.setLevel(Level.INFO);
        logger.addHandler(new ConsoleHandler());
        //logger.addHandler(new StreamHandler());
        try {
            //FileHandler file name with max size and number of log files limit
            String path = defaultLogFilePath.equals("") ?
                    FileSystems.getDefault().getPath("").toAbsolutePath().toString()+separator :
                    defaultLogFilePath+separator ;
            Handler fileHandler = new java.util.logging.FileHandler(path + "b2klogger.log", 2000, 5);
            fileHandler.setFormatter(new B2Klogging.Formatter());
            //setting custom filter for FileHandler
            fileHandler.setFilter(new B2Klogging.Filter());
            logger.addHandler(fileHandler);
            logger.log(Level.CONFIG, "Reading and setting logging config");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        FileConverter fileConverter = new FileConverter();
        if (args.length==0 || fileConverter.processArgs(args)) {
            //config passed
            fileConverter.configureLogging();
            if (batchMode) {
                //ensure source and destination provided
                if (defaultSourceFilePath.equals("") || defaultDestFilePath.equals("")) {
                    logger.severe("You must provide Source and destination folders for batch mode");
                    return;
                }
                fileConverter.processBatch();
                return;
            }

            logger.info("Starting application..");
            fileConverter.run();
        } else {
            logger.info("Closing application..");
        }
        return;
    }
}

