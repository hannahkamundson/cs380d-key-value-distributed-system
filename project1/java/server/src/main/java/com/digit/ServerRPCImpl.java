package com.digit;


import com.digit.server.ServerRPC;

public class ServerRPCImpl implements ServerRPC {

    @Override
    public String put(int key, int value) {
        return "Receive a get request: Key = " + key + ", Val = " + value;
    }

    @Override
    public String get(int key) {
        return "Receive a get request: Key = " + key;
    }

    @Override
    public String printKVPairs() {
        return "Receive a request printing all KV pairs stored in this server";
    }

    @Override
    public String shutdownServer() {
        return "Receive a request for a normal shutdown";
    }

    @Override
    public boolean lock() {
        return true;
    }

    @Override
    public boolean unlock() {
        return true;
    }
}
