package org.labs.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Statistic {

    private static final Logger log = LoggerFactory.getLogger(Statistic.class);

    private final List<AtomicInteger> studentStatistic;
    private final List<AtomicInteger> waiterStatistic;

    public Statistic(Integer studentsCount, Integer waitersCount) {
        this.studentStatistic = new ArrayList<>(studentsCount);
        this.waiterStatistic = new ArrayList<>(waitersCount);

        for (int i = 0; i < studentsCount; ++i) {
            this.studentStatistic.add(new AtomicInteger(0));
        }

        for (int i = 0; i < waitersCount; ++i) {
            this.waiterStatistic.add(new AtomicInteger(0));
        }
    }

    public void addStudentStatistic(int studentId) {
        studentStatistic.get(studentId).incrementAndGet();
    }

    public void addWaiterStatistic(int waiterId) {
        waiterStatistic.get(waiterId).incrementAndGet();
    }

    public void printStatistic() {
        StringBuilder sb = new StringBuilder("Statistic:\nStudents: [");

        for (int i = 0; i < studentStatistic.size(); ++i) {
            sb.append(studentStatistic.get(i).get());
            if (i != studentStatistic.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]\nWaiters: [");

        for (int i = 0; i < waiterStatistic.size(); ++i) {
            sb.append(waiterStatistic.get(i).get());
            if (i != waiterStatistic.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");

        log.info(sb.toString());
    }
}
