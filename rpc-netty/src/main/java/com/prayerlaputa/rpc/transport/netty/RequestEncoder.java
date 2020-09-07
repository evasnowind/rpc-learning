package com.prayerlaputa.rpc.transport.netty;

import com.prayerlaputa.rpc.transport.command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public class RequestEncoder extends CommandEncoder {

    @Override
    protected void encodeHeader(ChannelHandlerContext channelHandlerContext, Header header, ByteBuf byteBuf) throws Exception {
        super.encodeHeader(channelHandlerContext, header, byteBuf);
    }
}
