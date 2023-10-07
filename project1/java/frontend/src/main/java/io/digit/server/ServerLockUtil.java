package io.digit.server;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.function.Consumer;

@Slf4j
public class ServerLockUtil {
    public static void runWithLock(Collection<ServerRPC> rpcs, Consumer<ServerRPC> fn) {
        // TODO: is this actually in parallel or do we need to work with threads
        lock(rpcs);
        rpcs.parallelStream().forEach(fn);
        unlock(rpcs);
    }

    public static void lock(Collection<ServerRPC> rpcs) {
        log.info("Locking all servers");
        for (ServerRPC rpc : rpcs) {
            rpc.lock();
        }
    }

    public static void unlock(Collection<ServerRPC> rpcs) {
        log.info("Unlocking all servers");
        for (ServerRPC rpc : rpcs) {
            rpc.unlock();
        }
    }
}
