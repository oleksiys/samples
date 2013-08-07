import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import example.guice.Service;

public class ServiceGrizzlyTest {

    private static EmbeddedGrizzly grizzlyServer;
    

    @BeforeClass
    public static void beforeClass() throws Exception {
        grizzlyServer = new EmbeddedGrizzly();
        grizzlyServer.start();
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
        grizzlyServer.stop();
    }

    @Test
    public void givenRunningGrizzlyInstance_whenGetMyresource_thenServiceStringReturned() {
        Client client = ClientBuilder.newClient();
        
        System.out.println(grizzlyServer.getBaseUri());
        
        WebTarget path = client.target(grizzlyServer.getBaseUri())
                        .path("myresource");
        String entity = path
                        .request(MediaType.TEXT_PLAIN_TYPE)
                        .get(String.class);

        assertEquals(Service.SERVICE_STRING, entity);
        
        
    }

}
