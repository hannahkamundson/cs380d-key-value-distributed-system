package com.digit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.digit.server.ServerRPC;
public class ServerRPCImpl implements ServerRPC {

    private final Map<Integer, Integer> servers = new ConcurrentHashMap<>();

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
        StringBuilder keyValues = new StringBuilder();
        for (Map.Entry<Integer,Integer> entry : servers.entrySet()){
            String key = Integer.toString(entry.getKey());
            String value = Integer.toString(entry.getValue());
            keyValues.append("key = " + key + " ,value = " + value);
        }
        return keyValues.toString();
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
