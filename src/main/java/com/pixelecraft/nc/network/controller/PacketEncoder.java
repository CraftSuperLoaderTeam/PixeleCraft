package com.pixelecraft.nc.network.controller;

import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.NetworkServer;
import com.pixelecraft.nc.network.ServerProtocolManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;
import java.util.logging.Level;

public class PacketEncoder extends MessageToByteEncoder<ServerProtocolManager.Packet> {
    NetworkServer server;
    ServerProtocolManager.Direction direction;
    public PacketEncoder(ServerProtocolManager.Direction direction, NetworkServer server){
        this.server = server;
        this.direction = direction;
    }

    protected void encode(ChannelHandlerContext p_encode_1_, ServerProtocolManager.Packet p_encode_2_, ByteBuf p_encode_3_) throws Exception {
        Integer integer = p_encode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacketId(this.direction, p_encode_2_);

        if (integer == null) {
            throw new IOException("Can\'t serialize unregistered packet");
        } else {
            PacketByteBuffer packetbuffer = new PacketByteBuffer(p_encode_3_);
            packetbuffer.writeVarIntToBuffer(integer.intValue());

            try {
                p_encode_2_.writePacketData(packetbuffer);
            } catch (Throwable throwable) {
                server.getLogger().log(Level.SEVERE,throwable.getMessage(),throwable);
            }
        }
    }
}
