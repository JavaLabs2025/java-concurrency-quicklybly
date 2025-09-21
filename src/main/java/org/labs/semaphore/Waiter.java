package org.labs.semaphore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.labs.common.Kitchen;
import org.labs.common.Kitchen.SoupOrderStatus;
import org.labs.common.Statistic;

@Slf4j
@RequiredArgsConstructor
public class Waiter implements Runnable {

    private final Integer id;
    private final BlockingQueue<CompletableFuture<SoupOrderStatus>> orders;
    private final Kitchen kitchen;
    private final Statistic statistic;

    @Override
    public void run() {
        try {
            while (true) {
                CompletableFuture<SoupOrderStatus> order = null;
                try {
                    order = orders.take();
                    SoupOrderStatus orderStatus = kitchen.getSoup();

                    order.complete(orderStatus);

                    if (orderStatus != SoupOrderStatus.OUT_OF_SOUP) {
                        statistic.addWaiterStatistic(id);
                    }
                } finally {
                    if (order != null) {
                        order.complete(SoupOrderStatus.OUT_OF_SOUP);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.info("Waiter {} was interrupted", id);
            Thread.currentThread().interrupt();
        }
    }
}
