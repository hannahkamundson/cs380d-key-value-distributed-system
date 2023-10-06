package io.digit;

public interface FrontendRPCServer {

    /**
     * This function routes requests from clients to proper servers that are responsible for inserting a new key-value 
     * pair or updating an existing one.
     */
    String put(int key, int value);

    /**
     * This function routes requests from clients to proper servers that are responsible for getting the value 
     * associated with the given key.
     */
    String get(int key);

    /**
     * This function routes requests to servers matched with the given serverIds.
     */
    String printKVPairs(int serverId);

    /**
     * This function registers a new server with the serverId to the cluster membership.
     */
    String addServer(int serverId);
    
    /**
     * This function prints out a list of servers that are currently active/alive inside the cluster.
     */
    String listServer();

    /**
     * This function routes the shutdown request to a server matched with the specified serverId to let the 
     * corresponding server terminate normally.
     */
    String shutdownServer(int serverId);
}
