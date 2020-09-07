package com.prayerlaputa.rpc.client;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public class RequestIdSupport {

    private static final AtomicInteger nextRequestId = new AtomicInteger(0);

    public static int next() {
        return nextRequestId.getAndIncrement();
    }
}
