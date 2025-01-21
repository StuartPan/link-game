/**
 * Copyright (C) 2023, 南京晓鸣信息技术有限公司
 * This file is the private property of the copyright owner. It must not be copied,
 * distributed or used without the copyright owner's authorization.
 * Any unauthorized use, disclosure or distribution is strictly prohibited and may be unlawful.
 * Author: 南京晓鸣信息技术有限公司
 * Version: 1.0
 * License：Proprietary License
 * Created: 2025/1/20
 * Modified: 2025/1/20
 * Contact: 南京晓鸣信息技术有限公司
 */
package com.raindrops.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * RandomQueue
 *
 * @author 南京晓鸣信息技术有限公司
 */
public class RandomQueue<T> {

    private List<T> list;

    private Random random;

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