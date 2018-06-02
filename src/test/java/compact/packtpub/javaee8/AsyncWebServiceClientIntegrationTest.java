/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compact.packtpub.javaee8;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import junit.framework.Assert;
import static junit.framework.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author VMC027
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AsyncWebServiceClientIntegrationTest {

    private static final Logger LOGGER = Logger.getAnonymousLogger();

    private Client client;
    private WebTarget webTarget;

    @Before
    public void setUp() {

        client = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        webTarget = client.target("http://localhost:8080").path("/async-service/api");

    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void fibonacci17() throws InterruptedException, ExecutionException {
        Future<Long> fibonnaci = webTarget.path("/fibonacci/17").request(MediaType.TEXT_PLAIN_TYPE).async().get(Long.class);
        assertEquals(1597, (long) fibonnaci.get());
    }

    @Test
    public void fibonacci17WithCallback() throws InterruptedException, ExecutionException {
        Future<Long> fibonnaci = webTarget.path("/fibonacci/17").request(MediaType.TEXT_PLAIN_TYPE).async().get(new InvocationCallback<Long>() {
            @Override
            public void completed(Long rspns) {
                LOGGER.log(Level.INFO, "Completed Fibonacci 17 with {0}.", rspns);
            }

            @Override
            public void failed(Throwable thrwbl) {
                LOGGER.log(Level.INFO, "Completed Fibonacci 17 with error.", thrwbl);
            }

        });
        assertEquals(1597, (long) fibonnaci.get());
    }

    @Test
    public void fibonacci3_4_6_6_8_21() throws InterruptedException, ExecutionException {

        CompletableFuture<Long> fibonnaci = Futures.toCompletable(webTarget.path("/fibonacci/{i}")
                .resolveTemplate("i", 3)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .async()
                .get(Long.class))
                .thenApplyAsync(i -> webTarget.path("/fibonacci/{i}").resolveTemplate("i", i + 2).request(MediaType.TEXT_PLAIN_TYPE).get(Long.class))
                .thenApplyAsync(i -> webTarget.path("/fibonacci/{i}").resolveTemplate("i", i + 2).request(MediaType.TEXT_PLAIN_TYPE).get(Long.class))
                .thenApplyAsync(i -> webTarget.path("/fibonacci/{i}").resolveTemplate("i", i + 1).request(MediaType.TEXT_PLAIN_TYPE).get(Long.class))
                .thenApplyAsync(i -> webTarget.path("/fibonacci/{i}").resolveTemplate("i", i).request(MediaType.TEXT_PLAIN_TYPE).get(Long.class))
                .thenApplyAsync(i -> webTarget.path("/fibonacci/{i}").resolveTemplate("i", i).request(MediaType.TEXT_PLAIN_TYPE).get(Long.class));
        assertEquals(10946, (long) fibonnaci.get());
    }

    @Test
    public void fibonacciAnyOf() throws InterruptedException, ExecutionException {
        CompletableFuture<Long> fibonnaci3 = Futures.toCompletable(webTarget.path("/fibonacci/{i}")
                .resolveTemplate("i", 3)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .async()
                .get(Long.class));

        CompletableFuture<Long> fibonnaci5 = Futures.toCompletable(webTarget.path("/fibonacci/{i}")
                .resolveTemplate("i", 5)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .async()
                .get(Long.class));
        CompletableFuture<Long> fibonnaci7 = Futures.toCompletable(webTarget.path("/fibonacci/{i}")
                .resolveTemplate("i", 5)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .async()
                .get(Long.class));
        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(fibonnaci3, fibonnaci5, fibonnaci7);
        assertEquals(2, (long) anyOfFuture.get());
    }

    @Test
    public void fibonacciAllOf() throws InterruptedException, ExecutionException {
        List<CompletableFuture<Long>> fibonnaci = new ArrayList<>();
        CompletableFuture<Long> fibonnaci3 = Futures.toCompletable(webTarget.path("/fibonacci/{i}")
                .resolveTemplate("i", 3)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .async()
                .get(Long.class));

        CompletableFuture<Long> fibonnaci5 = Futures.toCompletable(webTarget.path("/fibonacci/{i}")
                .resolveTemplate("i", 5)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .async()
                .get(Long.class));
        CompletableFuture<Long> fibonnaci7 = Futures.toCompletable(webTarget.path("/fibonacci/{i}")
                .resolveTemplate("i", 5)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .async()
                .get(Long.class));
        fibonnaci.add(fibonnaci3);
        fibonnaci.add(fibonnaci5);
        fibonnaci.add(fibonnaci7);

        CompletableFuture<Void> anyOfFuture = CompletableFuture.allOf(fibonnaci.toArray(new CompletableFuture[fibonnaci.size()]));
        CompletableFuture<List<Long>> thenApply = anyOfFuture.thenApply(v -> {
            return fibonnaci.stream().map(fibo -> fibo.join()).collect(Collectors.toList());
        });
        thenApply.get().stream().forEach(value -> {
            LOGGER.log(Level.INFO, "Completed Fibonacci {0} .", value);
        });
        assertEquals(true, true);
    }

    private CompletableFuture<Long> CompletableFuture(Future<Long> get) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*@Test
    public void fibonacci49WithCallback() throws Exception {
        Future<Response> fibonacci = webTarget.path("/fibonacci/49").request().async().get(new InvocationCallback<Response>() {
            @Override
            public void completed(Response Response) {
                LOGGER.log(Level.INFO, "Completed Fibonacci 49 with {0}.", Response);
            }

            @Override
            public void failed(Throwable throwable) {
                LOGGER.log(Level.WARNING, "Completed Fibonacci 49 with error.", throwable);
            }
        });
        assertEquals(202, fibonacci.get().getStatus());
    }*/
    static class Futures {

        static <T> CompletableFuture<T> toCompletable(Future<T> future) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

}
