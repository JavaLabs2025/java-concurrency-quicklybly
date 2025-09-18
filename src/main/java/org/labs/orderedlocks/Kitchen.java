package org.labs.orderedlocks;

import java.util.concurrent.atomic.AtomicInteger;
import org.labs.common.Config;

public class Kitchen {

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
                System.out.println("Kitchen " + currentSoupCount);
                return SoupOrderStatus.OUT_OF_SOUP;
            }
        } while (!soupCount.compareAndSet(currentSoupCount, currentSoupCount - 1));

        if ((currentSoupCount % 10000) == 0) {
            System.out.println("Kitchen: " + currentSoupCount);
        }
        return SoupOrderStatus.OK;
    }
}
