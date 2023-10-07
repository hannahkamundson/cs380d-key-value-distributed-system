package io.digit.server;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * All things to do with the client for the ServerRPC
 */
public class ServerRPCClient {
    private static final String BASE_HOST = "http://localhost";
    private static final int BASE_SERVER_PORT = 9000;

    public static ServerRPC create(int serverId) throws MalformedURLException {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            int serverPort = ServerRPCClient.serverPort(serverId);
            config.setServerURL(new URL(BASE_HOST +  ":" + String.valueOf(serverPort) + "/xmlrpc"));
            config.setEnabledForExtensions(true);
            config.setConnectionTimeout(0);
            config.setReplyTimeout(0);
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            ClientFactory factory = new ClientFactory(client);
        return (ServerRPC) factory.newInstance(ServerRPC.class);
    }

    public static int serverPort(int serverId) {
        return BASE_SERVER_PORT + serverId;
    }
}
