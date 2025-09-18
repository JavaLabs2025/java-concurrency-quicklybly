package org.labs.orderedlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import org.labs.common.Config;
import org.labs.common.Spoon;
import org.labs.common.Statistic;
import org.labs.orderedlocks.Kitchen.SoupOrderStatus;

public class DiningStudents {

    public static void main(String[] args) {
        var statistic = new Statistic(Config.NUMBER_OF_STUDENTS, Config.NUMBER_OF_WAITERS);

        List<Spoon> spoons = new ArrayList<>(Config.NUMBER_OF_STUDENTS);
        for (int i = 0; i < Config.NUMBER_OF_STUDENTS; ++i) {
            spoons.add(new Spoon(i));
        }

        Kitchen kitchen = new Kitchen();

        BlockingQueue<CompletableFuture<SoupOrderStatus>> orders = new ArrayBlockingQueue<>(
                Config.NUMBER_OF_SOUP + Config.NUMBER_OF_STUDENTS + 1,
                false
        );

        List<Thread> waiters = new ArrayList<>(Config.NUMBER_OF_WAITERS);
        for (int i = 0; i < Config.NUMBER_OF_WAITERS; ++i) {
            var waiterRunnable = new Waiter(i, orders, kitchen, statistic);
            var waiterThread = new Thread(waiterRunnable);
            waiterThread.start();
            waiters.add(waiterThread);
        }

        List<Thread> students = new ArrayList<>(Config.NUMBER_OF_STUDENTS);
        for (int i = 0; i < Config.NUMBER_OF_STUDENTS; ++i) {
            // debug i == zero case
            int leftSpoonId = i != Config.NUMBER_OF_STUDENTS - 1 ?
                    i == 0 ? Config.NUMBER_OF_STUDENTS - 1 : i - 1 : i;
            int rightSpoonId = i != Config.NUMBER_OF_STUDENTS - 1 ? i : i - 1;
            var studentRunnable = new Student(
                    i,
                    statistic,
                    spoons.get(leftSpoonId),
                    spoons.get(rightSpoonId),
                    orders
            );
            var studentThread = new Thread(studentRunnable);
            studentThread.start();
            students.add(studentThread);
        }

        students.forEach((thread -> {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
        waiters.forEach(Thread::interrupt);

        printStatistic(statistic);
    }

    private static void printStatistic(Statistic statistic) {
        StringBuilder sb = new StringBuilder("Statistic:\nStudents: [");

        for (int i = 0; i < Config.NUMBER_OF_STUDENTS; ++i) {
            sb.append(statistic.getStudentStatistic(i));
            if (i != Config.NUMBER_OF_STUDENTS - 1) {
                sb.append(", ");
            }
        }

        sb.append("]\nWaiters: [");

        for (int i = 0; i < Config.NUMBER_OF_WAITERS; ++i) {
            sb.append(statistic.getWaiterStatistic(i));
            if (i != Config.NUMBER_OF_WAITERS - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");

        System.out.println(sb);
    }
}
