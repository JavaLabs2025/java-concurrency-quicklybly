package org.labs.orderedlocks;

import java.util.concurrent.atomic.AtomicInteger;
import org.labs.common.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kitchen {

    private static final Logger log = LoggerFactory.getLogger(Kitchen.class);
    private final AtomicInteger soupCount = new AtomicInteger(Config.NUMBER_OF_SOUP);

    public enum SoupOrderStatus {
        OK,
        OUT_OF_SOUP,
    }

    public SoupOrderStatus getSoup() {
        Integer currentSoupCount;

        do {
            currentSoupCount = soupCount.get();

            if (currentSoupCount.equals(0)) {
                log.info("Kitchen is out of soup");
                return SoupOrderStatus.OUT_OF_SOUP;
            }
        } while (!soupCount.compareAndSet(currentSoupCount, currentSoupCount - 1));

        if (currentSoupCount % 10_000 == 0) {
            log.debug("Kitchen soup count {}", currentSoupCount);
        }
        return SoupOrderStatus.OK;
    }
}
