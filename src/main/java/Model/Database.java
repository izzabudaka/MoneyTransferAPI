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
        createTransactionsTable();
    }

    public static void startTransaction() throws SQLException {
        conn.setAutoCommit(false);
    }

    public static void commitTransaction(int transactionId) throws SQLException {
        conn.commit();
        String commitTransaction  = "UPDATE TRANSACTIONS SET State=? WHERE TransactionId=?";
        PreparedStatement statement = conn.prepareStatement(commitTransaction);
        statement.setInt(1, TransactionState.COMMITED.getValue());
        statement.setInt(2, transactionId);
        updateStatement(statement);
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
        String createTable          = "CREATE TABLE ACCOUNTS"+
                "( UserId INTEGER NOT NULL AUTO_INCREMENT," +
                "Balance INTEGER NOT NULL DEFAULT 0)";
        PreparedStatement statement = conn.prepareStatement(createTable);
        updateStatement(statement);
    }

    private static void createTransactionsTable() throws SQLException {
        String createTable          = "CREATE TABLE TRANSACTIONS"+
                "( TransactionId INTEGER NOT NULL," +
                "State INTEGER NOT NULL DEFAULT 0," +
                "Sender INTEGER NOT NULL," +
                "Receiver INTEGER NOT NULL," +
                "Amount DOUBLE NOT NULL)";
        PreparedStatement statement = conn.prepareStatement(createTable);
        updateStatement(statement);
    }

    public static void insertIntoAccounts(int userId, double balance) throws SQLException {
        String insertUser           = "INSERT INTO ACCOUNTS VALUES (?,?)";
        PreparedStatement statement = conn.prepareStatement(insertUser);
        statement.setInt(1, userId);
        statement.setDouble(2, balance);
        updateStatement(statement);
    }

    public static void insertIntoTransactions(int transactionId, int from, int to, double amount) throws SQLException {
        String insertTransaction    = "INSERT INTO TRANSACTIONS VALUES (?,?,?,?,?)";
        PreparedStatement statement = conn.prepareStatement(insertTransaction);
        statement.setInt(1, transactionId);
        statement.setInt(2, TransactionState.STARTED.getValue());
        statement.setInt(3, from);
        statement.setInt(4, to);
        statement.setDouble(5, amount);
        updateStatement(statement);
    }
}
