package Service;

/**
 * Created by Home on 08/02/16.
 */
public class TransactionServiceFactory {

    public static TransactionService createTransactionService(){
        return new H2TransactionService();
    }
}
