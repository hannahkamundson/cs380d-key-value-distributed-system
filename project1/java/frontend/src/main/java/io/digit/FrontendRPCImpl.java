package io.digit;

import io.digit.server.ServerLockUtil;
import io.digit.server.ServerRPC;
import io.digit.server.ServerRPCClient;
import io.digit.server.ServerSelector;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class FrontendRPCImpl implements FrontendRPC {
   // private final Map<Integer, ServerRPC> servers = new ConcurrentHashMap<>();

    /**
     * Don't try to lock the servers more than once at a time
     */
    private final Object serversLockingLock = new Object();

    @Override
    public String put(int key, int value) {
        log.info("Starting putting key value {} {}", key, value);
        // TODO do we need to do a copy of this?
        // TODO: what happens if a server dies in the middle of this?
        synchronized (serversLockingLock) {
            ServerLockUtil.runWithLock(ServersList.servers.values(), rpc -> rpc.put(key, value));
        }

        log.info("Completed putting key value {} {}", key, value);
        return String.format("Success %s=%s", key, value);
    }

    @Override
    public String get(int key) {
        log.info("Starting getting key's value {}", key);
        int serverId = ServerSelector.select(key, ServersList.servers.keySet());
        log.info("Completed getting key's value {}", key);
        return ServersList.servers.get(serverId).get(key);
    }

    @Override
    public String printKVPairs(int serverId) {
        log.info("Starting to ask server to print key-value pairs {}", serverId);
        ServerRPC serverRpc = ServersList.servers.get(serverId);

        // Make sure the server exists
        if (serverRpc == null) {
            log.info("Checking whether the server is registered before asking to print.");
            return String.format("The server is not registered: %s", serverId);
        }
        log.info("Successfully requested the server to print key-value pairs {}", serverId);
        return serverRpc.printKVPairs();
    }

    @Override
    public String addServer(int serverId) {
        log.info("Adding server {}", serverId);
        // Make sure we aren't overwriting a server
        if (ServersList.servers.containsKey(serverId)) {
            return String.format("The server already exists: %s", serverId);
        }

        ServerRPC serverRpc;

        try {
            log.info("Starting to create a server {}", serverId);
            serverRpc = ServerRPCClient.create(serverId);
        } catch (Exception e) {
            log.error("There was an error creating the server rpc {} {}", serverId, e);
            return String.format("The port is not accepting messages: %s: ", serverId) + e.getMessage();
        }

        Collection<ServerRPC> rpcs = ServersList.servers.values();

        // Make sure we have another server
        Optional<Integer> sendingServerId = ServersList.servers.keySet().stream().findFirst();

        // If there aren't any other servers, we don't need to move data over
        if (sendingServerId.isEmpty()) {
            ServersList.servers.put(serverId, serverRpc);
            log.info("Successfully added server {}", serverId);
            return "Success";
        }

        synchronized (serversLockingLock) {
            log.info("Entering add server synchronization with sending server {}", sendingServerId);
            // Send locks to all the servers
            ServerLockUtil.lock(rpcs);

            log.info("Starting to send all the data to the new server {}", serverId);
            // Tell one server to send all data to other server
            ServersList.servers.get(sendingServerId.get()).sendValuesToServer(serverId);

            // Create the RPC client for the new port
            ServersList.servers.put(serverId, serverRpc);

            log.info("Unlocking all servers.");
            // Unlock all servers
            ServerLockUtil.unlock(rpcs);
        }

        log.info("Successfully added server {}", serverId);

        return "Success";
    }
    
    @Override
    public String listServer() {
        log.info("Starting to get the list of servers.");
        return "[" + ServersList.servers.keySet().stream().map(Object::toString).collect(Collectors.joining(" ")) + "]";
    }

    @Override
    public String shutdownServer(int serverId) {
        log.info("Starting to shut down server {}", serverId);
        ServerRPC serverRpc = ServersList.servers.get(serverId);

        log.info("Checking the server exist to be shut down {}", serverId);
        // Male sure the server exists
        if (serverRpc == null) {
            return String.format("The server is not registered: %s", serverId);
        }

        // Shut it down before removing it from our list and then remove it
        String message = serverRpc.shutdownServer();
        ServersList.servers.remove(serverId);

        log.info("Seccussfully shut down server {}", serverId);
        return message;
    }


}
