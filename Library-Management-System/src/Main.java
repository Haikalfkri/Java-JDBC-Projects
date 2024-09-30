import java.sql.*;
import java.util.Scanner;


public class Main {

    private static final String url = "jdbc:mysql://localhost:3306/LibraryDB";
    private static final String username = "root";
    private static final String password = "";

    private static Connection connection;

    public static void main(String[] args) {

        try {
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);

            Scanner scanner = new Scanner(System.in);
            int option;
            do {
                System.out.println("---- Library Management System ----");
                System.out.println("1. Add Author");
                System.out.println("2. Add Book");
                System.out.println("3. Add Borrower");
                System.out.println("4. Borrow a Book");
                System.out.println("5. Return a Book");
                System.out.println("6. View Books");
                System.out.println("7. Exit");
                System.out.print("Choose an option: ");
                option = scanner.nextInt();
                scanner.nextLine(); // consume new line

                switch (option) {
                    case 1:
                        // add author
                        addAuthor(scanner);
                        break;
                    case 2:
                        // add book
                        addBook(scanner);
                        break;
                    case 3:
                        // add borrower
                        addBorrower(scanner);
                        break;
                    case 4:
                        // borrow a book
                        borrowBook(scanner);
                        break;
                    case 5:
                        // return a book
                        returnBook(scanner);
                        break;
                    case 6:
                        // view book
                        viewBooks();
                        break;
                    case 7:
                        System.out.println("Exiting...");
                        break;
                }
            } while (option != 7);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addAuthor(Scanner scanner) throws SQLException {
        System.out.print("Enter author name: ");
        String name = scanner.nextLine();

        String sql = "INSERT INTO authors (name) VALUES (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);

        int rowsAffected = preparedStatement.executeUpdate();
        connection.commit();
        if (rowsAffected > 0) {
            System.out.println("Author added.");
        } else {
            System.out.println("Failed to add author.");
        }
    }

    private static void addBook(Scanner scanner) throws SQLException {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();

        System.out.print("Enter author id: ");
        int authorId = scanner.nextInt();

        String sql = "INSERT INTO books (title, author_id) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, title);
        preparedStatement.setInt(2, authorId);

        int rowsAffected = preparedStatement.executeUpdate();
        connection.commit();
        if (rowsAffected > 0) {
            System.out.println("Book added.");
        } else {
            System.out.println("Failed to add book.");
        }
    }

    private static void addBorrower(Scanner scanner) throws SQLException {
        System.out.print("Enter borrower name: ");
        String name = scanner.nextLine();

        System.out.print("Enter borrower email: ");
        String email = scanner.nextLine();

        String sql = "INSERT INTO borrowers(name, email) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, email);

        int rowsAffected = preparedStatement.executeUpdate();
        connection.commit();
        if (rowsAffected > 0) {
            System.out.println("Borrower added.");
        } else {
            System.out.println("Failed to add borrower.");
        }
    }

    private static void borrowBook(Scanner scanner) throws SQLException {
        System.out.print("Enter book ID to borrow: ");
        int bookId = scanner.nextInt();

        System.out.print("Enter borrower ID: ");
        int borrowerId = scanner.nextInt();

        String sql = "INSERT INTO borrowing (book_id, borrower_id) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, bookId);
        preparedStatement.setInt(2, borrowerId);

        int rowsAffected = preparedStatement.executeUpdate();
        connection.commit();
        if (rowsAffected > 0) {
            System.out.println("Book borrowed successfully.");
        } else {
            System.out.println("Failed to borrow the book.");
        }
    }

    private static void returnBook(Scanner scanner) throws SQLException {
        System.out.print("Enter book ID to return: ");
        int bookId = scanner.nextInt();

        System.out.print("Entter borrower ID: ");
        int borrowerId = scanner.nextInt();

        String sql = "UPDATE borrowing SET returned_at = CURRENT_TIMESTAMP WHERE book_id = ? AND borrower_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, bookId);
        preparedStatement.setInt(2, borrowerId);

        int rowsAffected = preparedStatement.executeUpdate();
        connection.commit();
        if (rowsAffected > 0) {
            System.out.println("Book returned successfully.");
        } else {
            System.out.println("Book return failed.");
        }
    }

    private static void viewBooks() throws SQLException {
        String sql = "SELECT b.id, b.title, a.name AS author FROM books b JOIN authors a ON b.author_id = a.id";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("---- Books in Library ----");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") + ", Title: " + rs.getString("title") + ", Author: " + rs.getString("author"));
        }
    }
}