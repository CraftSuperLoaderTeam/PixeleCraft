package com.pixelecraft.nc.network.controller;

import com.pixelecraft.nc.craft.CraftServer;
import com.pixelecraft.nc.network.NetworkServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class LegacyPingHandle extends ChannelInboundHandlerAdapter {

    NetworkServer networkServer;
    public LegacyPingHandle(NetworkServer server){
        this.networkServer = server;
    }

    public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
        ByteBuf var3 = (ByteBuf)var2;
        var3.markReaderIndex();
        boolean var4 = true;

        try {
            try {
                if (var3.readUnsignedByte() != 254) {
                    return;
                }

                InetSocketAddress var5 = (InetSocketAddress)var1.channel().remoteAddress();
                CraftServer var6 = this.networkServer.getServer();
                int var7 = var3.readableBytes();
                String var8;
                switch (var7) {
                    case 0:
                        var6.getLogger().log(Level.INFO,String.format("Ping: (<1.3.x) from %s:%d", var5.getAddress(), var5.getPort()));
                        var8 = String.format("%s§%d§%d", var6.getMotd(), var6.getPlayerList().getPlayerCount(), var6.getMaxPlayer());
                        this.a(var1, this.a(var8));
                        break;
                    case 1:
                        if (var3.readUnsignedByte() != 1) {
                            return;
                        }

                        var6.getLogger().log(Level.INFO,String.format("Ping: (<1.3.x) from %s:%d", var5.getAddress(), var5.getPort()));
                        var8 = String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, var6.getVersion(), var6.getMotd(), var6.getPlayerList().getPlayerCount(), var6.getMaxPlayer());
                        this.a(var1, this.a(var8));
                        break;
                    default:
                        boolean var23 = var3.readUnsignedByte() == 1;
                        var23 &= var3.readUnsignedByte() == 250;
                        var23 &= "MC|PingHost".equals(new String(var3.readBytes(var3.readShort() * 2).array(), StandardCharsets.UTF_16BE));
                        int var9 = var3.readUnsignedShort();
                        var23 &= var3.readUnsignedByte() >= 73;
                        var23 &= 3 + var3.readBytes(var3.readShort() * 2).array().length + 4 == var9;
                        var23 &= var3.readInt() <= 65535;
                        var23 &= var3.readableBytes() == 0;
                        if (!var23) {
                            return;
                        }

                        var6.getLogger().log(Level.INFO,String.format("Ping: (1.6) from %s:%d", var5.getAddress(), var5.getPort()));
                        String var10 = String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, var6.getVersion(), var6.getMotd(), var6.getPlayerList().getPlayerCount(), var6.getMaxPlayer());
                        ByteBuf var11 = this.a(var10);

                        try {
                            this.a(var1, var11);
                        } finally {
                            var11.release();
                        }
                }

                var3.release();
                var4 = false;
            } catch (RuntimeException var21) {
            }

        } finally {
            if (var4) {
                var3.resetReaderIndex();
                var1.channel().pipeline().remove("legacy_query");
                var1.fireChannelRead(var2);
            }

        }
    }

    private void a(ChannelHandlerContext var1, ByteBuf var2) {
        var1.pipeline().firstContext().writeAndFlush(var2).addListener(ChannelFutureListener.CLOSE);
    }

    private ByteBuf a(String var1) {
        ByteBuf var2 = Unpooled.buffer();
        var2.writeByte(255);
        char[] var3 = var1.toCharArray();
        var2.writeShort(var3.length);
        char[] var4 = var3;
        int var5 = var3.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            char var7 = var4[var6];
            var2.writeChar(var7);
        }

        return var2;
    }
}
