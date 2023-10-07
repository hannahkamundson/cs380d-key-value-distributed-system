package com.digit;

import com.digit.server.ServerRPC;
import com.digit.util.ProcessorFactoryFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;


public class App {
    private static final int Base_Port = 9000;
    private static int serverId = 0;
    static WebServer webServer = null;

    // TODO: Do we want it to die when there is an exception? I'd think not
    public static void main( String[] args ) throws XmlRpcException, IOException {
        ArgumentParser parser = ArgumentParsers.newFor("App").build().defaultHelp(true)
                .description("To be Added");
        parser.addArgument("-i", "--id")
                .nargs(1)
                .type(Integer.class)
                .metavar("I")
                .help("Server id (required)")
                .dest("serverId")
                .required(true);
        Namespace ns;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            throw new RuntimeException(e);
        }

        serverId = ns.getInt("serverId");

        PropertyHandlerMapping propertyHandlerMapping = new PropertyHandlerMapping();
        // Create a new process factory, so we can maintain state across rpc calls
        ServerRPCImpl serverRpc = new ServerRPCImpl();
        propertyHandlerMapping.setRequestProcessorFactoryFactory(new ProcessorFactoryFactory(serverRpc));
        propertyHandlerMapping.setVoidMethodEnabled(true);
        propertyHandlerMapping.addHandler("default", ServerRPC.class);

        // Start the server and add the handler mapping
        webServer = new WebServer(Base_Port);
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

        xmlRpcServer.setHandlerMapping(propertyHandlerMapping);
        webServer.start();
    }
}
