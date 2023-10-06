package com.digit;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Hello world!
 *
 */


public class App  {

    private static final int Base_Port = 9000;
    private static int serverId = 0;

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
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        serverId = ns.getInt("serverId");



        PropertyHandlerMapping propertyHandlerMapping = new PropertyHandlerMapping();
        propertyHandlerMapping.addHandler("default", ServerRPCImpl.class);

        // Start the server and add the handler mapping
        WebServer webServer = new WebServer(Base_Port);
        webServer.getXmlRpcServer().setHandlerMapping(propertyHandlerMapping);
        webServer.start();

    }


}
