package org.labs.orderedlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.labs.common.Config;
import org.labs.common.Spoon;
import org.labs.common.Statistic;
import org.labs.orderedlocks.Kitchen.SoupOrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiningStudents {

    private static final Logger log = LoggerFactory.getLogger(DiningStudents.class);

    public static void main(String[] args) throws InterruptedException {
        var statistic = new Statistic(Config.NUMBER_OF_STUDENTS, Config.NUMBER_OF_WAITERS);

        List<Spoon> spoons = createSpoons(Config.NUMBER_OF_STUDENTS);

        Kitchen kitchen = new Kitchen();

        BlockingQueue<CompletableFuture<SoupOrderStatus>> orders = new ArrayBlockingQueue<>(
                Config.NUMBER_OF_SOUP + Config.NUMBER_OF_STUDENTS + 1,
                Config.FAIR_IF_POSSIBLE
        );

        List<Thread> waiters = createAndStartWaiters(Config.NUMBER_OF_WAITERS, orders, kitchen, statistic);

        var startTime = System.nanoTime();
        List<Thread> students = createAndStartStudents(Config.NUMBER_OF_STUDENTS, spoons, statistic, orders);

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

    private static List<Spoon> createSpoons(int numberOfSpoons) {
        List<Spoon> spoons = new ArrayList<>(numberOfSpoons);
        for (int i = 0; i < Config.NUMBER_OF_STUDENTS; ++i) {
            spoons.add(new Spoon(i));
        }
        return spoons;
    }

    private static List<Thread> createAndStartWaiters(
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

    private static List<Thread> createAndStartStudents(
            int numberOfStudents,
            List<Spoon> spoons,
            Statistic statistic,
            BlockingQueue<CompletableFuture<SoupOrderStatus>> orders
    ) {
        List<Thread> students = new ArrayList<>(numberOfStudents);
        for (int i = 0; i < numberOfStudents; ++i) {
            // all students except for the last should take left spoon first
            int firstSpoonId = i != Config.NUMBER_OF_STUDENTS - 1 ?
                    i == 0 ? Config.NUMBER_OF_STUDENTS - 1 : i - 1 : i;
            int secondSpoonId = i != Config.NUMBER_OF_STUDENTS - 1 ? i : i - 1;
            var studentRunnable = new Student(
                    i,
                    statistic,
                    spoons.get(firstSpoonId),
                    spoons.get(secondSpoonId),
                    orders
            );
            var studentThread = new Thread(studentRunnable);
            studentThread.start();
            students.add(studentThread);
        }
        return students;
    }
}
