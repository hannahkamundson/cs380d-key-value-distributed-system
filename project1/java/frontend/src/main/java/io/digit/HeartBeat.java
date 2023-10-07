package io.digit;

import com.digit.server.ServerRPC;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HeartBeat implements Runnable{

    private int heartbeatTimePeriod = 10;
    //ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(ServersList.servers.size());


    @Override
    public void run() {
        while (true) {
            for (Entry<Integer, ServerRPC> entry : ServersList.servers.entrySet()){
                if (entry.getValue().alive() != true){
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
