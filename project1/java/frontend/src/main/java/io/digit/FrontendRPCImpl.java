package io.digit;

import com.digit.server.ServerRPC;
import com.digit.server.ServerRPCClient;
import io.digit.server.ServerLock;
import io.digit.server.ServerSelector;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FrontendRPCImpl implements FrontendRPC {
    private final Map<Integer, ServerRPC> servers = new ConcurrentHashMap<>();

    @Override
    public String put(int key, int value) {
        // TODO do we need to do a copy of this?
        ServerLock.runWithLock(servers.values(), rpc -> rpc.put(key, value));
        return String.format("Success %s=%s", key, value);
    }

    @Override
    public String get(int key) {
        int serverId = ServerSelector.select(key, servers.keySet());
        return String.valueOf(servers.get(serverId).get(key));
    }

    @Override
    public String printKVPairs(int serverId) {
        ServerRPC serverRpc = servers.get(serverId);

        // Make sure the server exists
        if (serverRpc == null) {
            return String.format("The server is not registered: %s", serverId);
        }

        return serverRpc.printKVPairs();
    }

    @Override
    public String addServer(int serverId) {
        // Make sure we aren't overwriting a server
        if (servers.containsKey(serverId)) {
            return String.format("The server already exists: %s", serverId);
        }

        ServerRPC serverRpc;

        try {
            serverRpc = ServerRPCClient.create(serverId);
        } catch (Exception e) {
            return String.format("The port is not accepting messages: %s: ", serverId) + e.getMessage();
        }


        Collection<ServerRPC> rpcs = servers.values();

        // Make sure we have another server
        Optional<Integer> sendingServerId = servers.keySet().stream().findFirst();

        // If there aren't any other servers, we don't need to move data over
        if (sendingServerId.isEmpty()) {
            return "Success";
        }

        // Send locks to all the servers
        ServerLock.lock(rpcs);

        // Tell one server to send all data to other server
        servers.get(sendingServerId.get()).sendValuesToServer(serverId);

        // Create the RPC client for the new port
        servers.put(serverId, serverRpc);
        
        // Unlock all servers
        ServerLock.unlock(rpcs);

        return "Success";
    }
    
    @Override
    public String listServer() {
        return "[" + servers.keySet().stream().map(Object::toString).collect(Collectors.joining(" ")) + "]";
    }

    @Override
    public String shutdownServer(int serverId) {
        ServerRPC serverRpc = servers.get(serverId);

        // Male sure the server exists
        if (serverRpc == null) {
            return String.format("The server is not registered: %s", serverId);
        }

        // Shut it down before removing it from our list and then remove it
        String message = serverRpc.shutdownServer();
        servers.remove(serverId);

        return message;
    }
}
