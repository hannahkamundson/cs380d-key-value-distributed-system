package io.digit;

public interface DummyServerRpc {
    String shutdownServer();

    String printKVPairs();

    int get(int key);

    String put(int key, int value);

    boolean lock();

    boolean unlock();
}
