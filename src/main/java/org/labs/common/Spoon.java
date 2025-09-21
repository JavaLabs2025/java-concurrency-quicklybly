package org.labs.common;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Spoon {
    private final Integer id;
    private final Lock lock = new ReentrantLock(Config.FAIR_IF_POSSIBLE);

    public Spoon(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
