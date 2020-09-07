package com.prayerlaputa.rpc.transport.netty;

import com.prayerlaputa.rpc.transport.RequestHandler;
import com.prayerlaputa.rpc.transport.RequestHandlerRegistry;
import com.prayerlaputa.rpc.transport.command.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * @author chenglong.yu
 * created on 2020/9/5
 */
@ChannelHandler.Sharable
public class RequestInvocation extends SimpleChannelInboundHandler<Command> {

    private static final Logger logger = LoggerFactory.getLogger(RequestInvocation.class);
    private final RequestHandlerRegistry requestHandlerRegistry;

    public RequestInvocation(RequestHandlerRegistry requestHandlerRegistry) {
        this.requestHandlerRegistry = requestHandlerRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command request) throws Exception {
        RequestHandler handler = requestHandlerRegistry.get(request.getHeader().getType());
        if (null != handler) {
            Command response = handler.handle(request);
            if (null != response) {
                channelHandlerContext.writeAndFlush(response)
                        .addListener(channelFuture ->{
                           if (!channelFuture.isSuccess()) {
                               //????
                               logger.warn("Write response failed!", channelFuture.cause());
                               channelHandlerContext.channel().close();
                           }
                        });
            } else {
                logger.warn("Response is null!");
            }
        } else {
            throw new Exception(String.format("No handler for request with type: %d!", request.getHeader().getType()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Exception: ", cause);

        super.exceptionCaught(ctx, cause);

        Channel channel = ctx.channel();
        if(channel.isActive()) {
            ctx.close();
        }
    }
}
