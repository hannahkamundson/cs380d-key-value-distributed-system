package com.digit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.digit.server.ServerRPC;
public class ServerRPCImpl implements ServerRPC {

    private final Map<Integer, Integer> data = new ConcurrentHashMap<>();

    @Override
    public String put(int key, int value) {
        data.put(key,value);
        return "Receive a get request: Key = " + Integer.toString(key) + ", Val = " + Integer.toString(value);
    }

    @Override
    public String get(int key) {
        Integer value = data.get(key);
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

    @Override
    public String sendValuesToServer(int serverId) {
        // TODO: send all the values in the map to the server
        // Use ServerRPCClient.create() to get the rpc and then loop through the data sending one at a time (unless
        // we want to add a batch function)
        return "Success";
    }
}
