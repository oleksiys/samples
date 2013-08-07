package example.guice;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;

@Singleton
public class Service {

    public static final String SERVICE_STRING = "SERVICE_STRING";

    public Service() {
    }

    @Inject
    public String get() {

        return SERVICE_STRING;
    }

}
