package eu.lucubratory.jrjf1;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.server.mvc.Viewable;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class IndexModel {
    @Path("hello")
    @GET
    public String helloworld() {
        return "hello";
    }
    
    @GET
    @Path("index")
    public Viewable index(@Context HttpServletRequest request) {
        request.setAttribute("obj", "IT Works");
        System.out.println("/INDEX called");
        return new Viewable("/index.jsp", null);
    }
}
