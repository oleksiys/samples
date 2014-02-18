package org.glassfish.grizzly.samples.smartmeter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.http.HttpContent;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.Protocol;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.ssl.SSLBaseFilter;

public class AdministratorFilter extends BaseFilter
        implements SSLBaseFilter.HandshakeListener {

    private static final Logger LOGGER = Grizzly.logger(AdministratorFilter.class);
    @Override
    public NextAction handleRead(final FilterChainContext ctx) throws IOException {
        final HttpContent responseContent = ctx.getMessage();
        
        if (!responseContent.isLast()) {
            // if not last - wait for entire response to come
            return ctx.getStopAction(responseContent);
        }
        
        LOGGER.log(Level.INFO, "Response from {0}\n{1}\n\n{2}",
                new Object[] {ctx.getConnection().getPeerAddress(),
                    responseContent.getHttpHeader().toString(),
                    responseContent.getContent().toStringContent()});
        
        return ctx.getStopAction();
    }

    //------------------ SSLBaseFilter.HandshakeListener --------------------
    public void onStart(final Connection connection) {
        LOGGER.log(Level.INFO, "SSL handshake started {0}", connection);
    }

    public void onComplete(final Connection connection) {
        LOGGER.log(Level.INFO, "SSL handshake completed {0}", connection);
        
        // SSL handshake is completed

        final InetSocketAddress addr =
                (InetSocketAddress) connection.getPeerAddress();
        // Write HTTP request
        HttpRequestPacket request = HttpRequestPacket.builder()
                .method(Method.GET)
                .uri("/test")
                .protocol(Protocol.HTTP_1_1)
                .host(addr.getHostName() + ":" + addr.getPort())
                // use HTTP/1.0, because we don't need keep-alive in this sample
                .header(Header.Connection, "Close")
                .build();

        connection.write(request);
    }
    
}
