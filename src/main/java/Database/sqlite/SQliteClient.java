package Database.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQliteClient {
    static String databaseName = "database.sqlite";
    static String databasePath = "C:/ProgramData/Qiagen/Lims_QIA_Simulator/db/";
    static String databaseUrl = "jdbc:sqlite:" + databasePath + databaseName;

    static Connection conn;

    public static void openSQliteDbConnection() {
        try {
            conn = DriverManager.getConnection(databaseUrl);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String getValue(Connection conn, String tableName, String columnName, String condition) {
        openSQliteDbConnection();
        String sql = "SELECT * FROM " + tableName + " WHERE " + columnName + " = '" + condition + "'";
        String value = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                value = rs.getString(columnName);
                System.out.println("ID: " + id + ", " + columnName + ": " + value);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        closeConnection();
        return value;
    }

    public static void updateValue(String tableName, String columnName, String newValue, String condition) {
        openSQliteDbConnection();
        String sql = "UPDATE " + tableName + " SET " + columnName + "='" + newValue + "' WHERE " + condition + ";";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Update successful: " + sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        closeConnection();
    }

    private static void insertValue(Connection conn, String tableName, String columnNames, String values) {
        openSQliteDbConnection();
        String sql = "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + values + ")";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Insertion successful.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        closeConnection();
    }
}





