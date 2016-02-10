package Model;

import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by Home on 08/02/16.
 */
public class Database {
    private final static Logger logger = Logger.getLogger(Database.class);
    private static Connection conn;

    private static final String updateStateExpr =
            "UPDATE TRANSACTIONS SET State=? WHERE TransactionId=?";
    private static final String createAccountTableExpr =
            "CREATE TABLE ACCOUNTS"+
                    "( UserId INTEGER NOT NULL AUTO_INCREMENT," +
                    "Balance INTEGER NOT NULL DEFAULT 0)";
    private static final String createTransactionTableExpr =
            "CREATE TABLE TRANSACTIONS"+
            "( TransactionId INTEGER NOT NULL," +
            "State INTEGER NOT NULL DEFAULT 0," +
            "Sender INTEGER NOT NULL," +
            "Receiver INTEGER NOT NULL," +
            "Amount DOUBLE NOT NULL)";
    private static final String insertAccountExpr =
            "INSERT INTO ACCOUNTS VALUES (?,?)";
    private static final String insertTransactionExpr =
            "INSERT INTO TRANSACTIONS VALUES (?,?,?,?,?)";

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
        PreparedStatement statement = conn.prepareStatement(updateStateExpr);
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
        PreparedStatement statement = conn.prepareStatement( createAccountTableExpr);
        updateStatement(statement);
    }

    private static void createTransactionsTable() throws SQLException {
        PreparedStatement statement = conn.prepareStatement(createTransactionTableExpr);
        updateStatement(statement);
    }

    public static void insertIntoAccounts(int userId, double balance) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(insertAccountExpr);
        statement.setInt(1, userId);
        statement.setDouble(2, balance);
        updateStatement(statement);
    }

    public static void insertIntoTransactions(int transactionId, int from, int to, double amount) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(insertTransactionExpr);
        statement.setInt(1, transactionId);
        statement.setInt(2, TransactionState.STARTED.getValue());
        statement.setInt(3, from);
        statement.setInt(4, to);
        statement.setDouble(5, amount);
        updateStatement(statement);
    }
}
