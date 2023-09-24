package com.pixelecraft.nc.network.packet;

import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.handle.LoginClient;
import com.pixelecraft.nc.network.packet.handle.NetHandle;
import com.pixelecraft.nc.util.IChatComponent;

import java.io.IOException;

public class PacketDisconnect implements ServerProtocolManager.Packet {

    IChatComponent component;

    public PacketDisconnect() {
    }

    public PacketDisconnect(IChatComponent component) {
        this.component = component;
    }

    @Override
    public void readPacketData(PacketByteBuffer buffer) throws IOException {
        this.component = buffer.readChatComponent();
    }

    @Override
    public void writePacketData(PacketByteBuffer buf) throws IOException {
        buf.writeChatComponent(this.component);
    }

    @Override
    public void processPacket(NetworkManager manager, NetHandle handle) {
        ((LoginClient)handle).handleDisconnect(this);
    }

    public IChatComponent getReason() {
        return this.component;
    }
}
