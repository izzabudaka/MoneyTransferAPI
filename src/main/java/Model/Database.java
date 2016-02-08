package Model;

import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by Home on 08/02/16.
 */
public class Database {
    private final static Logger logger = Logger.getLogger(Database.class);
    private static Connection conn;

    public static PreparedStatement getStatement(String query) throws SQLException {
        return conn.prepareStatement(query);
    }
    public static void initialiseDatabase() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("Driver not Found!");
        }
        conn = DriverManager.getConnection("jdbc:h2:~/revolut", "revolut", "");
        createAccountsTable();
    }

    public static void startTransaction() throws SQLException {
        conn.setAutoCommit(false);
    }
    public static void commitTransaction() throws SQLException {
        conn.commit();
    }
    public static void terminateConnection() throws SQLException {
        conn.close();
    }

    public static ResultSet selectStatement(PreparedStatement statement){
        ResultSet resultSet = null;
        try {
            logger.debug(String.format("Executing statement %s\n", statement));
            resultSet = statement.executeQuery();
            logger.debug(String.format("Retrieved result %s\n", resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(String.format("Statement %s was not executed\n", statement));
        }
        return resultSet;
    }

    public static boolean updateStatement(PreparedStatement statement){
        try {
            logger.debug(String.format("Executing statement %s\n", statement));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.printf("Statement %s was not executed\n", statement);
            return false;
        }
        System.out.printf("Executing %s completed!\n", statement);
        return true;
    }

    private static void createAccountsTable() throws SQLException {
        String createTable = "CREATE TABLE ACCOUNTS"+
                "( UserId INTEGER NOT NULL AUTO_INCREMENT," +
                "Balance INTEGER NOT NULL DEFAULT 0)";
        PreparedStatement statement = conn.prepareStatement(createTable);
        updateStatement(statement);
    }

    public static void insertIntoAccounts(int userId, double balance) throws SQLException {
        String insertUser = "INSERT INTO ACCOUNTS VALUES (?,?)";
        PreparedStatement statement = conn.prepareStatement(insertUser);
        statement.setInt(1, userId);
        statement.setDouble(2, balance);
        updateStatement(statement);
    }
}
