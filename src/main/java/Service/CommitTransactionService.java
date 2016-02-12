package Service;

import Model.*;
import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * Created by Home on 09/02/16.
 */
public class CommitTransactionService implements CommitTransaction {
    private final static Logger logger = Logger.getLogger(Database.class);
    private String message;
    private final AccountFactory accountFactory;

    public CommitTransactionService(){
        accountFactory = new AccountFactory();
    }

    private void setMessage(String message){
        this.message = message;
        logger.debug(message);
    }

    public String commitTransaction(int transactionId) throws SQLException {
        Transaction transaction = new Transaction(transactionId);

        if(transaction.getState() == TransactionState.COMMITED){
            setMessage(String.format("Transaction %d already commited!", transactionId));
            return message;
        }

        Account sender   = accountFactory.createAccount(transaction.getSender());
        Account receiver = accountFactory.createAccount(transaction.getReceiver());

        if(sender.exists() && receiver.exists()){
            if(sender.getBalance() >= transaction.getAmount()) {
                Database.startTransaction();
                sender.changeBalance(-transaction.getAmount());
                receiver.changeBalance(transaction.getAmount());
                Database.commitTransaction(transactionId);
                setMessage(String.format("Transaction %d is successful!\n", transaction.getId()));
            }
            else{
                setMessage(String.format("User: %d has insufficient funds!\n", transaction.getSender()));
            }
        }
        else {
            setMessage(String.format("One of the users %d and %d does not exist.\n", transaction.getSender(),
                       transaction.getReceiver()));
        }

        return message;
    }
}
