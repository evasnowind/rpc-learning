package com.prayerlaputa.rpc.transport.netty;

import com.prayerlaputa.rpc.transport.command.Command;
import com.prayerlaputa.rpc.transport.command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public abstract class CommandEncoder extends MessageToByteEncoder {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) throws Exception {
        if (!(obj instanceof Command)) {
            throw new Exception(String.format("Unknown type: %s!", obj.getClass().getCanonicalName()));
        }

        Command command = (Command) obj;
        byteBuf.writeInt(Integer.BYTES
                + command.getHeader().length()
                + command.getPayload().length);
        encodeHeader(channelHandlerContext, command.getHeader(), byteBuf);
        byteBuf.writeBytes(command.getPayload());
    }

    protected void encodeHeader(ChannelHandlerContext channelHandlerContext, Header header, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(header.getType());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeInt(header.getRequestId());
    }

}
