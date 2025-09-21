package org.labs.orderedlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.labs.common.Config;
import org.labs.common.Spoon;
import org.labs.common.Statistic;
import org.labs.orderedlocks.Kitchen.SoupOrderStatus;

@Slf4j
public class DiningStudentsSimulation {

    private final Config config;

    public DiningStudentsSimulation(Config config) {
        this.config = config;
    }

    public void simulate() throws InterruptedException {
        var statistic = new Statistic(config.NUMBER_OF_STUDENTS, config.NUMBER_OF_WAITERS);

        List<Spoon> spoons = createSpoons(config.NUMBER_OF_STUDENTS);

        Kitchen kitchen = new Kitchen(config.NUMBER_OF_SOUP);

        BlockingQueue<CompletableFuture<SoupOrderStatus>> orders = new ArrayBlockingQueue<>(
                config.NUMBER_OF_SOUP + config.NUMBER_OF_STUDENTS + 1,
                config.FAIR_IF_POSSIBLE
        );

        List<Thread> waiters = createAndStartWaiters(config.NUMBER_OF_WAITERS, orders, kitchen, statistic);

        var startTime = System.nanoTime();
        List<Thread> students = createAndStartStudents(config.NUMBER_OF_STUDENTS, spoons, statistic, orders);

        students.forEach((thread -> {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
        var endTime = System.nanoTime();

        for (var waiter : waiters) {
            waiter.interrupt();
            waiter.join();
        }

        statistic.printStatistic();
        log.info("Time: {} ms", TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
    }

    private List<Spoon> createSpoons(int numberOfSpoons) {
        List<Spoon> spoons = new ArrayList<>(numberOfSpoons);
        for (int i = 0; i < config.NUMBER_OF_STUDENTS; ++i) {
            spoons.add(new Spoon(i, config.FAIR_IF_POSSIBLE));
        }
        return spoons;
    }

    private List<Thread> createAndStartWaiters(
            int numberOfWaiters,
            BlockingQueue<CompletableFuture<SoupOrderStatus>> orders,
            Kitchen kitchen,
            Statistic statistic
    ) {
        List<Thread> waiters = new ArrayList<>(numberOfWaiters);
        for (int i = 0; i < numberOfWaiters; ++i) {
            var waiterRunnable = new Waiter(i, orders, kitchen, statistic);
            var waiterThread = new Thread(waiterRunnable);
            waiterThread.start();
            waiters.add(waiterThread);
        }
        return waiters;
    }

    private List<Thread> createAndStartStudents(
            int numberOfStudents,
            List<Spoon> spoons,
            Statistic statistic,
            BlockingQueue<CompletableFuture<SoupOrderStatus>> orders
    ) {
        List<Thread> students = new ArrayList<>(numberOfStudents);
        for (int i = 0; i < numberOfStudents; ++i) {
            // all students except for the last should take left spoon first
            int firstSpoonId = i != config.NUMBER_OF_STUDENTS - 1 ?
                    i == 0 ? config.NUMBER_OF_STUDENTS - 1 : i - 1 : i;
            int secondSpoonId = i != config.NUMBER_OF_STUDENTS - 1 ? i : i - 1;
            var studentRunnable = new Student(
                    i,
                    statistic,
                    spoons.get(firstSpoonId),
                    spoons.get(secondSpoonId),
                    orders,
                    config.TIME_TO_SPEAK_MS,
                    config.TIME_TO_EAT_SOUP_MS
            );
            var studentThread = new Thread(studentRunnable);
            studentThread.start();
            students.add(studentThread);
        }
        return students;
    }
}
