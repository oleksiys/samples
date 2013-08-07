import java.net.URI;
import java.util.EnumSet;

import com.google.inject.servlet.GuiceFilter;
import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.servlet.ServletContainer;

public class EmbeddedGrizzly {

    private HttpServer server;

    private static final URI BASE_URI = UriBuilder
            .fromUri("http://127.0.0.1/").port(8080).build();

    public HttpServer start() throws IOException {
        System.out.println("Starting grizzly...");

        // Create HttpServer
        final HttpServer serverLocal =
                GrizzlyHttpServerFactory.createHttpServer(BASE_URI, false);

        final WebappContext context =
                new WebappContext("Guice Webapp sample", "");

        context.addListener(example.jersey.Main.class);
        
        // Initialize and register Jersey ServletContainer
        ServletRegistration servletRegistration =
                context.addServlet("ServletContainer", ServletContainer.class);
        servletRegistration.addMapping("/*");
        servletRegistration.setInitParameter("javax.ws.rs.Application",
                "example.jersey.MyApplication");

        // Initialize and register GuiceFilter
        final FilterRegistration registration =
                context.addFilter("GuiceFilter", GuiceFilter.class);
        registration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), "/*");

        context.deploy(serverLocal);

        serverLocal.start();
        
        server = serverLocal;
        return server;
    }
    
    public void stop() throws Exception{
        server.shutdown();
    }
    
    public URI getBaseUri(){
        return BASE_URI;
    }
}