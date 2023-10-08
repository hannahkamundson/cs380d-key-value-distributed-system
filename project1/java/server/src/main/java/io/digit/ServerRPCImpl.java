package io.digit;

import io.digit.server.Retry;
import io.digit.server.ServerRPC;
import io.digit.server.ServerRPCClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServerRPCImpl implements ServerRPC {

    private final Map<Integer, Integer> data = new ConcurrentHashMap<>();
    private static boolean isLocked = false;

    private static final Object canWriteLock = new Object();

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
        // Make sure there are no other uncompleted writes exist
        while (isLocked) {
            try {
                log.info("Waiting until lock opens");
                synchronized(canWriteLock) {
                    canWriteLock.wait();
                }
            } catch (InterruptedException e) {
                log.info("Runtime Err while waiting for lock to open during get");
                throw new RuntimeException(e);
            }
        }
        log.info("Thread has been awakened. Key will be returned for {}", key);
        Integer value = data.get(key);
        String readKey;
        if (value == null){
            readKey = "ERR_KEY";
        } else {
            readKey = String.format("%s:%s", key, value);
        }
        log.info("Completed getting key's value {}", key);
        return readKey;
    }

    @Override
    public String printKVPairs() {
        log.info("Starting to print key-value pairs");
        // Make sure there is no uncompleted write happening
        while (isLocked) {
            try {
                log.info("Waiting until lock opens for kv pairs");
                synchronized(canWriteLock) {
                    canWriteLock.wait();
                }
            } catch (InterruptedException e) {
                log.info("Runtime Err while waiting for lock to open during kv pairs print");
                throw new RuntimeException(e);
            }
        }
        StringBuilder keyValues = new StringBuilder();

        for (Map.Entry<Integer,Integer> entry : data.entrySet()){
            String key = Integer.toString(entry.getKey());
            String value = Integer.toString(entry.getValue());
<<<<<<< HEAD
            //keyValues.append("key = ").append(key).append(" ,value = ").append(value);
            keyValues.append(key).append(":").append(value);
=======
            keyValues.append(key).append(":").append(value).append("\n");
>>>>>>> origin/master
        }
        log.info("Completed storing key-value pairs as string");
        return keyValues.toString();
    }


    @Override
    public String shutdownServer() {
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
        try {
            synchronized (canWriteLock) {
                log.info("Notifying all threads in can write lock");
                canWriteLock.notifyAll();
            }
        } catch (Exception e) {
            log.error("Issue notifying {}", e.getMessage(), e);
        }
        log.info("Successfully unlocked the system");
        return true;
    }

    @Override
    public String sendValuesToServer(int serverId) {
        log.info("Sending values to the server ID {}", serverId);
        // Sending all existing key-value pairs to the newly added server
        try {
            ServerRPC newServer = ServerRPCClient.create(serverId);
            log.info("Starting to send the values to the new server {}", serverId);
            log.info("There are this number of values to send {}", data.size());
            for (Map.Entry<Integer,Integer> entry : data.entrySet()){
                log.info("Submitting key {}", entry.getKey());
                Retry.run(() -> newServer.put(entry.getKey(),entry.getValue()));
                log.info("Submitted key {}", entry.getKey());
            }
        } catch (Exception e) {
            log.error("Runtime Err while trying to send values to new server. {}", e.getMessage(), e);
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
