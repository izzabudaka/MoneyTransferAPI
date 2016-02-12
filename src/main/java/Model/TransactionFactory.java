package Model;

import java.sql.SQLException;

/**
 * Created by Home on 12/02/16.
 */
public class TransactionFactory {

    public Transaction createTransaction(int sender, int receiver, double amount) {
        return new Transaction(sender, receiver, amount);
    }

    public Transaction getTransactionFromDatabase( int transactionId ) throws SQLException {
        return new Transaction(transactionId);
    }
}
