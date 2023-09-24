package com.pixelecraft.nc.network.controller;

import api.pixelecraft.Piexele;
import com.pixelecraft.nc.network.NetworkServer;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.NetworkManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class PacketDecoder extends ByteToMessageDecoder {

    NetworkServer server;
    ServerProtocolManager.Direction direction;

    public PacketDecoder(ServerProtocolManager.Direction direction, NetworkServer server) {
        this.server = server;
        this.direction = direction;
    }

    // /*
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        server.getLogger().log(Level.WARNING,"Netty IO has exception:" + cause.getClass().getSimpleName() + ": " + cause.getLocalizedMessage());
        ctx.close();
    }

    // */

    @Override
    protected void decode(ChannelHandlerContext var1, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() != 0) {
            PacketByteBuffer packetbuffer = new PacketByteBuffer(byteBuf);
            int i = packetbuffer.readVarIntFromBuffer();

            ServerProtocolManager.Packet packet = var1.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacket(this.direction, i);

            if (packet == null) {
                Piexele.getLogger().log(Level.SEVERE,"Bad packet id <"+i+">/ NettyCraft_NetworkManager_ERROR");
                return;
            }else {
                packet.readPacketData(packetbuffer);
                if (packetbuffer.readableBytes() > 0) {
                    throw new IOException("Packet /" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
                }
            }

            list.add(packet);
        }
    }
}
