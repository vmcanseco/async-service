/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compact.packtpub.javaee8;

import static compact.packtpub.javaee8.FibonacciResource.fibonnaci;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

/**
 *
 * @author VMC027
 */

@ApplicationScoped
@Path("completable")
public class CompletableFibonacciResource {
    
    @Resource
    private ManagedExecutorService executorService;

    @GET
    @Path("/{i}")
    public void fibonnaci(@Suspended final AsyncResponse asyncResponse, @PathParam("i") final int i) {
        CompletableFuture
                .supplyAsync(()->{
                    return fibonacci(i);
                })
                .thenAccept(value-> asyncResponse.resume(Response.ok(value).build()));
    }

    private long fibonacci(long n) {
         if (n <= 1) {
            return n;
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }

    
}
