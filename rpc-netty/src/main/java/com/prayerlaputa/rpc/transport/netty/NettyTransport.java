package com.prayerlaputa.rpc.transport.netty;

import com.prayerlaputa.rpc.transport.InFlightRequests;
import com.prayerlaputa.rpc.transport.ResponseFuture;
import com.prayerlaputa.rpc.transport.Transport;
import com.prayerlaputa.rpc.transport.command.Command;
import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;

/**
 * @author chenglong.yu
 * created on 2020/9/4
 */
public class NettyTransport implements Transport {

    private final Channel channel;
    private final InFlightRequests inFlightRequests;

    public NettyTransport(Channel channel, InFlightRequests inFlightRequests) {
        this.channel = channel;
        this.inFlightRequests = inFlightRequests;
    }


    @Override
    public CompletableFuture<Command> send(Command request) {
        CompletableFuture<Command> completableFuture = new CompletableFuture<>();

        try {
            /*
             将在途请求放到inFlightRequests中
             在途请求：发出了请求但还没有收到响应
             */
            inFlightRequests.put(new ResponseFuture(request.getHeader().getRequestId(), completableFuture));
            //发送命令
            channel.writeAndFlush(request)
                    /*
                    处理发送失败的情况

                    已经发出去的请求，有可能会因为网络连接断开或者对方进程崩溃等各种异常情况，
                    永远都收不到响应。那为了确保这些孤儿 ResponseFuture 不会在内存中越积越多，
                    我们必须要捕获所有的异常情况，结束对应的 ResponseFuture。
                    所以，我们在两个地方都做了异常处理，分别应对发送失败和发送异常两种情况。
                     */
                    .addListener(channelFuture -> {
                        if (!channelFuture.isSuccess()) {
                            completableFuture.completeExceptionally(channelFuture.cause());
                            channel.close();
                        }
                    });
        } catch (Throwable t) {
            //处理发送异常
            inFlightRequests.remove(request.getHeader().getRequestId());
            completableFuture.completeExceptionally(t);
        }
        return completableFuture;
    }
}
