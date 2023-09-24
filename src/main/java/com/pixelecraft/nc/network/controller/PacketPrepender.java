package com.pixelecraft.nc.network.controller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext p_encode_1_, ByteBuf p_encode_2_, ByteBuf p_encode_3_) throws Exception {
        int i = p_encode_2_.readableBytes();
        int j = PacketByteBuffer.getVarIntSize(i);

        if (j > 3) {
            throw new IllegalArgumentException("unable to fit " + i + " into " + 3);
        } else {
            PacketByteBuffer packetbuffer = new PacketByteBuffer(p_encode_3_);
            packetbuffer.ensureWritable(j + i);
            packetbuffer.writeVarIntToBuffer(i);
            packetbuffer.writeBytes(p_encode_2_, p_encode_2_.readerIndex(), i);
        }
    }
}
