package io.digit;

import io.digit.server.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class FrontendRPCImpl implements FrontendRPC {
    @Override
    public String put(int key, int value) {
        log.info("Starting putting key value {} {}", key, value);
        // TODO do we need to do a copy of this?
        // TODO: what happens if a server dies in the middle of this?
        synchronized (ServersList.serversLock) {
            ServerLockUtil.runWithLock(rpc -> rpc.put(key, value));
        }

        log.info("Completed putting key value {} {}", key, value);
        return String.format("Success %s=%s", key, value);
    }

    @Override
    public String get(int key) {
        log.info("Starting getting key's value {}", key);
        int serverId = ServerSelector.select(key, ServersList.servers.keySet());
        String value = ServersList.servers.get(serverId).get(key);
        log.info("Completed getting {}", value);
        return value;
    }

    @Override
    public String printKVPairs(int serverId) {
        log.info("Starting to ask server to print key-value pairs {}", serverId);
        ServerRPC serverRpc = ServersList.servers.get(serverId);

        // Make sure the server exists
        if (serverRpc == null) {
            log.info("Checking whether the server is registered before asking to print.");
            return "ERR_NOEXIST";
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

        // Make sure we have another server
        Optional<Integer> sendingServerId = ServersList.servers.keySet().stream().findFirst();

        // If there aren't any other servers, we don't need to move data over
        if (sendingServerId.isEmpty()) {
            synchronized (ServersList.serversLock) {
                ServersList.servers.put(serverId, serverRpc);
            }
            log.info("Successfully added server {}", serverId);
            return "Success";
        }

        synchronized (ServersList.serversLock) {
            log.info("Entering add server synchronization with sending server {}", sendingServerId);
            // Send locks to all the servers
            ServerLockUtil.lock();

            log.info("Starting to send all the data to the new server {}", serverId);
            // Tell one server to send all data to other server
            Retry.run(() -> ServersList.servers.get(sendingServerId.get()).sendValuesToServer(serverId));

            log.info("Data went. Unlocking all servers.");
            // Unlock all servers
            ServerLockUtil.unlock();
            log.info("Unlocked all servers.");

            // Create the RPC client for the new port
            ServersList.servers.put(serverId, serverRpc);
        }

        log.info("Successfully added server {}", serverId);

        return "Success";
    }
    
    @Override
    public String listServer() {
        log.info("Starting to get the list of servers.");
        synchronized (ServersList.serversLock) {
            if (ServersList.servers.isEmpty()) {
                return "ERR_NOSERVERS";
            }

            return ServersList.servers.keySet().stream()
                    .sorted()
                    .map(Object::toString)
                    .collect(Collectors.joining(" "));
        }
    }

    @Override
    public String shutdownServer(int serverId) {
        log.info("Starting to shut down server {}", serverId);
        String message;
        synchronized (ServersList.serversLock) {
            ServerRPC serverRpc = ServersList.servers.get(serverId);

            log.info("Checking the server exist to be shut down {}", serverId);
            // Male sure the server exists
            if (serverRpc == null) {
                return "ERR_NOEXIST";
            }

            // Shut it down before removing it from our list and then remove it
            message = serverRpc.shutdownServer();
            ServersList.servers.remove(serverId);
        }

        log.info("Successfully shut down server {}", serverId);
        return message;
    }


}
