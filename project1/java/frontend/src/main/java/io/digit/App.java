package io.digit;

import io.digit.util.ProcessorFactoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;

@Slf4j
public class App {
    private static final int PORT = 8001;

    // TODO: Do we want it to die when there is an exception? I'd think not
    public static void main(String[] args) throws XmlRpcException, IOException {
        // Create the handler mapping to our class that handles it
        PropertyHandlerMapping propertyHandlerMapping = new PropertyHandlerMapping();
        // Create a new process factory, so we can maintain state across rpc calls
        FrontendRPCImpl frontendRpc = new FrontendRPCImpl();
        propertyHandlerMapping.setRequestProcessorFactoryFactory(new ProcessorFactoryFactory(frontendRpc));
        propertyHandlerMapping.setVoidMethodEnabled(true);
        propertyHandlerMapping.addHandler("default", FrontendRPC.class);

        // Start the server and add the handler mapping
        WebServer webServer = new WebServer(PORT);
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

        xmlRpcServer.setHandlerMapping(propertyHandlerMapping);
        log.info("Starting the server");
        webServer.start();

        HeartBeat h = new HeartBeat();
        Thread t = new Thread(h);
        log.info("Starting the heartbeats.");
        t.start();
    }
}
