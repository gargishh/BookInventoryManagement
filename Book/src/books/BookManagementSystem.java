package books;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class BookManagementSystem extends JFrame {
    private JTextField titleTextField, authorTextField, priceTextField, quantityTextField;
    private DefaultTableModel tableModel;
    private BookShop bookShop;

    public BookManagementSystem() {
        bookShop = new BookShop();

        setTitle("Book Management System");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Book Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblTitle = new JLabel("Title:");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblTitle, gbc);

        titleTextField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(titleTextField, gbc);

        JLabel lblAuthor = new JLabel("Author:");
        lblAuthor.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblAuthor, gbc);

        authorTextField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(authorTextField, gbc);

        JLabel lblPrice = new JLabel("Price:");
        lblPrice.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblPrice, gbc);

        priceTextField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(priceTextField, gbc);

        JLabel lblQuantity = new JLabel("Quantity:");
        lblQuantity.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(lblQuantity, gbc);

        quantityTextField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(quantityTextField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Book");
        JButton sellButton = new JButton("Sell Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton sortButton = new JButton("Sort");
        JButton updateButton = new JButton("Update Book");
        JButton closeButton = new JButton("Close");

        buttonPanel.add(addButton);
        buttonPanel.add(sellButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(closeButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Price", "Quantity"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter book ID:"));
                String title = titleTextField.getText();
                String author = authorTextField.getText();
                double price = Double.parseDouble(priceTextField.getText());
                int quantity = Integer.parseInt(quantityTextField.getText());

                bookShop.addBook(id, title, author, price, quantity);

                Object[] rowData = {id, title, author, price, quantity};
                tableModel.addRow(rowData);

                clearFields();
            }
        });

        sellButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = JOptionPane.showInputDialog(null, "Enter book title to sell:");
                Book book = bookShop.findBook(title);
                if (book != null) {
                    int quantity = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter quantity to sell:"));
                    if (quantity <= book.getQuantity()) {
                        bookShop.sellBook(title, quantity);
                        int rowIndex = findRowByBookId(book.getBookId());
                        if (rowIndex != -1) {
                            tableModel.setValueAt(book.getQuantity() - quantity, rowIndex, 4);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Not enough stock!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Book not found!");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter book ID to delete:"));
                boolean deleted = bookShop.deleteBook(id);
                if (deleted) {
                    int rowIndex = findRowByBookId(id);
                    if (rowIndex != -1) {
                        tableModel.removeRow(rowIndex);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Book not found!");
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Select a row to update!");
                    return;
                }
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String newTitle = JOptionPane.showInputDialog(null, "Enter new title:", tableModel.getValueAt(selectedRow, 1));
                String newAuthor = JOptionPane.showInputDialog(null, "Enter new author:", tableModel.getValueAt(selectedRow, 2));
                double newPrice = Double.parseDouble(JOptionPane.showInputDialog(null, "Enter new price:", tableModel.getValueAt(selectedRow, 3)));
                int newQuantity = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter new quantity:", tableModel.getValueAt(selectedRow, 4)));

                boolean updated = bookShop.updateBook(id, newTitle, newAuthor, newPrice, newQuantity);
                if (updated) {
                    tableModel.setValueAt(newTitle, selectedRow, 1);
                    tableModel.setValueAt(newAuthor, selectedRow, 2);
                    tableModel.setValueAt(newPrice, selectedRow, 3);
                    tableModel.setValueAt(newQuantity, selectedRow, 4);
                } else {
                    JOptionPane.showMessageDialog(null, "Update failed!");
                }
            }
        });

        sortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
                sorter.setComparator(1, String.CASE_INSENSITIVE_ORDER);
                table.setRowSorter(sorter);
                ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
                sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
                sorter.setSortKeys(sortKeys);
                sorter.sort();
            }
        });

        closeButton.addActionListener(e -> System.exit(0));
    }

    private int findRowByBookId(int id) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == id) return i;
        }
        return -1;
    }

    private void clearFields() {
        titleTextField.setText("");
        authorTextField.setText("");
        priceTextField.setText("");
        quantityTextField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BookManagementSystem().setVisible(true);
        });
    }
}
