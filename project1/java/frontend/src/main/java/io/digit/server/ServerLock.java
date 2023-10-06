package io.digit.server;

import com.digit.server.ServerRPC;

import java.util.Collection;
import java.util.function.Consumer;

public class ServerLock {
    public static void runWithLock(Collection<ServerRPC> rpcs, Consumer<ServerRPC> fn) {
        // TODO: is this actually in parallel or do we need to work with threads
        lock(rpcs);
        rpcs.parallelStream().forEach(fn);
        unlock(rpcs);
    }

    public static void lock(Collection<ServerRPC> rpcs) {
        rpcs.parallelStream().forEach(ServerRPC::lock);
    }

    public static void unlock(Collection<ServerRPC> rpcs) {
        rpcs.parallelStream().forEach(ServerRPC::unlock);
    }
}
