package example.jersey;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import example.guice.Service;
//import com.sun.jersey.api.core.PackagesResourceConfig;
//import com.sun.jersey.api.core.ResourceConfig;
//import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * User: Renato
 */
public class Main extends GuiceServletContextListener {

    public static Injector injector;
    
    @Override
    protected Injector getInjector() {
        System.out.println("Getting injector");
        // return null;
        // final ResourceConfig rc = new PackagesResourceConfig( "com.aptusinteractive.server" );

        // return Guice.createInjector( new ServletModule() {
        // @Override
        // protected void configureServlets() {
        // bind( new TypeLiteral<Dao<String>>() {
        // } ).to( StuffDao.class );
        //
        // for ( Class<?> resource : rc.getClasses() ) {
        // System.out.println( "Binding resource: " + resource.getName() );
        // bind( resource );
        // }
        //
        // serve( "/services/*" ).with( GuiceContainer.class );
        // }
        // } );

        injector = Guice.createInjector(new ServletModule() {
            // Configure your IOC
            @Override
            protected void configureServlets() {
                bind(Service.class);
            }
        });
        
        return injector;

    }
}
