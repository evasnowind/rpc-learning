package com.prayerlaputa.rpc.transport.netty;

import com.prayerlaputa.rpc.transport.InFlightRequests;
import com.prayerlaputa.rpc.transport.Transport;
import com.prayerlaputa.rpc.transport.TransportClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public class NettyClient implements TransportClient {

    private EventLoopGroup ioEventGroup;
    private Bootstrap bootstrap;
    private final InFlightRequests inFlightRequests;
    private List<Channel> channels = new LinkedList<>();

    public NettyClient() {
        this.inFlightRequests = new InFlightRequests();
    }

    @Override
    public Transport createTransport(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        return new NettyTransport(createChannel(address, connectionTimeout), inFlightRequests);
    }

    private Bootstrap createBootstrap(ChannelHandler channelHandler, EventLoopGroup ioEventGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .group(ioEventGroup)
                .handler(channelHandler)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return bootstrap;
    }

    @Override
    public void close() {
        for (Channel channel : channels) {
            if(null != channel) {
                channel.close();
            }
        }
        if (ioEventGroup != null) {
            ioEventGroup.shutdownGracefully();
        }
        inFlightRequests.close();
    }


    private synchronized Channel createChannel(SocketAddress address, long connectionTimeout)  throws InterruptedException, TimeoutException {
        if (address == null) {
            throw new IllegalArgumentException("address must not be null!");
        }
        if (ioEventGroup == null) {
            ioEventGroup = createIoEventGroup();
        }
        if (bootstrap == null){
            ChannelHandler channelHandlerPipeline = createChannelHandlerPipeline();
            bootstrap = createBootstrap(channelHandlerPipeline, ioEventGroup);
        }

        ChannelFuture channelFuture;
        Channel channel;
        channelFuture = bootstrap.connect(address);

        if (!channelFuture.await(connectionTimeout)) {
            throw new TimeoutException();
        }

        channel = channelFuture.channel();
        if (null == channel || !channel.isActive()) {
            throw new IllegalStateException();
        }
        channels.add(channel);
        return channel;
    }

    private ChannelHandler createChannelHandlerPipeline() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline()
                        //????
                        .addLast(new ResponseDecoder())
                        .addLast(new RequestEncoder())
                        .addLast(new ResponseInvocation(inFlightRequests));
            }
        };
    }

    private EventLoopGroup createIoEventGroup() {

        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
    }
}
