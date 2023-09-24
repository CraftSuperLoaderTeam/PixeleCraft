package com.pixelecraft.nc.network.packet.handle;

import com.pixelecraft.nc.MetaData;
import com.pixelecraft.nc.craft.CraftServer;
import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.packet.Handshake;
import com.pixelecraft.nc.network.packet.PacketDisconnect;
import com.pixelecraft.nc.util.IChatComponent;
import com.pixelecraft.nc.util.text.ChatComponentString;

public class HandshakeTCP implements NetHandle {
    NetworkManager manager;
    CraftServer server;

    public HandshakeTCP(NetworkManager manager, CraftServer server) {
        this.manager = manager;
        this.server = server;
    }

    public void processHandshake(Handshake handshake) {
        switch (handshake.getRequested()) {
            case LOGIN -> {
                this.manager.setConnectionState(ServerProtocolManager.LOGIN);
                if (handshake.getProtocolVersion() > MetaData.PROTCOL_VERSION) {
                    ChatComponentString chatcomponenttext = new ChatComponentString("Outdated server! I\'m still on "+ MetaData.minecraft_version);
                    this.manager.sendPacket(new PacketDisconnect(chatcomponenttext));
                    this.manager.closeChannel(chatcomponenttext);
                } else if (handshake.getProtocolVersion() < MetaData.PROTCOL_VERSION) {
                    ChatComponentString chatcomponenttext1 = new ChatComponentString("Outdated client! Please use "+MetaData.minecraft_version);
                    this.manager.sendPacket(new PacketDisconnect(chatcomponenttext1));
                    this.manager.closeChannel(chatcomponenttext1);
                }else {
                    this.manager.setNetHandle(new LoginClient(manager,server));
                }
            }
            case STATUS -> {
                this.manager.setConnectionState(ServerProtocolManager.STATUS);
                this.manager.setNetHandle(new StatusServer(manager, server));
            }
        }
    }

    @Override
    public void onDisconnect(IChatComponent reason) {

    }
}
