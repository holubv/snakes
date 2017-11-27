package com.gmail.holubvojtech.snakes.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;

public class LengthPrepender extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int bodyLen = msg.readableBytes();

        if (bodyLen > 65535) {
            throw new EncoderException("packet body length is too long");
        }

        out.ensureWritable(2 + bodyLen);

        out.writeShort(bodyLen);
        out.writeBytes(msg);
    }
}
