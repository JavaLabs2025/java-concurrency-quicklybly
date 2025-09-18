package org.labs.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistic {
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

    public Integer getStudentStatistic(int studentId) {
        return studentStatistic.get(studentId).get();
    }

    public Integer getWaiterStatistic(int waiterId) {
        return waiterStatistic.get(waiterId).get();
    }
}
