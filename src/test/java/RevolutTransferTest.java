import Model.Account;
import Model.Database;
import Model.SerializableTransaction;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Home on 08/02/16.
 */
public class RevolutTransferTest {
    private static Vertx vertx;
    private final static Logger logger = Logger.getLogger(RevolutTransferTest.class);
    static HttpClient client;
    static Pattern transactionPattern;

    private void completeRestCalls( Account user1, Account user2, double amount) throws Exception {
        final String json   = Json.encodePrettily(new SerializableTransaction(user1.getUserId(), user2.getUserId(), amount));
        final String length = Integer.toString(json.length());
        final CompletableFuture<String> transactionId = new CompletableFuture<>();
        final CompletableFuture<String>  transactionResult = new CompletableFuture<>();

        client.post(8080, "localhost", "/transaction")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(response -> response.bodyHandler(body ->
                        transactionId.complete(body.toString())))
                .end(json);

        transactionId.thenAccept( transaction -> {
            Matcher matcher = transactionPattern.matcher(transaction);
            if(matcher.find()){
                int id = Integer.parseInt(matcher.group(1));
                logger.debug(transaction);
                client.put(8080, "localhost", String.format("/transaction/%d", id))
                .handler(response -> response.bodyHandler(body -> transactionResult.complete(body.toString())))
                .end();
            } else {
                transactionResult.complete("Invalid Transaction");
            }
        });
        logger.debug(transactionResult.get());
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
        transactionPattern = Pattern.compile("Transaction: (0|[1-9][0-9]*)");
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        Database.terminateConnection();
    }

    @Test
    public void testSimpleTransfer() throws Exception {
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

    @Test
    public void transferInvalidAmount() throws Exception {
        logger.debug("Starting transferNoFunds");
        double transactionAmount = -2.0;
        Account user1 = new Account(3);
        Account user2 = new Account(2);
        double user1Balance = user1.getBalance();
        double user2Balance = user2.getBalance();

        completeRestCalls(user1, user2, transactionAmount);
        assert( user1.getBalance() == user1Balance );
        assert( user2.getBalance() == user2Balance );
    }

    @Test
    public void transferNoFunds() throws Exception {
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
