package io.digit;

import io.digit.server.ServerRPC;

import java.util.Map.Entry;

public class HeartBeat implements Runnable{

    private final int heartbeatTimePeriod = 10;
    //ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(ServersList.servers.size());


    @Override
    public void run() {
        while (true) {
            for (Entry<Integer, ServerRPC> entry : ServersList.servers.entrySet()){
                if (!entry.getValue().alive()){
                    ServersList.servers.remove(entry.getKey());
                }
            }
            try {
                Thread.sleep(heartbeatTimePeriod);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
