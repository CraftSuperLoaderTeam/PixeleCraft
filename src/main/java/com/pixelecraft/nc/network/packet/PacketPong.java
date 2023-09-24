package com.pixelecraft.nc.network.packet;

import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.handle.NetHandle;
import com.pixelecraft.nc.network.packet.handle.StatusServer;

import java.io.IOException;

public class PacketPong implements ServerProtocolManager.Packet {
    private long clientTime;

    public PacketPong() {
    }

    public PacketPong(long time) {
        this.clientTime = time;
    }

    public void readPacketData(PacketByteBuffer buf) throws IOException {
        this.clientTime = buf.readLong();
    }

    public void writePacketData(PacketByteBuffer buf) throws IOException {
        buf.writeLong(this.clientTime);
    }

    public void processPacket(NetworkManager manager, NetHandle handler) {
        ((StatusServer)handler).handlePong(this);
    }
}
