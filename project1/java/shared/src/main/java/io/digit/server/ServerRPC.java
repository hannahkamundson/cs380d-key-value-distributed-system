package io.digit.server;

public interface ServerRPC {

    String put(int key, int value);

    String get(int key);

    String printKVPairs();

    String shutdownServer();

    boolean lock();

    boolean unlock();

    String sendValuesToServer(int serverId);
}
