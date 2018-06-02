/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compact.packtpub.javaee8;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.ConnectionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

/**
 *
 * @author VMC027
 */
@ApplicationScoped
@Path("fibonacci")
public class FibonacciResource {

    static final Logger LOGGER = Logger.getAnonymousLogger();

    @Resource
    private ManagedExecutorService executorService;

    @GET
    @Path("/{i}")
    public void fibonnaci(@Suspended final AsyncResponse asyncResponse, @PathParam("i") final int i) {
        LOGGER.log(Level.INFO, "Calculating Fibonacci {0} for {1}.", new Object[]{i, asyncResponse});

        asyncResponse.setTimeout(10, TimeUnit.SECONDS);
        asyncResponse.setTimeoutHandler((r) -> {
            r.resume(Response.accepted(UUID.randomUUID().toString()).build());
        });

        asyncResponse.register(LoggingCompletionCallback.class);
        asyncResponse.register(LoggingConnectionCallback.class);

        executorService.execute(() -> {
            asyncResponse.resume(Response.ok(fibonnaci(i)).build());
            LOGGER.log(Level.INFO, "Calculated Fibonnaci for {0}.", asyncResponse);
        });
    }

    static long fibonnaci(int n) {
        if (n <= 1) {
            return n;
        } else {
            return fibonnaci(n - 1) + fibonnaci(n - 2);
        }
    }

    static class LoggingCompletionCallback implements CompletionCallback {

        @Override
        public void onComplete(Throwable throwable) {
            LOGGER.log(Level.INFO, "Completed Processing", throwable);
        }
    }

    static class LoggingConnectionCallback implements ConnectionCallback {

        @Override
        public void onDisconnect(AsyncResponse disconnected) {
            LOGGER.log(Level.INFO, "Client disconnected on {0}.", disconnected);
        }
    }
}
