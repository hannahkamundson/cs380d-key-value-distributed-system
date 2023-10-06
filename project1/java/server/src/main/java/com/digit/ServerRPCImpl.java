package com.digit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.digit.server.ServerRPC;
public class ServerRPCImpl implements ServerRPC {

    private final Map<Integer, Integer> data = new ConcurrentHashMap<>();

    @Override
    public String put(int key, int value) {
        servers.put(key,value);
        String writeKey = "Receive a get request: Key = " + Integer.toString(key) + ", Val = " + Integer.toString(value);
        return writeKey;
    }

    @Override
    public String get(int key) {
        Integer value = servers.get(key);
        String readKey = "";
        if (value == null){
            readKey = Integer.toString(key) + "does not have a value yet!";
        }else {
            readKey = "Receive a get request: Key = " + Integer.toString(key);
        }
        return readKey;
    }

    @Override
    public String printKVPairs() {
        StringBuilder keyValues = new StringBuilder();
        for (Map.Entry<Integer,Integer> entry : data.entrySet()){
            String key = Integer.toString(entry.getKey());
            String value = Integer.toString(entry.getValue());
            keyValues.append("key = ").append(key).append(" ,value = ").append(value);
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
