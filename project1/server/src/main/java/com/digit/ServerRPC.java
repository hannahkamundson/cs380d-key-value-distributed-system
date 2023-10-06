package com.digit;

public class ServerRPC implements ServerRPCImpl {

    @Override
    public String put(int key, int value) {
        return "Receive a get request: Key = " + key + ", Val = " + value;
    }

    @Override
    public String get(int key) {
        return "Receive a get request: Key = " + key;
    }

    @Override
    public String printKVPairs(int serverId) {
        return "Receive a request printing all KV pairs stored in this server";
    }

    @Override
    public String shutdownServer(int serverId) {
        return "Receive a request for a normal shutdown";
    }
}
