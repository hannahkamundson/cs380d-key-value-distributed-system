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
        log.info("Starting putting key value {} {}", key, value);
        data.put(key,value);
        log.info("Completed putting key value {} {}", key, value);
        return String.format("Receive a get request: Key = %s, Val = %s, key, value", key, value);
    }

    @Override
    public String get(int key) {
        log.info("Starting getting key's value {}", key);
        while (isLocked) {
            try {
                log.info("Waiting until lock opens");
                wait();
            } catch (InterruptedException e) {
                log.info("Runtime Err while waiting for lock to open during get");
                throw new RuntimeException(e);
            }
        }
        log.info("Thread has been awakened. Key will be returned for {}", key);
        Integer value = data.get(key);
        String readKey;
        if (value == null){
            readKey = String.format("Key %s does not have a value yet!", key);
        } else {
            readKey = String.format("Receive a get request: Key = %s, value=%s", key, value);
        }
        log.info("Completed getting key's value {}", key);
        return readKey;
    }

    @Override
    public String printKVPairs() {
        log.info("Starting to print key-value pairs");
        StringBuilder keyValues = new StringBuilder();
        for (Map.Entry<Integer,Integer> entry : data.entrySet()){
            String key = Integer.toString(entry.getKey());
            String value = Integer.toString(entry.getValue());
            keyValues.append("key = ").append(key).append(" ,value = ").append(value);
        }
        log.info("Completed storing key-value pairs as string");
        return keyValues.toString();
    }


    @Override
    public String shutdownServer() {
        log.info("Starting shutting down the server");
        App.webServer.shutdown();
        log.info("Completed shutting down the server");
        return "Success";
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
            log.info("Starting to send the values to the new server {}", serverId);
            for (Map.Entry<Integer,Integer> entry : data.entrySet()){
                newServer.put(entry.getKey(),entry.getValue());
            }
        } catch (MalformedURLException e) {
            log.info("Runtime Err while trying to send values to new server. {}", serverId);
            throw new RuntimeException(e);
        }

        log.info("Successfully sent values to the server ID {}", serverId);
        return "Success";
    }

    @Override
    public boolean alive() {
        log.info("Checking whether the server is alive");
        return true;
    }
}
