package com.pixelecraft.nc.network.packet.handle;

import api.pixelecraft.event.server.ServerPingEvent;
import com.google.common.base.Charsets;
import com.pixelecraft.nc.craft.CraftServer;
import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.packet.PacketPing;
import com.pixelecraft.nc.network.packet.PacketPong;
import com.pixelecraft.nc.network.packet.PacketServerInfo;
import com.pixelecraft.nc.network.packet.PacketServerQuery;
import com.pixelecraft.nc.util.IChatComponent;
import com.pixelecraft.nc.util.ServerPing;
import com.pixelecraft.nc.util.text.ChatComponentString;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.logging.Level;

public class StatusServer implements NetHandle {
    NetworkManager manager;
    CraftServer server;
    private boolean field_183008_d;
    private static final IChatComponent field_183007_a = new ChatComponentString("Status request has been handled.");

    public StatusServer(NetworkManager manager, CraftServer server) {
        this.manager = manager;
        this.server = server;
    }

    public void loadIcon(ServerPing ping){
        try {
            File file = server.getIcon();
            if (file == null || !file.exists()) {
                throw new Exception();
            }
            ByteBuf bytebuf = Unpooled.buffer();
            BufferedImage bufferedimage = ImageIO.read(file);
            ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
            ByteBuf bytebuf1 = Base64.encode(bytebuf);
            ping.setFavicon("data:image/png;base64," +bytebuf1.toString(Charsets.UTF_8));
        }catch (Exception e){
            server.getLogger().log(Level.SEVERE,"Cannot load server icon.");
        }
    }

    public void processServerQuery(PacketServerQuery packetIn) {
        if (this.field_183008_d) {
            this.manager.closeChannel(field_183007_a);
        } else {
            this.field_183008_d = true;

            ServerPingEvent event = new ServerPingEvent((InetSocketAddress) manager.getSocketAddress(),server.getMotd(),server.getMaxPlayer(),server);
            server.getPluginManager().callEvent(event);
            ServerPing ping = server.getServerInfo();
            ping.setPlayerCountData(new ServerPing.PlayerCountData(event.getMaxplayers(),server.getPlayerList().getPlayerCount()));
            ping.setServerDescription(new ChatComponentString(event.getMotd()));
            loadIcon(ping);
            this.manager.sendPacket(new PacketServerInfo(ping));
        }
    }

    public void handleServerInfo(PacketServerInfo packet){

    }

    public void handlePong(PacketPong pong){

    }


    public void processPing(PacketPing ping) {
        this.manager.sendPacket(new PacketPong(ping.getClientTime()));
        this.manager.closeChannel(field_183007_a);
    }

    @Override
    public void onDisconnect(IChatComponent reason) {

    }
}
