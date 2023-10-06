package io.digit;

import com.digit.server.ServerRPC;
import io.digit.server.ServerRPCClient;
import io.digit.server.ServerSelector;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FrontendRPCImpl implements FrontendRPC {
    private final Map<Integer, ServerRPC> servers = new ConcurrentHashMap<>();

    @Override
    public String put(int key, int value) {
        Collection<ServerRPC> rpcs = servers.values();
        // First we need to tell all servers to lock
        // TODO: is this actually in parallel or do we need to work with threads
        rpcs.parallelStream().forEach(ServerRPC::lock);

        // Then we need to write the value
        rpcs.parallelStream().forEach(rpc -> rpc.put(key, value));

        // Then we need to unlock
        rpcs.parallelStream().forEach(ServerRPC::unlock);
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

        // Create the RPC client for the new port
        servers.put(serverId, serverRpc);

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
