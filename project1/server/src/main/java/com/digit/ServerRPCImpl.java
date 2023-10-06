package com.digit;

public interface ServerRPCImpl {

    String put(int key, int value);

    String get(int key);

    String printKVPairs(int serverId);

    String shutdownServer(int serverId);

}
