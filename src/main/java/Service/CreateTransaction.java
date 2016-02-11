package Service;

import java.sql.SQLException;

/**
 * Created by Home on 11/02/16.
 */
public interface CreateTransaction {
    public int createTransaction(String transactionDetails) throws SQLException;
}
