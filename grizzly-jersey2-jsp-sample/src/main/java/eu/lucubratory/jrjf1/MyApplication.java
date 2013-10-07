package eu.lucubratory.jrjf1;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

/**
 * Application
 */
public class MyApplication extends ResourceConfig {

    public MyApplication() {
        // Resources.
        register(IndexModel.class);

        // MVC.
        register(JspMvcFeature.class);
    }
}
