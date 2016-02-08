package Service;

import Model.Transaction;

import java.sql.SQLException;

/**
 * Created by Home on 08/02/16.
 */
public interface TransactionService {
    public String makeTransaction(Transaction transaction) throws SQLException;
}
