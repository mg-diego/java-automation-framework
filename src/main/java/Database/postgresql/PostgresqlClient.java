package Database.postgresql;

import Helpers.ConfigFileReader;
import Helpers.RetryPolicies;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PostgresqlClient {
    private static Connection postgresqlConnection;

    public static void openConnection() {
        openPostgresqlConnection();
    }

    public static void closeConnection() {
        closePostgresqlConnection();
    }

    private static void openPostgresqlConnection() {
        try {
            postgresqlConnection = DriverManager.getConnection(
                    ConfigFileReader.getPostgresqlConnectionString(),
                    ConfigFileReader.getPostgresqlUser(),
                    ConfigFileReader.getPostgresqlPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void closePostgresqlConnection() {
        try {
            postgresqlConnection.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    protected static void executeQuery(String query) {
        RetryPolicies.executeActionWithRetries(() -> {
            try {
                Statement stmt = postgresqlConnection.createStatement();
                stmt.execute(query);
            } catch (Exception e) {
                openPostgresqlConnection();
                throw new Error(e.getMessage());
            }
        });
    }

    protected static int executeUpdate(String query) {
        final int[] modifiedRows = {-1};
        RetryPolicies.executeActionWithRetries(() -> {
            try {
                Statement stmt = postgresqlConnection.createStatement();
                modifiedRows[0] = stmt.executeUpdate(query);
            } catch (Exception e) {
                openPostgresqlConnection();
                throw new Error(e.getMessage());
            }
        });

        return modifiedRows[0];
    }

    protected static ResultSet executeQueryAndReturnResults(String query) {
        final ResultSet[] rs = { null };
        RetryPolicies.executeActionWithRetries(() -> {
            try {
                PreparedStatement preparedStatement = postgresqlConnection.prepareStatement(query);
                rs[0] = preparedStatement.executeQuery();
                rs[0].next();

            } catch (Exception e) {
                openPostgresqlConnection();
                throw new Error(e.getMessage());
            }
        });
        return rs[0];
    }

    public static void executeSqlScript(String filePath) throws Exception {
        try {
            ScriptRunner sr = new ScriptRunner(postgresqlConnection);
            sr.setStopOnError(true);
            Reader reader = new BufferedReader(new FileReader(filePath));
            sr.setLogWriter(null);
            sr.runScript(reader);
            postgresqlConnection.setAutoCommit(true);
        } catch (Exception e) {
            throw new Exception(String.format("An error occurred while executing SQL script '%s': \n %s", filePath, e.getMessage()));
        }
    }

    protected static ArrayList<String> getAllResultsFromResultSet(ResultSet rs, String columnName) throws SQLException {
        var resultList = new ArrayList<String>();

        if (rs.getRow() > 0) {
            resultList.add(rs.getObject(columnName).toString());
            while (rs.next()) {
                resultList.add(rs.getObject(columnName).toString());
                rs.next();
            }
        }
        return resultList;
    }
}
