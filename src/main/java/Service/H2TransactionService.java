package Service;

import Model.Account;
import Model.Database;
import Model.Transaction;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Home on 08/02/16.
 */
public class H2TransactionService implements TransactionService {
    private final static Logger                       logger   = Logger.getLogger(H2TransactionService.class);
    private final ConcurrentHashMap<Integer, Account> accounts;

    public H2TransactionService(){
        accounts = new ConcurrentHashMap(1009);
    }

    private Account getAccount(int userId) throws SQLException {
        Account account;
        if( accounts.contains(userId) )
            account = accounts.get(userId);
        else {
            account = new Account(userId);
            accounts.put(userId, new Account(userId));
        }
        return account;
    }

    @Override
    public String makeTransaction(Transaction transaction) throws SQLException {
        Account sender   = getAccount(transaction.getSender());
        Account receiver = getAccount(transaction.getReceiver());
        String  message;

        if(sender.Exists() && receiver.Exists()){
              synchronized (sender){
                  if(sender.getBalance() >= transaction.getAmount()) {
                      synchronized (receiver) {
                          Database.startTransaction();
                          sender.setBalance(-transaction.getAmount());
                          receiver.setBalance(transaction.getAmount());
                          Database.commitTransaction();
                          message = String.format("Transaction %d is successful!\n", transaction.getId());
                          logger.debug(message);
                      }
                  }
                  else{
                      message = String.format("User: %d has insufficient funds!\n",
                                transaction.getSender());
                      logger.debug(message);
                  }
              }
        }
        else {
            message = String.format("One of the users %d and %d does not exist.\n",
                    transaction.getSender(), transaction.getReceiver());
            logger.error(message);
        }

        accounts.remove(transaction.getReceiver());
        accounts.remove(transaction.getSender());

        return message;
    }
}
