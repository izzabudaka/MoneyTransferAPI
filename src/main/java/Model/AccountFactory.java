package Model;

import Utility.AccountNotFoundException;

import java.sql.SQLException;

/**
 * Created by Home on 11/02/16.
 */
public class AccountFactory {

    public Account createAccount(int userId) throws SQLException {
        Account account = new Account(userId);
        if(account.exists()) return account;
        else throw new AccountNotFoundException(String.format("Account %d doesn't exist in the database!", userId));
    }
}
