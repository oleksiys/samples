package example.jersey;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.google.inject.servlet.RequestScoped;

import example.guice.Service;



/**
 * Root resource (exposed at "myresource" path)
 */
@RequestScoped
@Path("myresource")
public class MyResource {

	private Service service;

	@Inject
	public MyResource(Service service) {
		this.service = service;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt(@Context UriInfo uriInfo) {
	    
		return service.get();
	}
}