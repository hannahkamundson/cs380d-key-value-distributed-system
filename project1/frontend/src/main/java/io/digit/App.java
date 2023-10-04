package io.digit;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;

public class App {
    private static final int PORT = 8001;

    // TODO: Do we want it to die when there is an exception? I'd think not
    public static void main(String[] args) throws XmlRpcException, IOException {
        // Create the handler mapping to our class that handles it
        PropertyHandlerMapping propertyHandlerMapping = new PropertyHandlerMapping();
        propertyHandlerMapping.addHandler("$default", FrontendRPCServerImpl.class);

        // Start the server and add the handler mapping
        WebServer webServer = new WebServer(PORT);
        webServer.getXmlRpcServer().setHandlerMapping(propertyHandlerMapping);
        webServer.start();
    }
}
