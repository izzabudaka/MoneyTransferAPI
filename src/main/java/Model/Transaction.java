package Model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Home on 08/02/16.
 */
public class Transaction {
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

    public Transaction(){
        this.receiver = 0;
        this.sender   = 0;
        this.amount   = 0.0;
        this.id       = COUNTER.getAndIncrement();
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
