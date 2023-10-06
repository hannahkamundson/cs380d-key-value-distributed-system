package io.digit;

public class FrontendRPCServerImpl implements FrontendRPCServer {
    @Override
    public String put(int key, int value) {
        return String.format("Put key %s with value %s", key, value);
    }

    @Override
    public String get(int key) {
        return String.format("Getting key %s", key);
    }

    @Override
    public String printKVPairs(int serverId) {
        return "Printing the KVs";
    }

    @Override
    public String addServer(int serverId) {
        return String.format("Adding server %s", serverId);
    }
    
    @Override
    public String listServer() {
        return "I changed this";
    }

    @Override
    public String shutdownServer(int serverId) {
        return String.format("Shutting down server %s", serverId);
    }
    
}
