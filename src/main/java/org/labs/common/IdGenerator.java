package org.labs.common;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private final AtomicInteger id = new AtomicInteger(0);

    public int getId() {
        return id.getAndIncrement();
    }
}
