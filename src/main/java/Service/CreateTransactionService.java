package Service;

import Model.Database;
import Model.Transaction;
import io.vertx.core.json.Json;

import java.sql.SQLException;

/**
 * Created by Home on 09/02/16.
 */
public class CreateTransactionService {

    public int createTransaction(String transactionDetails) throws SQLException {
        Transaction transaction = Json.decodeValue(transactionDetails, Transaction.class);
        Database.insertIntoTransactions(transaction.getId(), transaction.getSender(),
                                        transaction.getReceiver(), transaction.getAmount());
        return transaction.getId();
    }
}
