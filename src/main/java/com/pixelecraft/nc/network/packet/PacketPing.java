package com.pixelecraft.nc.network.packet;

import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.handle.NetHandle;
import com.pixelecraft.nc.network.packet.handle.StatusServer;

import java.io.IOException;

public class PacketPing implements ServerProtocolManager.Packet {
    private long clientTime;

    public PacketPing() {
    }

    public PacketPing(long ping) {
        this.clientTime = ping;
    }

    public void readPacketData(PacketByteBuffer buf) throws IOException {
        this.clientTime = buf.readLong();
    }

    public void writePacketData(PacketByteBuffer buf) throws IOException {
        buf.writeLong(this.clientTime);
    }

    public void processPacket(NetworkManager manager, NetHandle handler) {
        ((StatusServer)handler).processPing(this);
    }

    public long getClientTime() {
        return this.clientTime;
    }
}
