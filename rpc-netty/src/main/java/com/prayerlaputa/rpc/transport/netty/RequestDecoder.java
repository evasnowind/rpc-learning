package com.prayerlaputa.rpc.transport.netty;

import com.prayerlaputa.rpc.transport.command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public class RequestDecoder extends CommandDecoder {
    @Override
    protected Header decodeHeader(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        return new Header(
                byteBuf.readInt(),
                byteBuf.readInt(),
                byteBuf.readInt()
        );
    }
}
