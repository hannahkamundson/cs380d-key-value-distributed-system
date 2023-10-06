package com.digit;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.digit.server.ServerRPC;
import com.digit.server.ServerRPCClient;

public class ServerRPCImpl implements ServerRPC {

    private final Map<Integer, Integer> data = new ConcurrentHashMap<>();
    private static boolean isLocked;

    @Override
    public String put(int key, int value) {
        while (isLocked) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        data.put(key,value);
        return "Receive a get request: Key = " + Integer.toString(key) + ", Val = " + Integer.toString(value);
    }

    @Override
    public String get(int key) {
        while (isLocked) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Integer value = data.get(key);
        String readKey = "";
        if (value == null){
            readKey = Integer.toString(key) + " does not have a value yet!";
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
        isLocked = true;
        return isLocked;
    }

    @Override
    public boolean unlock() {
        isLocked = false;
        notifyAll();
        return isLocked;
    }

    @Override
    public String sendValuesToServer(int serverId) {
        try {
            ServerRPC newServer = ServerRPCClient.create(serverId);
            for (Map.Entry<Integer,Integer> entry : data.entrySet()){
                newServer.put(entry.getKey(),entry.getValue());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return "Success";
    }
}
