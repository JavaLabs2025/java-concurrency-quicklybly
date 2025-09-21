package org.labs.common;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;

public class Spoon {
    @Getter
    private final Integer id;
    private final Lock lock;

    public Spoon(int id, boolean fairness) {
        this.id = id;
        lock = new ReentrantLock(fairness);
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public boolean tryLock() {
        return lock.tryLock();
    }
}
