package io.digit.server;

import java.util.Set;
import java.util.stream.Collectors;

public class ServerSelector {
    // TODO: Do we want to do something more clever?
    /**
     * Select the server ID based on the
     * @param key The key to map to
     * @param servers A set of all servers
     * @return The server ID associated with the key
     */
    public static int select(int key, Set<Integer> servers) {
        int index = key % servers.size();

        return servers.stream().sorted().collect(Collectors.toList()).get(index);
    }
}
