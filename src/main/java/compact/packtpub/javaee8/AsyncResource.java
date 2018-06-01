/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compact.packtpub.javaee8;

import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author VMC027
 */
@ApplicationScoped
@Path("async")
@Produces(MediaType.APPLICATION_JSON)
public class AsyncResource {

    private static final Logger LOGGER = Logger.getAnonymousLogger();

    private final BlockingQueue<AsyncResponse> responses = new LinkedBlockingDeque<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void lock(@Suspended final AsyncResponse asyncResponse) throws InterruptedException {
        String currentThreadName = getCurrrentThreadName();
        LOGGER.log(Level.INFO, "Locking {0} with thread {1} ", new Object[]{asyncResponse, currentThreadName});

        asyncResponse.setTimeout(10, TimeUnit.SECONDS);
        responses.put(asyncResponse);

    }

    @DELETE
    public Response unlock() {
        String currentThreadName = getCurrrentThreadName();
        AsyncResponse asyncResponse = responses.poll();

        if (asyncResponse != null) {
            LOGGER.log(Level.INFO, "Unlocking {0} with thread {1} ", new Object[]{asyncResponse, currentThreadName});
            asyncResponse.resume(Response.ok(Collections.singletonMap("currentThread", currentThreadName)).build());
        }
        
        return Response.noContent().build();
    }

    private String getCurrrentThreadName() {
        return Thread.currentThread().getName();
    }
}
