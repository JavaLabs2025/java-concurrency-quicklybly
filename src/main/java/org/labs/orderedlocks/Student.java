package org.labs.orderedlocks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.labs.common.Config;
import org.labs.common.Spoon;
import org.labs.common.Statistic;
import org.labs.orderedlocks.Kitchen.SoupOrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Student implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Student.class);

    private final Integer id;
    private final Statistic statistic;
    private final Spoon firstSpoon;
    private final Spoon secondSpoon;
    private final BlockingQueue<CompletableFuture<SoupOrderStatus>> orders;

    public Student(Integer id, Statistic statistic, Spoon firstSpoon, Spoon secondSpoon, BlockingQueue<CompletableFuture<SoupOrderStatus>> orders) {
        this.id = id;
        this.statistic = statistic;
        this.firstSpoon = firstSpoon;
        this.secondSpoon = secondSpoon;
        this.orders = orders;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    speak();

                    synchronized (firstSpoon) {
                        logger.debug("Student {} took spoon with id: {}", id, firstSpoon.getId());
                        synchronized (secondSpoon) {
                            logger.debug("Student {} took spoon with id: {}", id, secondSpoon.getId());

                            CompletableFuture<SoupOrderStatus> order = new CompletableFuture<>();
                            orders.put(order);

                            var orderStatus = order.get();

                            if (orderStatus == SoupOrderStatus.OUT_OF_SOUP) {
                                logger.info("no more food for {}, leaving restaurant", id);
                                return;
                            }

                            Thread.sleep(Config.TIME_TO_EAT_SOUP_MS);
                            statistic.addStudentStatistic(id);
                        }
                        logger.debug("Student {} put down spoon with id: {}", id, secondSpoon.getId());
                    }
                    logger.debug("Student {} put down spoon with id: {}", id, firstSpoon.getId());
                } catch (ExecutionException e) {
                    // should not happen
                    logger.error("Student {} received error, stopping", id, e);
                    return;
                }
            }
        } catch (InterruptedException e) {
            logger.warn("Student {} was interrupted", id);
            Thread.currentThread().interrupt();
        }
    }

    private void speak() throws InterruptedException {
        Thread.sleep(Config.TIME_TO_SPEAK_MS);
    }
}
