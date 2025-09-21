package org.labs.orderedlocks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import org.labs.common.Statistic;
import org.labs.orderedlocks.Kitchen.SoupOrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Waiter implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Waiter.class);

    private final Integer id;
    private final BlockingQueue<CompletableFuture<SoupOrderStatus>> orders;
    private final Kitchen kitchen;
    private final Statistic statistic;

    public Waiter(Integer id, BlockingQueue<CompletableFuture<SoupOrderStatus>> orders, Kitchen kitchen, Statistic statistic) {
        this.id = id;
        this.orders = orders;
        this.kitchen = kitchen;
        this.statistic = statistic;
    }

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
            logger.info("Waiter {} was interrupted", id);
            Thread.currentThread().interrupt();
        }
    }
}
