package Model;

/**
 * Created by Home on 10/02/16.
 */
public class MockTransaction {
    public final double amount;
    public final int receiver;
    public final int sender;

    public MockTransaction(int sender, int receiver, double amount){
        this.receiver = receiver;
        this.sender   = sender;
        this.amount   = amount;
    }

    public MockTransaction(){
        this.receiver = 0;
        this.sender   = 0;
        this.amount   = 0.0;
    }
}
