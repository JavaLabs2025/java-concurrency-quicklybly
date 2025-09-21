package org.labs.orderedlocks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.labs.common.Spoon;
import org.labs.common.Statistic;
import org.labs.orderedlocks.Kitchen.SoupOrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Student implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Student.class);

    private final Integer id;
    private final Statistic statistic;
    private final Spoon firstSpoon;
    private final Spoon secondSpoon;
    private final BlockingQueue<CompletableFuture<SoupOrderStatus>> orders;

    private final long speakTimeMs;
    private final long eatTimeMs;

    public Student(
            Integer id,
            Statistic statistic,
            Spoon firstSpoon,
            Spoon secondSpoon,
            BlockingQueue<CompletableFuture<SoupOrderStatus>> orders,
            long speakTimeMs,
            long eatTimeMs
    ) {
        this.id = id;
        this.statistic = statistic;
        this.firstSpoon = firstSpoon;
        this.secondSpoon = secondSpoon;
        this.orders = orders;
        this.speakTimeMs = speakTimeMs;
        this.eatTimeMs = eatTimeMs;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    speak();

                    firstSpoon.lock();
                    try {
                        log.debug("Student {} took spoon with id: {}", id, firstSpoon.getId());
                        secondSpoon.lock();
                        try {
                            log.debug("Student {} took spoon with id: {}", id, secondSpoon.getId());

                            CompletableFuture<SoupOrderStatus> order = new CompletableFuture<>();
                            orders.put(order);

                            var orderStatus = order.get();

                            if (orderStatus == SoupOrderStatus.OUT_OF_SOUP) {
                                log.info("no more food for {}, leaving restaurant", id);
                                return;
                            }

                            Thread.sleep(eatTimeMs);
                            statistic.addStudentStatistic(id);
                        } finally {
                            secondSpoon.unlock();
                            log.debug("Student {} put down spoon with id: {}", id, secondSpoon.getId());
                        }
                    } finally {
                        firstSpoon.unlock();
                        log.debug("Student {} put down spoon with id: {}", id, firstSpoon.getId());
                    }
                } catch (ExecutionException e) {
                    // should not happen
                    log.error("Student {} received error, stopping", id, e);
                    return;
                }
            }
        } catch (InterruptedException e) {
            log.warn("Student {} was interrupted", id);
            Thread.currentThread().interrupt();
        }
    }

    private void speak() throws InterruptedException {
        Thread.sleep(speakTimeMs);
    }
}
