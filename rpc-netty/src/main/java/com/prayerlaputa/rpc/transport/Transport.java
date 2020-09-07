package com.prayerlaputa.rpc.transport;

import com.prayerlaputa.rpc.transport.command.Command;

import java.util.concurrent.CompletableFuture;

/**
 * @author chenglong.yu
 * created on 2020/9/4
 */
public interface Transport {

    /**
     * 发送请求命令
     * @param request 请求命令
     * @return 返回值是一个Future，Future
     */
    CompletableFuture<Command> send(Command request);
}
