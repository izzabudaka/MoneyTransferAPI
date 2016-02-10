import Model.Account;
import Model.Database;
import Model.MockTransaction;
import Utility.CreateTransactionException;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.Json;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Home on 08/02/16.
 */
public class RevolutTransferTest {
    private static Vertx vertx;
    private final static Logger logger = Logger.getLogger(RevolutTransferTest.class);
    static HttpClient client;

    private void completeRestCalls( Account user1, Account user2, double amount) throws SQLException, ExecutionException, InterruptedException {
        final String json   = Json.encodePrettily(new MockTransaction(user1.getUserId(), user2.getUserId(), amount));
        final String length = Integer.toString(json.length());
        final CompletableFuture<Integer> transactionId = new CompletableFuture<>();
        final CompletableFuture<String>  transactionResult = new CompletableFuture<>();

        client.post(8080, "localhost", "/transaction")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(response -> response.bodyHandler(body -> transactionId.complete(Integer.parseInt(body.toString()))))
                .write(json).end();

        transactionId.thenAccept( id -> {
            logger.debug(String.format("Transaction id %d", id));
            client.put(8080, "localhost", String.format("/transaction/%d", id))
                    .handler( response -> response.bodyHandler( body -> transactionResult.complete(body.toString())))
                    .end();
        });

        String result = transactionResult.get();
        logger.debug(result);
    }
    private static void deployVerticle() throws SQLException {
        BasicConfigurator.configure();
        Database.initialiseDatabase();
        vertx.deployVerticle(TransferVerticle.class.getName());
    }

    private static void populateUserDatabase() throws SQLException, FileNotFoundException {
        Scanner sc = new Scanner(new FileReader("Users.txt"));
        while( sc.hasNextLine()){
            String[] user = sc.nextLine().split(" ");
            logger.debug(String.format("Adding User %s, %s", user[0], user[1]));
            Database.insertIntoAccounts(Integer.parseInt(user[0]), Double.parseDouble(user[1]));
        }
    }

    @BeforeClass
    public static void initaliseTests() throws FileNotFoundException, SQLException {
        vertx = Vertx.vertx();
        deployVerticle();
        populateUserDatabase();
        client = vertx.createHttpClient();
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        Database.terminateConnection();
    }

    @Test
    public void testSimpleTransfer() throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Starting testSimpleTransfer");
        double transactionAmount = 10.0;
        Account user1 = new Account(1);
        Account user2 = new Account(2);
        double user1Balance = user1.getBalance();
        double user2Balance = user2.getBalance();

        completeRestCalls(user1, user2, transactionAmount);
        assert( user1.getBalance() == user1Balance - transactionAmount);
        assert( user2.getBalance() == user2Balance + transactionAmount);
    }

    @Test(expected = CreateTransactionException.class)
    public void transferInvalidAmount() throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Starting transferNoFunds");
        double transactionAmount = -2.0;
        Account user1 = new Account(3);
        Account user2 = new Account(2);

        completeRestCalls(user1, user2, transactionAmount);
    }

    @Test
    public void transferNoFunds() throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Starting transferNoFunds");
        double transactionAmount = 20.0;
        Account user1 = new Account(5);
        Account user2 = new Account(6);
        double user1Balance = user1.getBalance();
        double user2Balance = user2.getBalance();

        completeRestCalls(user1, user2, transactionAmount);
        assert( user1.getBalance() == user1Balance);
        assert( user2.getBalance() == user2Balance);
    }
}
