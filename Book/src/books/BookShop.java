package books;

import javax.swing.*;
import java.sql.*;

public class BookShop {

    public void addBook(int bookId, String title, String author, double price, int quantity) {
        String query = "INSERT INTO books (id, title, author, price, quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, bookId);
            pst.setString(2, title);
            pst.setString(3, author);
            pst.setDouble(4, price);
            pst.setInt(5, quantity);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book added to database!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteBook(int bookId) {
        String query = "DELETE FROM books WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, bookId);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Book deleted from database!");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Book not found!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBook(int bookId, String title, String author, double price, int quantity) {
        String query = "UPDATE books SET title=?, author=?, price=?, quantity=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, title);
            pst.setString(2, author);
            pst.setDouble(3, price);
            pst.setInt(4, quantity);
            pst.setInt(5, bookId);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Book updated in database!");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Book not found!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Book findBook(String title) {
        String query = "SELECT * FROM books WHERE title = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, title);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sellBook(String title, int quantity) {
        Book book = findBook(title);
        if (book != null) {
            if (book.getQuantity() >= quantity) {
                int newQuantity = book.getQuantity() - quantity;
                updateBook(book.getBookId(), book.getTitle(), book.getAuthor(), book.getPrice(), newQuantity);
                JOptionPane.showMessageDialog(null, "Book sold successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Insufficient stock!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Book not found!");
        }
    }
}


