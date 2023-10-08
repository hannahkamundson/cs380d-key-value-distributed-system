package io.digit.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServersList {
    /**
     * Don't try to lock the servers more than once at a time or modify/access the servers hash map except for reads
     */
    public static final Object serversLock = new Object();
    public static final Map<Integer, ServerRPC> servers = new ConcurrentHashMap<>();
}
