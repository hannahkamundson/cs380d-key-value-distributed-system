package io.digit;

import io.digit.server.ServerRPC;
import io.digit.server.ServerRPCClient;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServerRPCImpl implements ServerRPC {

    private final Map<Integer, Integer> data = new ConcurrentHashMap<>();
    private static boolean isLocked = false;

    @Override
    public String put(int key, int value) {
        data.put(key,value);
        return String.format("Receive a get request: Key = %s, Val = %s, key, value", key, value);
    }

    @Override
    public String get(int key) {
        while (isLocked) {
            try {
                log.info("Waiting until lock opens");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Thread has been notified. Key will be returned for {}", key);
        Integer value = data.get(key);
        String readKey;
        if (value == null){
            readKey = String.format("Key %s does not have a value yet!", key);
        } else {
            readKey = String.format("Receive a get request: Key = %s, value=%s", key, value);
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
        log.info("Locking the system");
        isLocked = true;

        log.info("Successfully locked the system");
        return true;
    }

    @Override
    public boolean unlock() {
        log.info("Unlocking the system");
        isLocked = false;
        notifyAll();
        log.info("Successfully unlocked the system");
        return true;
    }

    @Override
    public String sendValuesToServer(int serverId) {
        log.info("Sending values to the server ID {}", serverId);
        try {
            ServerRPC newServer = ServerRPCClient.create(serverId);
            for (Map.Entry<Integer,Integer> entry : data.entrySet()){
                newServer.put(entry.getKey(),entry.getValue());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        log.info("Successfully sent values to the server ID {}", serverId);
        return "Success";
    }
}
