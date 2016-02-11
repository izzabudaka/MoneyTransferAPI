package Service;

import java.sql.SQLException;

/**
 * Created by Home on 11/02/16.
 */
public class MockCreateTransactionService implements CreateTransaction{
    @Override
    public int createTransaction(String transactionDetails) throws SQLException {
        return 42;
    }
}
