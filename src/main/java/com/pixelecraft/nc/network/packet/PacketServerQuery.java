package com.pixelecraft.nc.network.packet;

import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.handle.NetHandle;
import com.pixelecraft.nc.network.packet.handle.StatusServer;

import java.io.IOException;

public class PacketServerQuery implements ServerProtocolManager.Packet {
    @Override
    public void readPacketData(PacketByteBuffer buffer) throws IOException {
    }

    @Override
    public void writePacketData(PacketByteBuffer buf) throws IOException {
    }

    @Override
    public void processPacket(NetworkManager manager, NetHandle handle) {
        ((StatusServer)handle).processServerQuery(this);
    }
}
