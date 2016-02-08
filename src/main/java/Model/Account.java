package Model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Home on 08/02/16.
 */
public class Account {
    private ResultSet accountInfo;
    private final int userId;

    public Account(int userId) throws SQLException {
        this.userId = userId;
        PreparedStatement statement = Database.getStatement("SELECT * FROM ACCOUNTS WHERE UserId = ?");
        statement.setInt(1, userId);
        accountInfo = Database.selectStatement(statement);
    }

    public boolean Exists() throws SQLException {
        return accountInfo.first();
    }

    public double getBalance() throws SQLException {
        PreparedStatement statement = Database.getStatement("SELECT Balance FROM ACCOUNTS WHERE UserId = ?");
        statement.setInt(1, userId);
        ResultSet balance = Database.selectStatement(statement);
        return balance.getDouble(0);
    }

    public void setBalance(double change) throws SQLException {
        double balance = getBalance() + change;
        PreparedStatement statement = Database.getStatement("UPDATE ACCOUNT Balance= ? WHERE UserId= ?");
        statement.setDouble(1, balance);
        statement.setInt(2, userId);
        Database.updateStatement(statement);
    }
}
