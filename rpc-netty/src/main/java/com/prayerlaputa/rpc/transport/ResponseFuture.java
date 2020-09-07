package com.prayerlaputa.rpc.transport;

import com.prayerlaputa.rpc.transport.command.Command;

import java.util.concurrent.CompletableFuture;

/**
 * @author chenglong.yu
 * created on 2020/9/4
 */
public class ResponseFuture {
    private final int requestId;
    private final CompletableFuture<Command> future;
    private final long timestamp;


    public ResponseFuture(int requestId, CompletableFuture<Command> future) {
        this.requestId = requestId;
        this.future = future;
        this.timestamp = System.nanoTime();
    }

    public int getRequestId() {
        return requestId;
    }

    public CompletableFuture<Command> getFuture() {
        return future;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
