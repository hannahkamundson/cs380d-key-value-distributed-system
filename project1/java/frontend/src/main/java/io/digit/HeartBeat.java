package io.digit;

import io.digit.server.ServerRPC;
import lombok.extern.slf4j.Slf4j;

import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class HeartBeat implements Runnable{

    private final int heartbeatTimePeriod = 10;
    //private final ScheduledExecutorService timerService = Executors.newScheduledThreadPool(ServersList.servers.size());

    //ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(ServersList.servers.size());


    @Override
    public void run() {
        log.info("Starting to send heartbeats to everyone");
        while (true) {
            for (Entry<Integer, ServerRPC> entry : ServersList.servers.entrySet()){
                log.info("Checking if server is alive {}", entry.getValue());
                if (!entry.getValue().alive()){
                    ServersList.servers.remove(entry.getKey());
                }
            }
            try {
                log.info("Waiting until the next heartbeat.");
                Thread.sleep(heartbeatTimePeriod);
            } catch (InterruptedException e) {
                log.info("Runtime Err during waiting time for the next heartbeat");
                throw new RuntimeException(e);
            }
        }
    }
}
