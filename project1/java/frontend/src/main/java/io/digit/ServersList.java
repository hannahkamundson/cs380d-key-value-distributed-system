package io.digit;

import com.digit.server.ServerRPC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServersList {
    static Map<Integer, ServerRPC> servers = new ConcurrentHashMap<>();
}
