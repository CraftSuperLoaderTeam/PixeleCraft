package com.pixelecraft.nc.network.packet;

import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.handle.HandshakeTCP;
import com.pixelecraft.nc.network.packet.handle.NetHandle;

public class Handshake implements ServerProtocolManager.Packet {
    private int protocolVersion;
    private String ip;
    private int port;
    private ServerProtocolManager requested;

    @Override
    public void readPacketData(PacketByteBuffer buf) {
        this.protocolVersion = buf.readVarIntFromBuffer();
        this.ip = buf.readStringFromBuffer(255);
        this.port = buf.readUnsignedShort();
        this.requested = ServerProtocolManager.getById(buf.readVarIntFromBuffer());
    }

    @Override
    public void writePacketData(PacketByteBuffer buf){
        buf.writeVarIntToBuffer(this.protocolVersion);
        buf.writeString(this.ip);
        buf.writeShort(this.port);
        buf.writeVarIntToBuffer(this.requested.getId());
    }

    public ServerProtocolManager getRequested() {
        return requested;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public void processPacket(NetworkManager manager, NetHandle handle) {
        HandshakeTCP tcp = (HandshakeTCP) handle;
        tcp.processHandshake(this);
    }
}
