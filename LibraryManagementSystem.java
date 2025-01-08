import java.sql.*;
import java.util.Scanner;

// Database Utility Class
class Database {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}

// Book Class (Model)
class Book {
    private int id;
    private String title;
    private String author;
    private int quantity;

    public Book(String title, String author, int quantity) {
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    public Book(int id, String title, String author, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getQuantity() { return quantity; }
}

// BookRepository Class (Handles Database Operations)
class BookRepository {
    public void createTableIfNotExists() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY," +
                                "title VARCHAR(255) NOT NULL," +
                                "author VARCHAR(255) NOT NULL," +
                                "quantity INT NOT NULL)";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public void addBook(Book book) throws SQLException {
        String insertSQL = "INSERT INTO books (title, author, quantity) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getQuantity());
            pstmt.executeUpdate();
        }
    }

    public void viewBooks() throws SQLException {
        String selectSQL = "SELECT * FROM books";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            System.out.printf("%-5s %-30s %-30s %-10s%n", "ID", "Title", "Author", "Quantity");
            System.out.println("---------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-30s %-30s %-10d%n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("quantity"));
            }
        }
    }

    public void updateBook(int id, Book book) throws SQLException {
        String updateSQL = "UPDATE books SET title = ?, author = ?, quantity = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getQuantity());
            pstmt.setInt(4, id);
            int rowsUpdated = pstmt.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Book updated successfully." : "Book ID not found.");
        }
    }

    public void deleteBook(int id) throws SQLException {
        String deleteSQL = "DELETE FROM books WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Book deleted successfully." : "Book ID not found.");
        }
    }
}

// Main Application Class
public class LibraryManagementSystem {
    public static void main(String[] args) {
        BookRepository repo = new BookRepository();
        Scanner scanner = new Scanner(System.in);

        try {
            repo.createTableIfNotExists();

            while (true) {
                System.out.println("\nLibrary Management System");
                System.out.println("1. Add Book");
                System.out.println("2. View Books");
                System.out.println("3. Update Book");
                System.out.println("4. Delete Book");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter author: ");
                        String author = scanner.nextLine();
                        System.out.print("Enter quantity: ");
                        int quantity = scanner.nextInt();
                        scanner.nextLine();
                        repo.addBook(new Book(title, author, quantity));
                        System.out.println("Book added successfully.");
                    }
                    case 2 -> repo.viewBooks();
                    case 3 -> {
                        System.out.print("Enter book ID to update: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter new title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter new author: ");
                        String author = scanner.nextLine();
                        System.out.print("Enter new quantity: ");
                        int quantity = scanner.nextInt();
                        scanner.nextLine();
                        repo.updateBook(id, new Book(title, author, quantity));
                    }
                    case 4 -> {
                        System.out.print("Enter book ID to delete: ");
                        int id = scanner.nextInt();
                        repo.deleteBook(id);
                    }
                    case 5 -> {
                        System.out.println("Exiting... Goodbye!");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
