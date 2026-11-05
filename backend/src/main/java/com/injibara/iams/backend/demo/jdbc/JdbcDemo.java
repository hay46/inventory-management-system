package com.injibara.iams.backend.demo.jdbc;

import java.sql.*;

/**
 * Standalone JDBC demonstration (Advanced Programming requirement).
 * Demonstrates: connection, PreparedStatement, CRUD, transactions, safe resource closing.
 */
public class JdbcDemo {

    public static void main(String[] args) {
        String url = "jdbc:h2:mem:jdbcDemo;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(url, "sa", "")) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS demo_item (id INT PRIMARY KEY, name VARCHAR(100), qty INT)");
            }
            // Insert (PreparedStatement)
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO demo_item(id,name,qty) VALUES (?,?,?)")) {
                ps.setInt(1, 1); ps.setString(2, "Computer"); ps.setInt(3, 10);
                ps.executeUpdate();
            }
            // Read
            try (PreparedStatement ps = conn.prepareStatement("SELECT id,name,qty FROM demo_item WHERE id=?")) {
                ps.setInt(1, 1);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("Read: " + rs.getInt("id") + " " + rs.getString("name") + " qty=" + rs.getInt("qty"));
                    }
                }
            }
            // Update
            try (PreparedStatement ps = conn.prepareStatement("UPDATE demo_item SET qty=? WHERE id=?")) {
                ps.setInt(1, 15); ps.setInt(2, 1);
                ps.executeUpdate();
            }
            // Transaction commit
            conn.commit();
            System.out.println("JDBC demo completed successfully.");
        } catch (SQLException e) {
            System.err.println("JDBC error: " + e.getMessage());
        }
    }
}
