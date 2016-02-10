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
            "UPDATE ACCOUNTS SET Balance=? WHERE UserId=?";
    private final String accountExistsExpr =
            "SELECT COUNT(*) FROM ACCOUNTS WHERE UserId=?";
    private final String accountBalanceExpr =
            "SELECT Balance FROM ACCOUNTS WHERE UserId=?";

    public Account(int userId) throws SQLException {
        this.userId = userId;
        if(!Exists()){
            throw new AccountNotFoundException(String.format("Account %d doesn't exist in the database!", userId));
        }
    }

    public int getUserId(){
        return userId;
    }

    public boolean Exists() throws SQLException {
        PreparedStatement statement = Database.getStatement(accountExistsExpr);
        statement.setInt(1, userId);
        ResultSet count = Database.selectStatement(statement);
        boolean exists = false;
        if(count.next())
            exists = count.getInt(1) == 1? true : false;
        statement.close();
        return exists;
    }

    public double getBalance() throws SQLException {
        PreparedStatement statement = Database.getStatement(accountBalanceExpr);
        statement.setInt(1, userId);
        ResultSet balance = Database.selectStatement(statement);
        double balanceResult;
        if(balance.next())
            balanceResult = balance.getDouble(1);
        else
            throw new AccountNotFoundException(String.format("Balance for user %d was not retrieved!", userId));
        statement.close();
        return balanceResult;
    }

    public void changeBalance(double change) throws SQLException {
        double balance = getBalance() + change;
        PreparedStatement statement = Database.getStatement(updateBalanceExpr);
        statement.setDouble(1, balance);
        statement.setInt(2, userId);
        Database.updateStatement(statement);
    }
}
