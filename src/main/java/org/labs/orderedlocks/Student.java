package org.labs.orderedlocks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.labs.common.Kitchen.SoupOrderStatus;
import org.labs.common.Spoon;
import org.labs.common.Statistic;

@Slf4j
@RequiredArgsConstructor
public class Student implements Runnable {

    private final Integer id;
    private final Statistic statistic;
    private final Spoon firstSpoon;
    private final Spoon secondSpoon;
    private final BlockingQueue<CompletableFuture<SoupOrderStatus>> orders;

    private final long speakTimeMs;
    private final long eatTimeMs;

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
