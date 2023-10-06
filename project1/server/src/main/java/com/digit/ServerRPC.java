package com.digit;

public interface ServerRPC {

    String put(int key, int value);

    String get(int key);

    String printKVPairs(int serverId);

    String shutdownServer(int serverId);

}
