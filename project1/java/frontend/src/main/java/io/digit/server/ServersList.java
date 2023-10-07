package io.digit.server;

import io.digit.server.ServerRPC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServersList {
    public static final Map<Integer, ServerRPC> servers = new ConcurrentHashMap<>();
}
