package com.pixelecraft.nc.network.packet;

import com.pixelecraft.nc.craft.MinecraftEncryption;
import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.handle.LoginClient;
import com.pixelecraft.nc.network.packet.handle.NetHandle;
import com.pixelecraft.nc.util.CryptManager;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PacketEncryptionRequest implements ServerProtocolManager.Packet {
    private String hashedServerId;
    private PublicKey publicKey;
    private byte[] verifyToken;

    public PacketEncryptionRequest() {
    }

    public PacketEncryptionRequest(String serverIdIn, PublicKey publicKeyIn, byte[] verifyTokenIn) {
        this.hashedServerId = serverIdIn;
        this.publicKey = publicKeyIn;
        this.verifyToken = verifyTokenIn;
    }

    @Override
    public void readPacketData(PacketByteBuffer buf) throws IOException {
        this.hashedServerId = buf.readStringFromBuffer(20);
        this.publicKey = CryptManager.decodePublicKey(buf.readByteArray());
        this.verifyToken = buf.readByteArray();
    }

    @Override
    public void writePacketData(PacketByteBuffer buf) throws IOException {
        buf.writeString(this.hashedServerId);
        buf.writeByteArray(this.publicKey.getEncoded());
        buf.writeByteArray(this.verifyToken);
    }

    @Override
    public void processPacket(NetworkManager manager, NetHandle handler) {
        ((LoginClient)handler).handleEncryptionRequest(this);
    }

    public String getServerId() {
        return this.hashedServerId;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    public byte[] bytes(PrivateKey key){
        return key == null ? this.verifyToken : MinecraftEncryption.encrypt(key,this.verifyToken);
    }
}
