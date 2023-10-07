package io.digit.server;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class Retry {
    private static final int RETRIES = 3;

    public static <V> V run(Supplier<V> fn) {
        V returnValue;
        try {
            returnValue = fn.get();
            return returnValue;
        } catch (Exception e) {
            log.info("Starting retries");
            int attempt = 0;
            while (attempt < RETRIES) {
                try {
                    returnValue = fn.get();
                    return returnValue;
                } catch (Exception e2) {
                    // Do nothing
                }
                try {
                    TimeUnit.SECONDS.sleep(attempt + 1);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
                attempt++;
            }

            log.error("Three attempts and failed {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
