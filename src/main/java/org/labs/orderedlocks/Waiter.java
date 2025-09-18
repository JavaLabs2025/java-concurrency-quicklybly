package org.labs.orderedlocks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import org.labs.common.Statistic;
import org.labs.orderedlocks.Kitchen.SoupOrderStatus;

public class Waiter implements Runnable {

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

    // Почитать про реордеринг условия в while
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
                } catch (InterruptedException e) {
                    if (order != null) {
                        order.completeExceptionally(e);
                    }
                    Thread.currentThread().interrupt();
                    throw e;
                } finally {
                    if (order != null) {
                        order.completeExceptionally(new RuntimeException("bla-bla"));
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
