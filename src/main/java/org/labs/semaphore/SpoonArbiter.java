package org.labs.semaphore;

import java.util.concurrent.Semaphore;

public class SpoonArbiter {

    private final Semaphore semaphore;

    public SpoonArbiter(int numberOfStudents, boolean fairness) {
        this.semaphore = new Semaphore(numberOfStudents - 1, fairness);
    }

    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    public void release() {
        semaphore.release();
    }
}
