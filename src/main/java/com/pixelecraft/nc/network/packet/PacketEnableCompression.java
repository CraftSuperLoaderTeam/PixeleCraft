package com.pixelecraft.nc.network.packet;

import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.handle.LoginClient;
import com.pixelecraft.nc.network.packet.handle.NetHandle;

import java.io.IOException;

public class PacketEnableCompression implements ServerProtocolManager.Packet {
    private int compressionThreshold;

    public PacketEnableCompression() {
    }

    public PacketEnableCompression(int thresholdIn) {
        this.compressionThreshold = thresholdIn;
    }

    @Override
    public void readPacketData(PacketByteBuffer buf) throws IOException {
        this.compressionThreshold = buf.readVarIntFromBuffer();
    }

    @Override
    public void writePacketData(PacketByteBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.compressionThreshold);
    }

    @Override
    public void processPacket(NetworkManager manager, NetHandle handler) {
        ((LoginClient)handler).handleEnableCompression(this);
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}
