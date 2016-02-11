package Service;

import Model.Account;
import Model.Database;
import Model.Transaction;
import Utility.CreateTransactionException;
import io.vertx.core.json.Json;

import java.sql.SQLException;

/**
 * Created by Home on 09/02/16.
 */
public class CreateTransactionService implements CreateTransaction {

    public int createTransaction(String transactionDetails) throws SQLException {
        Transaction transaction = Json.decodeValue(transactionDetails, Transaction.class);
        if(transaction.getAmount() <= 0)
            throw new CreateTransactionException(
                    String.format("You cannot make a transaction with amount %f", transaction.getAmount()));
        else if( new Account(transaction.getSender()).getBalance() < transaction.getAmount() )
            throw new CreateTransactionException(String.format("User %d has insufficient funds",
                    transaction.getSender()));
        Database.insertIntoTransactions(transaction.getId(), transaction.getSender(),
                                        transaction.getReceiver(), transaction.getAmount());
        return transaction.getId();
    }
}
