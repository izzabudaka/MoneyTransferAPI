package Model;

import Utility.AccountNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Home on 08/02/16.
 */
public class Account {
    private final int userId;

    private final String updateBalanceExpr =
            "UPDATE ACCOUNT Balance= ? WHERE UserId= ?";
    private final String accountExistsExpr =
            "SELECT COUNT(*) FROM ACCOUNTS WHERE UserId = ?";
    private final String accountBalanceExpr =
            "SELECT Balance FROM ACCOUNTS WHERE UserId = ?";

    public Account(int userId) throws SQLException {
        if(Exists()){
            this.userId = userId;
        } else {
            throw new AccountNotFoundException(String.format("Account %d doesn't exist in the database!", userId));
        }
    }

    public boolean Exists() throws SQLException {
        PreparedStatement statement = Database.getStatement(accountExistsExpr);
        statement.setInt(1, userId);
        ResultSet count = Database.selectStatement(statement);
        if(count.next())
            return  count.getInt(1) == 1? true : false;
        return false;
    }

    public double getBalance() throws SQLException {
        PreparedStatement statement = Database.getStatement(accountBalanceExpr);
        statement.setInt(1, userId);
        ResultSet balance = Database.selectStatement(statement);
        if(balance.next())
            return balance.getDouble(0);
        else
            throw new AccountNotFoundException(String.format("Balance for user %d was not retrieved!", userId));
    }

    public void changeBalance(double change) throws SQLException {
        double balance = getBalance() + change;
        PreparedStatement statement = Database.getStatement(updateBalanceExpr);
        statement.setDouble(1, balance);
        statement.setInt(2, userId);
        Database.updateStatement(statement);
    }
}
