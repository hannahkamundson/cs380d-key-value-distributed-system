package io.digit.server;

import com.digit.server.ServerRPC;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * All things to do with the client for the ServerRPC
 */
public class ServerRPCClient {
    private static final String BASE_HOST = "http://localhost:";
    private static final int BASE_SERVER_PORT = 9000;

    public static ServerRPC create(int serverId)  throws MalformedURLException {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            int serverPort = BASE_SERVER_PORT + serverId;
            config.setServerURL(new URL(String.join(BASE_HOST, ":", String.valueOf(serverPort))));
            config.setEnabledForExtensions(true);
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            ClientFactory factory = new ClientFactory(client);
            return (ServerRPC) factory.newInstance(ServerRPC.class);
        }
}
