package eu.lucubratory.jrjf1;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.apache.jasper.servlet.JspServlet;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;

import static eu.lucubratory.jrjf1.Main.startServer;

public class Main {
    private static final String JERSEY_SERVLET_CONTEXT_PATH = "";
    private static final String JSP_CLASSPATH_ATTRIBUTE =
            "org.apache.catalina.jsp_classpath";
    
    private static final URI BASE_URI = UriBuilder
            .fromUri("http://127.0.0.1/").port(8080).build();
    
    protected static HttpServer startServer() throws IOException {
        System.out.println("Starting grizzly...");
        
        // Create HttpServer and register dummy "not found" HttpHandler
        HttpServer httpServer = GrizzlyServerFactory.createHttpServer(BASE_URI, new HttpHandler() {

            @Override
            public void service(Request rqst, Response rspns) throws Exception {
                rspns.setStatus(404, "Not found");
                rspns.getWriter().write("404: not found");
            }
        });
        
        WebappContext context = new WebappContext("WebappContext", JERSEY_SERVLET_CONTEXT_PATH);
        
        // Initialize and register Jersey Servlet
        FilterRegistration registration = context.addFilter("ServletContainer", ServletContainer.class);
        registration.setInitParameter(ServletContainer.RESOURCE_CONFIG_CLASS, 
                ClassNamesResourceConfig.class.getName());
        registration.setInitParameter(ClassNamesResourceConfig.PROPERTY_CLASSNAMES, IndexModel.class.getName());
        registration.setInitParameter("com.sun.jersey.config.property.JSPTemplatesBasePath",
                "/WEB-INF/jsp");
        // configure Jersey to bypass non-Jersey requests (static resources and jsps)
        registration.setInitParameter("com.sun.jersey.config.property.WebPageContentRegex",
                "(/(image|js|css)/?.*)|(/.*\\.jsp)|(/WEB-INF/.*\\.jsp)|"
                + "(/WEB-INF/.*\\.jspf)|(/.*\\.html)|(/favicon\\.ico)|"
                + "(/robots\\.txt)");
        
        registration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), "/*");

        // Initialize and register JSP Servlet        
        ServletRegistration jspRegistration = context.addServlet(
                "JSPContainer", JspServlet.class.getName());
        jspRegistration.addMapping("/*");
        
        // Set classpath for Jasper compiler based on the current classpath
        context.setAttribute(JSP_CLASSPATH_ATTRIBUTE,
                System.getProperty("java.class.path"));
        
        context.deploy(httpServer);
        
       return httpServer;
    }

    /**
     * Run standalone server
     */
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = startServer();
        try {
            System.out.println(String.format("Jersey app started with WADL available at "
                    + "%sapplication.wadl\nTry out %stest\nHit enter to stop it...",
                    BASE_URI, BASE_URI));
            System.in.read();
        } finally {
            httpServer.stop();
        }
    }
}