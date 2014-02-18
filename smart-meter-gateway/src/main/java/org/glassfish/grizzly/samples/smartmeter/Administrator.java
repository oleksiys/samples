package org.glassfish.grizzly.samples.smartmeter;

import java.io.IOException;
import java.net.URL;
import org.glassfish.grizzly.filterchain.FilterChain;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.http.HttpClientFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.ssl.SSLBaseFilter;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;

/**
 * Smart Meter Gateway Administrator
 */
public class Administrator {
    public static final String HOST = "localhost";
    public static final int PORT = 7777;
    
    public static void main(String[] args) throws IOException {
        final TCPNIOTransport transport = createTlsTransport();
        
        try {
            transport.bind("0.0.0.0", PORT);
            transport.start();
        
            System.out.println("Gateway administrator is running on port " + PORT);
            System.out.println("Press enter to stop...");
            System.in.read();
        } finally {
            transport.shutdown();
        }
    }
    
    private static TCPNIOTransport createTlsTransport() {
        final AdministratorFilter adminFilter = new AdministratorFilter();
        
        final SSLBaseFilter sslFilter = new SSLBaseFilter(createSslConfiguration());
        sslFilter.addHandshakeListener(adminFilter);
        
        // initialize a FilterChain for the Administrator
        final FilterChain chain = FilterChainBuilder.stateless()
                .add(new TransportFilter())
                .add(sslFilter)
                .add(new HttpClientFilter())
                .add(adminFilter)
                .build();
        
        // Create the Transport
        final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance()
                .setProcessor(chain)
                .build();
        
        return transport;
    }

    /**
     * Initialize server side SSL configuration.
     *
     * @return server side {@link SSLEngineConfigurator}.
     */
    private static SSLEngineConfigurator createSslConfiguration() {
        // Initialize SSLContext configuration
        SSLContextConfigurator sslContextConfig = new SSLContextConfigurator();

        ClassLoader cl = Gateway.class.getClassLoader();
        // Set key store
        URL keystoreUrl = cl.getResource("ssltest-keystore.jks");
        if (keystoreUrl != null) {
            sslContextConfig.setKeyStoreFile(keystoreUrl.getFile());
            sslContextConfig.setKeyStorePass("changeit");
        }

        // Create SSLEngine configurator
        return new SSLEngineConfigurator(sslContextConfig.createSSLContext(),
                false, false, false);    
    }
}
