import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;

import java.util.Scanner;

public class Main {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/online_shopping_jdbc";
    private static final String username = "root";
    private static final String password = "";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println("Online Shopping Cart System");
                System.out.println("1. Add Product");
                System.out.println("2. View Products");
                System.out.println("3. Add to Cart");
                System.out.println("4. View Cart");
                System.out.println("5. Checkout");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        // add product
                        addProduct(connection, scanner);
                        break;
                    case 2:
                        // view products
                        viewProducts(connection);
                        break;
                    case 3:
                        // add to cart
                        addToCart(connection, scanner);
                        break;
                    case 4:
                        // view cart
                        viewCart(connection);
                        break;
                    case 5:
                        // checkout
                        checkout(connection);
                        break;
                    case 6:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addProduct(Connection connection, Scanner scanner) {
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter price: ");
        Double price = scanner.nextDouble();
        System.out.print("Enter stock quantity: ");
        int stock = scanner.nextInt();

        String sql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, price);
            preparedStatement.setInt(3, stock);
            preparedStatement.executeUpdate();
            System.out.println("Product added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewProducts(Connection connection) {
        String sql = "SELECT * FROM products";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("ID\tName\tPrice\tStock");
            while (rs.next()) {
                System.out.printf("%d\t%s\t%.2f\t%d\n",
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addToCart(Connection connection, Scanner scanner) {
        System.out.print("Enter product ID to add to cart: ");
        int productId = scanner.nextInt();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        // check product stock
        String stockCheckSql = "SELECT stock FROM products WHERE product_id = ?";
        try {
            PreparedStatement stockCheckStmt  = connection.prepareStatement(stockCheckSql);
            stockCheckStmt.setInt(1, productId);
            ResultSet resultSet = stockCheckStmt.executeQuery();
            if (resultSet.next()) {
                int stock = resultSet.getInt("stock");
                if (stock < quantity) {
                    System.out.println("Insufficient stock.");
                    return;
                }
            } else {
                System.out.println("Product not found.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // add to cart
        String addToCartSql = "INSERT INTO cart (product_id, quantity) VALUES (?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(addToCartSql);
            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, quantity);
            preparedStatement.executeUpdate();
            System.out.println("Product added to cart successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewCart(Connection connection) {
        String sql = "SELECT c.cart_id, p.name, c.quantity, p.price " +
                     "FROM cart c JOIN products p ON c.product_id = p.product_id";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                System.out.printf("%d\t%s\t%d\t%.2f\n",
                        resultSet.getInt("cart_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void checkout(Connection connection) {
        String sql = "SELECT SUM(c.quantity * p.price) AS total FROM cart c JOIN products p ON c.product_id = p.product_id";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                double total = resultSet.getDouble("total");
                System.out.printf("Total Amount: %.2f\n", total);

                // clear the cart after checkout
                String clearCartSql = "DELETE FROM cart";
                try {
                    Statement clearStmt = connection.createStatement();
                    clearStmt.executeUpdate(clearCartSql);
                    System.out.println("Checkout successfull. Cart has been cleared");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}