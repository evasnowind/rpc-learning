package com.prayerlaputa.rpc.transport.netty;

import com.prayerlaputa.rpc.transport.InFlightRequests;
import com.prayerlaputa.rpc.transport.ResponseFuture;
import com.prayerlaputa.rpc.transport.command.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
//?????
@ChannelHandler.Sharable
public class ResponseInvocation extends SimpleChannelInboundHandler<Command> {

    private static final Logger logger = LoggerFactory.getLogger(ResponseInvocation.class);
    private final InFlightRequests inFlightRequests;

    public ResponseInvocation(InFlightRequests inFlightRequests) {
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command response) throws Exception {
        //???
        ResponseFuture future = inFlightRequests.remove(response.getHeader().getRequestId());
        if (null != future) {
            future.getFuture().complete(response);
        } else {
            logger.warn("Drop response: {}", response);
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
