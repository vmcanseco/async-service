/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compact.packtpub.javaee8;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@Path("thread")
@Produces(MediaType.APPLICATION_JSON)
public class ThreadResource {
    
    private static final Logger LOGGER = Logger.getAnonymousLogger();
    
    @GET
    public void calculate(@Suspended final AsyncResponse asyncResponse){
        final String requestThreadName=getCurrrentThreadName();
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            final String responseThreadName=getCurrrentThreadName();
            
            Map<String,String> response=new HashMap<>();
            response.put("requestThread", requestThreadName);
            response.put("responseThread", responseThreadName);
            asyncResponse.resume(Response.ok(response).build());

        }).start();
    }
    
    private String getCurrrentThreadName(){
        return Thread.currentThread().getName();
    }
    
}
