package com.prayerlaputa.rpc.transport.netty;

import com.prayerlaputa.rpc.transport.command.Command;
import com.prayerlaputa.rpc.transport.command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public abstract class CommandDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (!byteBuf.isReadable(Integer.BYTES)) {
            return;
        }

        byteBuf.markReaderIndex();

        int length = byteBuf.readInt() - Integer.BYTES;

        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }

        Header header = decodeHeader(channelHandlerContext, byteBuf);
        int payloadLength = length - header.length();
        byte[] payload = new byte[payloadLength];
        byteBuf.readBytes(payload);
        list.add(new Command(header, payload));
    }

    protected abstract Header decodeHeader(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf);
}
