package Service;

import java.sql.SQLException;

/**
 * Created by Home on 11/02/16.
 */
public interface CommitTransaction {

    public String commitTransaction(int transactionId ) throws SQLException;
}
