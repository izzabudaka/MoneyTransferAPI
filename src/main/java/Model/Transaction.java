package Model;

import Utility.TransactionNotFoundException;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Home on 08/02/16.
 */
public class Transaction {
    private final static Logger logger = Logger.getLogger(Transaction.class);
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final int    receiver;
    private final int    sender;
    private final double amount;
    private final int    id;

    public Transaction(int sender, int receiver, double amount){
        this.receiver = receiver;
        this.sender   = sender;
        this.amount   = amount;
        this.id = COUNTER.getAndIncrement();
    }

    public Transaction(int id) throws SQLException {
        this.id = id;
        if(Exists()){
            logger.debug(String.format("Transaction %d is valid!", id));
            PreparedStatement statement = Database.getStatement("SELECT Sender, Receiver, Amount FROM TRANSACTIONS WHERE TransactionId = ?");
            statement.setInt(1, id);
            ResultSet resultSet         = Database.selectStatement(statement);
            if(resultSet.next()){
                this.sender                 = resultSet.getInt(1);
                this.receiver               = resultSet.getInt(2);
                this.amount                 = resultSet.getInt(3);
            } else {
                throw new TransactionNotFoundException(String.format("Transaction %d was just deleted causing a problem!", id));
            }
        } else {
            throw new TransactionNotFoundException(String.format("Transaction %d doesn't exist in the database!", id));
        }
    }

    public Transaction(){
        this.receiver = 0;
        this.sender   = 0;
        this.amount   = 0.0;
        this.id       = COUNTER.getAndIncrement();
    }

    public boolean Exists() throws SQLException {
        PreparedStatement statement = Database.getStatement("SELECT COUNT(*) FROM TRANSACTIONS WHERE TransactionId = ?");
        statement.setInt(1, id);
        ResultSet count = Database.selectStatement(statement);
        if(count.next())
            return  count.getInt(1) == 1? true : false;
        return false;
    }

    public TransactionState getState() throws SQLException {
        String getTransaction = "SELECT State FROM TRANSACTIONS WHERE TransactionId = ?";
        PreparedStatement statement = Database.getStatement(getTransaction);
        statement.setInt(1, id);
        ResultSet state = Database.selectStatement(statement);
        if(state.next())
            return TransactionState.values()[state.getInt(1)];
        logger.error(String.format("An invalid state was reached with transaction %d!", id));
        return TransactionState.NOT_STARTED;
    }

    public int getSender(){
        return sender;
    }

    public int getReceiver(){
        return receiver;
    }

    public double getAmount(){
        return amount;
    }


    public int getId() {
        return id;
    }

}
