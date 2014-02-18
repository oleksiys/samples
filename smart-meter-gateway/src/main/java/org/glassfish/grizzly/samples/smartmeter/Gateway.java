package org.glassfish.grizzly.samples.smartmeter;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.grizzly.CloseListener;
import org.glassfish.grizzly.Closeable;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.ICloseType;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.http.server.AddOn;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.impl.FutureImpl;
import org.glassfish.grizzly.memory.Buffers;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.ssl.SSLBaseFilter;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.grizzly.ssl.SSLFilter;
import org.glassfish.grizzly.utils.Futures;

/**
 * Smart Meter Gateway
 */
public class Gateway {

    private static final Logger LOGGER = Grizzly.logger(Gateway.class);

    public static void main(String[] args) {
        final HttpServer server = createServer();
        
        try {
            // Start the server
            server.start();
            
            final Connection connection = establishTlsConnection(server);
            
            final FutureImpl<Connection> closeFuture =
                    Futures.<Connection>createSafeFuture();
            
            connection.addCloseListener(new CloseListener() {

                public void onClosed(final Closeable closeable,
                        final ICloseType type) throws IOException {
                    closeFuture.result(connection);
                }
            });
            
            // waiting for the connection to be closed and shutdown the server
            closeFuture.get();
        } catch (Exception ioe) {
            LOGGER.log(Level.SEVERE, ioe.toString(), ioe);
        } finally {
            server.shutdownNow();
        }
    }

    private static Connection establishTlsConnection(final HttpServer server)
            throws Exception {
        final TCPNIOTransport t = server.getListener("gateway").getTransport();
        final Future<Connection> connectFuture = t.connect(
                Administrator.HOST, Administrator.PORT);
        final Connection c = connectFuture.get();
        
        // it has to trigger client handshake
        c.write(Buffers.EMPTY_BUFFER);
        
        return c;
    }
    
    private static HttpServer createServer() {
        final HttpServer server = new HttpServer();
        final ServerConfiguration config = server.getServerConfiguration();
        
        // Register simple HttpHandler
        config.addHttpHandler(new SimpleHttpHandler(), "/");

        // create a network listener that listens on port 8080.
        final NetworkListener networkListener = new NetworkListener(
                "gateway",
                NetworkListener.DEFAULT_NETWORK_HOST,
                NetworkListener.DEFAULT_NETWORK_PORT);

        // Enable SSL on the listener
        final SSLEngineConfigurator clientSslConfig = createSslConfiguration(true);
        final SSLEngineConfigurator serverSslConfig = createSslConfiguration(false);
        
        networkListener.setSecure(true);
        networkListener.setSSLEngineConfig(serverSslConfig);
        networkListener.registerAddOn(new AddOn() {

            public void setup(final NetworkListener networkListener,
                    final FilterChainBuilder builder) {
                // replace server-side optimized SSLBasicFilter with one, that
                // supports client side
                final int sslFilterIdx = builder.indexOfType(SSLBaseFilter.class);
                assert sslFilterIdx != -1;
                
                builder.set(sslFilterIdx, new SSLFilter(serverSslConfig, clientSslConfig));
            }
        });

        server.addListener(networkListener);
        
        return server;
    }

    /**
     * Initialize SSL configuration.
     *
     * @return server side {@link SSLEngineConfigurator}.
     */
    private static SSLEngineConfigurator createSslConfiguration(final boolean isClient) {
        // Initialize SSLContext configuration
        SSLContextConfigurator sslContextConfig = new SSLContextConfigurator();

        ClassLoader cl = Gateway.class.getClassLoader();
        // Set key store
        URL keystoreUrl = cl.getResource("ssltest-keystore.jks");
        if (keystoreUrl != null) {
            sslContextConfig.setKeyStoreFile(keystoreUrl.getFile());
            sslContextConfig.setKeyStorePass("changeit");
        }

        // Set trust store
        URL cacertsUrl = cl.getResource("ssltest-cacerts.jks");
        if (cacertsUrl != null) {
            sslContextConfig.setTrustStoreFile(cacertsUrl.getFile());
            sslContextConfig.setTrustStorePass("changeit");
        }

        // Create SSLEngine configurator
        return new SSLEngineConfigurator(sslContextConfig.createSSLContext(),
                isClient, false, false);
    }
}
