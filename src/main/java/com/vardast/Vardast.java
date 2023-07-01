package com.vardast;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

public class Vardast extends JFrame {
    private List<Task> tasks;
    private List<Task> doneTasks;
    private DefaultTableModel tableModel;
    private DefaultTableModel doneTableModel;
    private JTable timetableTable;
    private JTable doneTable;
    private JTextField taskNameField;
    private JComboBox<String> priorityComboBox;
    private JButton addButton;
    private JButton deleteButton;
    private JButton sortButton;
    private JButton clearButton;
    private JButton doneButton;
    private JTabbedPane tabbedPane;

    public Vardast() {
        setTitle("Vardast");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tasks = new ArrayList<>();
        doneTasks = new ArrayList<>();

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel taskNameLabel = new JLabel("Task Name:");
        inputPanel.add(taskNameLabel);

        taskNameField = new JTextField(15);
        inputPanel.add(taskNameField);

        JLabel priorityLabel = new JLabel("Priority:");
        inputPanel.add(priorityLabel);

        priorityComboBox = new JComboBox<>();
        priorityComboBox.addItem("High");
        priorityComboBox.addItem("Medium");
        priorityComboBox.addItem("Low");
        inputPanel.add(priorityComboBox);

        addButton = new JButton("Add Task");
        addButton.setFocusPainted(false);
        addButton.setBackground(Color.decode("#4caf50"));
        addButton.setForeground(Color.BLACK);
        addButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        addButton.addActionListener(e -> addTask());
        inputPanel.add(addButton);

        deleteButton = new JButton("Delete Task");
        deleteButton.setFocusPainted(false);
        deleteButton.setBackground(Color.decode("#f44336"));
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        deleteButton.addActionListener(e -> deleteTask());
        inputPanel.add(deleteButton);

        sortButton = new JButton("Sort");
        sortButton.setFocusPainted(false);
        sortButton.setBackground(Color.decode("#2196f3"));
        sortButton.setForeground(Color.BLACK);
        sortButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        sortButton.addActionListener(e -> sortTasksByPriority());
        inputPanel.add(sortButton);

        clearButton = new JButton("Clear All");
        clearButton.setFocusPainted(false);
        clearButton.setBackground(Color.decode("#9e9e9e"));
        clearButton.setForeground(Color.BLACK);
        clearButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clearButton.addActionListener(e -> clearAllTasks());
        inputPanel.add(clearButton);

        doneButton = new JButton("Done");
        doneButton.setFocusPainted(false);
        doneButton.setBackground(Color.decode("#2196f3"));
        doneButton.setForeground(Color.BLACK);
        doneButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        doneButton.addActionListener(e -> moveTaskToDone());
        inputPanel.add(doneButton);

        add(inputPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timetableTable = new JTable(tableModel);
        tableModel.addColumn("ID");
        tableModel.addColumn("Task Name");
        tableModel.addColumn("Priority");

        timetableTable.setRowHeight(30);
        timetableTable.getTableHeader().setFont(timetableTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        timetableTable.getTableHeader().setReorderingAllowed(false);
        timetableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        timetableTable.setDefaultRenderer(Object.class, new PriorityCellRenderer());

        JScrollPane scrollPane = new JScrollPane(timetableTable);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        tabbedPane.addTab("Main Tab", scrollPane);

        doneTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doneTable = new JTable(doneTableModel);
        doneTableModel.addColumn("ID");
        doneTableModel.addColumn("Task Name");
        doneTableModel.addColumn("Priority");

        doneTable.setRowHeight(30);
        doneTable.getTableHeader().setFont(doneTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        doneTable.getTableHeader().setReorderingAllowed(false);
        doneTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        doneTable.setDefaultRenderer(Object.class, new PriorityCellRenderer());

        JScrollPane doneScrollPane = new JScrollPane(doneTable);
        doneScrollPane.setPreferredSize(new Dimension(400, 300));

        tabbedPane.addTab("Done Tasks", doneScrollPane);

        add(tabbedPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void addTask() {
        String taskName = taskNameField.getText();
        String priority = (String) priorityComboBox.getSelectedItem();

        if (!taskName.isEmpty()) {
            Task task = new Task(taskName, priority);
            tasks.add(task);
            tableModel.addRow(new Object[]{task.getId(), taskName, priority});
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a task name.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTask() {
        int selectedRow = timetableTable.getSelectedRow();
        if (selectedRow != -1) {
            int taskId = (int) tableModel.getValueAt(selectedRow, 0);
            for (Task task : tasks) {
                if (task.getId() == taskId) {
                    int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the task '" + task.getTaskName() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        tasks.remove(task);
                        break;
                    }
                }
            }
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sortTasksByPriority() {
        if (sortButton.getText().equals("Sort")) {
            Collections.sort(tasks, Comparator.comparingInt(task -> task.getPriority().ordinal()));
            sortButton.setText("Unsort");
        } else {
            tasks.sort(Comparator.comparingInt(Task::getId));
            sortButton.setText("Sort");
        }

        refreshTable();
    }

    private void clearAllTasks() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all tasks?", "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            tasks.clear();
            tableModel.setRowCount(0);
        }
    }

    private void clearFields() {
        taskNameField.setText("");
        priorityComboBox.setSelectedIndex(0);
    }

    private void moveTaskToDone() {
        int selectedRow = timetableTable.getSelectedRow();
        if (selectedRow != -1) {
            int taskId = (int) tableModel.getValueAt(selectedRow, 0);
            for (Task task : tasks) {
                if (task.getId() == taskId) {
                    doneTasks.add(task);
                    doneTableModel.addRow(new Object[]{task.getId(), task.getTaskName(), task.getPriority().toString()});
                    tasks.remove(task);
                    break;
                }
            }
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to mark as done.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Task task : tasks) {
            tableModel.addRow(new Object[]{task.getId(), task.getTaskName(), task.getPriority().toString()});
        }
    }

    private class Task {
        private static int idCounter = 1;

        private int id;
        private String taskName;
        private Priority priority;

        public Task(String taskName, String priority) {
            this.id = idCounter++;
            this.taskName = taskName;
            this.priority = Priority.valueOf(priority);
        }

        public int getId() {
            return id;
        }

        public String getTaskName() {
            return taskName;
        }

        public Priority getPriority() {
            return priority;
        }
    }

    private enum Priority {
        High,
        Medium,
        Low
    }

    private class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String priority = (String) table.getModel().getValueAt(row, 2);

            switch (priority) {
                case "High":
                    setBackground(Color.decode("#f44336"));
                    break;
                case "Medium":
                    setBackground(Color.decode("#ff9800"));
                    break;
                case "Low":
                    setBackground(Color.decode("#4caf50"));
                    break;
                default:
                    setBackground(table.getBackground());
                    break;
            }

            setForeground(table.getForeground());
            setHorizontalAlignment(SwingConstants.CENTER);

            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Vardast app = new Vardast();
            app.setVisible(true);
        });
    }
}
