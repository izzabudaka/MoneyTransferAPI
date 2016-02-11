package Service;

import java.sql.SQLException;

/**
 * Created by Home on 11/02/16.
 */
public class MockCommitTransactionService implements CommitTransaction {
    @Override
    public String commitTransaction(int transactionId) throws SQLException {
        if( transactionId == 42)
            return "Success";
        return "Failure";
    }
}
