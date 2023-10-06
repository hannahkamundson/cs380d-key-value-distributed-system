package com.digit.server;

public interface ServerRPC {

    String put(int key, int value);

    String get(int key);

    String printKVPairs();

    String shutdownServer(int serverId);

    boolean lock();

    boolean unlock();

}
