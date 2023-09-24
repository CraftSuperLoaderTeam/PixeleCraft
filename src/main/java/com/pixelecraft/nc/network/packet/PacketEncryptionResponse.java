package com.pixelecraft.nc.network.packet;

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

public class PacketEncryptionResponse implements ServerProtocolManager.Packet {
    private byte[] secretKeyEncrypted = new byte[0];
    private byte[] verifyTokenEncrypted = new byte[0];

    public PacketEncryptionResponse() {
    }

    public PacketEncryptionResponse(SecretKey secretKey, PublicKey publicKey, byte[] verifyToken) {
        this.secretKeyEncrypted = CryptManager.encryptData(publicKey, secretKey.getEncoded());
        this.verifyTokenEncrypted = CryptManager.encryptData(publicKey, verifyToken);
    }

    @Override
    public void readPacketData(PacketByteBuffer buf) throws IOException {
        this.secretKeyEncrypted = buf.readByteArray();
        this.verifyTokenEncrypted = buf.readByteArray();
    }

    @Override
    public void writePacketData(PacketByteBuffer buf) throws IOException {
        buf.writeByteArray(this.secretKeyEncrypted);
        buf.writeByteArray(this.verifyTokenEncrypted);
    }

    @Override
    public void processPacket(NetworkManager manager, NetHandle handler) {
        ((LoginClient)handler).processEncryptionResponse(this);
    }

    public SecretKey getSecretKey(PrivateKey key) {
        return CryptManager.decryptSharedKey(key, this.secretKeyEncrypted);
    }

    public byte[] getVerifyToken(PrivateKey key) {
        return key == null ? this.verifyTokenEncrypted : CryptManager.decryptData(key, this.verifyTokenEncrypted);
    }
}
