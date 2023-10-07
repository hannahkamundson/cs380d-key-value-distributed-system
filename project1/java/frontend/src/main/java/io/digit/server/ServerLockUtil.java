package io.digit.server;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Function;

@Slf4j
public class ServerLockUtil {
    public static <V> void runWithLock(Function<ServerRPC, V> fn) {
        // TODO: is this actually in parallel or do we need to work with threads
        lock();
        for (Map.Entry<Integer, ServerRPC> rpc : ServersList.servers.entrySet()) {
            log.info("Submitting function for {}", rpc.getKey());
            Retry.run(() -> fn.apply(rpc.getValue()));
        }
        unlock();
    }

    public static void lock() {
        log.info("Locking all servers");
        for (Map.Entry<Integer, ServerRPC> rpc : ServersList.servers.entrySet()) {
            try {
                log.info("Submitting lock for {}", rpc.getKey());
                Retry.run(() -> rpc.getValue().lock());
            } catch (Exception e) {
                log.error("Exception when attempting to lock {}", e.getMessage(), e);
            }
        }
        log.info("Successfully locked all servers");
    }

    public static void unlock() {
        log.info("Unlocking all servers");
        for (Map.Entry<Integer, ServerRPC> rpc : ServersList.servers.entrySet()) {
            log.info("Submitting unlock for {}", rpc.getKey());
            Retry.run(() -> rpc.getValue().unlock());
        }
        log.info("Successfully unlocked all servers");
    }
}
