package io.digit;

import io.digit.server.Retry;
import io.digit.server.ServerRPC;
import io.digit.server.ServersList;
import lombok.extern.slf4j.Slf4j;

import java.util.Map.Entry;

@Slf4j
public class HeartBeat implements Runnable{
    private final int heartbeatTimePeriod = 10;

    @Override
    public void run() {
        log.info("Starting to send heartbeats to everyone");
        while (true) {
            for (Entry<Integer, ServerRPC> entry : ServersList.servers.entrySet()){
                log.info("Checking if server is alive {}", entry.getKey());
                boolean isAlive = false;

                try {
                    isAlive = Retry.run(() -> entry.getValue().alive());
                } catch (Exception e) {
                    // If it isn't working, keep it not alive
                }

                if (!isAlive){
                    synchronized (ServersList.serversLock) {
                        ServersList.servers.remove(entry.getKey());
                    }
                }
            }
            try {
                log.info("Waiting until the next heartbeat.");
                // Wait time between sending two heatbeats
                Thread.sleep(heartbeatTimePeriod);
            } catch (InterruptedException e) {
                log.info("Runtime Err during waiting time for the next heartbeat");
                throw new RuntimeException(e);
            }
        }
    }
}
