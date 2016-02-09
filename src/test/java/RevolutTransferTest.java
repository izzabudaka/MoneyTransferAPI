import Model.Database;
import Model.Transaction;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Created by Home on 08/02/16.
 */
public class RevolutTransferTest {
    private static Vertx vertx;
    private final static Logger logger = Logger.getLogger(RevolutTransferTest.class);

    @BeforeClass
    public static void deployVerticle() throws FileNotFoundException, SQLException {
        RevolutTransfer.main(new String[0]);
        vertx = Vertx.vertx();
        Scanner sc = new Scanner(new FileReader("Users.txt"));
        while( sc.hasNextLine()){
            String[] user = sc.nextLine().split(" ");
            logger.debug(String.format("Adding User %s, %s", user[0], user[1]));
            Database.insertIntoAccounts(Integer.parseInt(user[0]), Double.parseDouble(user[1]));
        }
    }
    @AfterClass
    public static void tearDown() throws SQLException {
        Database.terminateConnection();
    }

    @Test
    public void testSimpleTransfer(){
        logger.debug("Starting testSimpleTransfer");
        final String json   = Json.encodePrettily(new Transaction(2,1,10.0));
        final String length = Integer.toString(json.length());
        vertx.createHttpClient().post(8080, "localhost", "/transfer")
        .putHeader("content-type", "application/json")
        .putHeader("content-length", length)
        .handler( response-> response.bodyHandler(body->{
            logger.debug(body.toString());
        })).write(json).end();
    }
}
