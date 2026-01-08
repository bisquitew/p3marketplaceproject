package util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private final AtomicLong counter;

    public IdGenerator(long startAt) {
        this.counter = new AtomicLong(startAt);
    }

    public long nextId() {
        return counter.incrementAndGet();
    }
}
