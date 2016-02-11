/**
 * Created by Izz Abudaka on 07/02/16.
 */

import Service.CommitTransactionService;
import Service.CreateTransactionService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
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
        CommitTransactionService commitTransaction = new CommitTransactionService();
        CreateTransactionService createTransaction = new CreateTransactionService();

        router.post("/transaction").handler(routingContext -> {
            CompletableFuture<String> transactionId = new CompletableFuture<>();
            routingContext.request().bodyHandler(buffer -> {
                logger.debug(String.format("/transaction %s", buffer.toString()));
                try {
                    String result = String.format("Transaction: %d", createTransaction.createTransaction(buffer.toString()));
                    transactionId.complete(result);
                } catch (SQLException e) {
                    logger.debug(e.getMessage());
                    e.printStackTrace();
                    transactionId.complete(e.getMessage());
                }
            });
            transactionId.thenAccept(x ->
                routingContext.response()
                .putHeader("content-length", String.valueOf(x.length()))
                .end(String.valueOf(x))
            );
        });

        router.put("/transaction/:id").handler(routingContext -> {
            final CompletableFuture<String> result = new CompletableFuture<>();
            int transactionId = Integer.parseInt(routingContext.request().getParam("id"));
            logger.debug(String.format("/transaction/:id with id %d", transactionId));
            try {
                result.complete(commitTransaction.commitTransaction(transactionId));
            } catch (SQLException e) {
                e.printStackTrace();
                routingContext.response().setStatusCode(500);
                result.complete(e.getMessage());
            }
            result.thenAccept( x-> routingContext.response().end(x));
        });

        server.requestHandler(router::accept).listen(8080);
    }
}
