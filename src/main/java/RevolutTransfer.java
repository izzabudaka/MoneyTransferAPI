import Model.Database;
import io.vertx.core.Vertx;
import org.apache.log4j.BasicConfigurator;

import java.sql.SQLException;

/**
 * Created by Home on 07/02/16.
 */
public class RevolutTransfer {
    public static void main(String[] args) throws SQLException {
        BasicConfigurator.configure();
        Database.initialiseDatabase();
        try {
            Vertx vertx = Vertx.vertx();
            vertx.deployVerticle(TransferVerticle.class.getName());
        } finally {
            //Database.terminateConnection();
        }

    }
}
