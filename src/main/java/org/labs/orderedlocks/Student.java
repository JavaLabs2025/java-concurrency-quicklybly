package org.labs.orderedlocks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.labs.common.Config;
import org.labs.common.Spoon;
import org.labs.common.Statistic;
import org.labs.orderedlocks.Kitchen.SoupOrderStatus;

public class Student implements Runnable {

    private final Integer id;
    private final Statistic statistic;
    private final Spoon leftSpoon;
    private final Spoon rightSpoon;
    private final BlockingQueue<CompletableFuture<SoupOrderStatus>> orders;

    public Student(Integer id, Statistic statistic, Spoon leftSpoon, Spoon rightSpoon, BlockingQueue<CompletableFuture<SoupOrderStatus>> orders) {
        this.id = id;
        this.statistic = statistic;
        this.leftSpoon = leftSpoon;
        this.rightSpoon = rightSpoon;
        this.orders = orders;
    }

    @Override
    public void run() {
        var name = Thread.currentThread().getName();
        try {
            while (true) {
                try {
                    speak();

                    synchronized (leftSpoon) {
                        System.out.println(name + " took spoon with id: " + leftSpoon.getId());
                        synchronized (rightSpoon) {
                            System.out.println(name + " took spoon with id: " + rightSpoon.getId());
                            CompletableFuture<SoupOrderStatus> order = new CompletableFuture<>();
                            orders.put(order);

                            var orderStatus = order.get();

                            if (orderStatus == SoupOrderStatus.OUT_OF_SOUP) {
                                return;
                            }

                            Thread.sleep(Config.TIME_TO_EAT_SOUP_MS);
                            statistic.addStudentStatistic(id);
                        }
                    }
                    // todo execution ex
                } catch (ExecutionException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void speak() throws InterruptedException {
        Thread.sleep(Config.TIME_TO_SPEAK_MS);
    }
}
