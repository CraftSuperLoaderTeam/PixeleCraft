package com.pixelecraft.nc.network.packet;

import com.mojang.authlib.GameProfile;
import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.handle.LoginClient;
import com.pixelecraft.nc.network.packet.handle.NetHandle;

import java.io.IOException;
import java.util.UUID;

public class PacketLoginSuccess implements ServerProtocolManager.Packet {
    private GameProfile profile;

    public PacketLoginSuccess() {
    }

    public PacketLoginSuccess(GameProfile profileIn) {
        this.profile = profileIn;
    }

    @Override
    public void readPacketData(PacketByteBuffer buf) throws IOException {
        String s = buf.readStringFromBuffer(36);
        String s1 = buf.readStringFromBuffer(16);
        UUID uuid = UUID.fromString(s);
        this.profile = new GameProfile(uuid, s1);
    }

    @Override
    public void writePacketData(PacketByteBuffer buf) throws IOException {
        UUID uuid = this.profile.getId();
        buf.writeString(uuid == null ? "" : uuid.toString());
        buf.writeString(this.profile.getName());
    }

    @Override
    public void processPacket(NetworkManager manager, NetHandle handler) {
        ((LoginClient)handler).handleLoginSuccess(this);
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}
