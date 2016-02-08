/**
 * Created by Izz Abudaka on 07/02/16.
 */

import Model.Transaction;
import Service.TransactionService;
import Service.TransactionServiceFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class TransferVerticle extends AbstractVerticle {
    private final static Logger logger = Logger.getLogger(TransferVerticle.class);
    @Override
    public void start(Future<Void> fut) {
        HttpServer         server             = vertx.createHttpServer();
        Router             router             = Router.router(vertx);
        TransactionService transactionService = TransactionServiceFactory.createTransactionService();

        router.post("/transfer").handler(routingContext -> {
                    final CompletableFuture<String> result = new CompletableFuture<>();
                    routingContext.request().bodyHandler(buffer -> {
                        Transaction transaction = Json.decodeValue(buffer.toString(), Transaction.class);
                        logger.debug(String.format("Transaction %d from: %d, to: %d, with amount: %d.\n",
                                transaction.getId(), transaction.getSender(), transaction.getReceiver(), transaction.getAmount()));
                        try {
                            result.complete(transactionService.makeTransaction(transaction));
                        } catch (SQLException e) {
                            e.printStackTrace();
                            logger.error(e);
                        }
                        String message = String.format("Transaction from: %d, to: %d, with amount: %d commited!",
                                transaction.getSender(), transaction.getReceiver(), transaction.getAmount());
                        logger.debug(message);
                        routingContext.response().end(message);
                    });
                }
        );
        server.requestHandler(router::accept).listen(8080);
    }
}
