package io.digit.server;

import java.util.Set;

public class ServerSelector {
    public static int select(int key, Set<Integer> servers) {
        // TODO: This doesn't make sense yet
        return key % servers.size();
    }
}
