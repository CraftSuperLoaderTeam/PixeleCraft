package com.pixelecraft.nc.network.packet;

import com.mojang.authlib.GameProfile;
import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.handle.LoginClient;
import com.pixelecraft.nc.network.packet.handle.NetHandle;

import java.io.IOException;
import java.util.UUID;

public class PacketLoginStart implements ServerProtocolManager.Packet {
    GameProfile profile;

    public PacketLoginStart() {
    }

    public PacketLoginStart(GameProfile profile) {
        this.profile = profile;
    }

    @Override
    public void readPacketData(PacketByteBuffer buffer) throws IOException {
        this.profile = new GameProfile((UUID) null, buffer.readStringFromBuffer(16));
    }

    @Override
    public void writePacketData(PacketByteBuffer buf) throws IOException {
        buf.writeString(this.profile.getName());
    }

    @Override
    public void processPacket(NetworkManager manager, NetHandle handle) {
        ((LoginClient)handle).processLoginStart(this);
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}
