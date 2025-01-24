package com.raindrops.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * RandomQueue
 *
 * @author raindrops
 */
public class RandomQueue<T> {

    private final List<T> list;

    private final Random random;

    public RandomQueue() {
        this.list = new ArrayList<>();
        this.random = new Random();
    }

    public void offer(T t) {
        this.list.add(t);
    }

    public T poll() {
        if (list.isEmpty()) {
            return null;
        }
        int index = this.random.nextInt(this.list.size());
        return this.list.remove(index);
    }
}