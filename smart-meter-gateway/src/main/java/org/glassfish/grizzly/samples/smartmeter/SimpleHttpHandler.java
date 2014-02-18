package org.glassfish.grizzly.samples.smartmeter;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

/**
 * Simple {@link HttpHandler} implementation.
 */
public class SimpleHttpHandler extends HttpHandler{

    @Override
    public void service(final Request request, final Response response)
            throws Exception {
        response.setContentType("text/plain");
        response.getWriter().write("Hello world!");
    }
    
}
