package com.pixelecraft.nc.network.packet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.handle.NetHandle;
import com.pixelecraft.nc.network.packet.handle.StatusServer;
import com.pixelecraft.nc.util.ChatStyle;
import com.pixelecraft.nc.util.EnumTypeAdapterFactory;
import com.pixelecraft.nc.util.IChatComponent;
import com.pixelecraft.nc.util.ServerPing;

import java.io.IOException;

public class PacketServerInfo implements ServerProtocolManager.Packet {

    ServerPing response;
    Gson GSON = (new GsonBuilder()).registerTypeAdapter(ServerPing.Version.class, new ServerPing.Version.Serializer())
            .registerTypeAdapter(ServerPing.PlayerCountData.class, new ServerPing.PlayerCountData.Serializer())
            .registerTypeAdapter(ServerPing.class, new ServerPing.Serializer())
            .registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer())
            .registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer())
            .registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();;

    public PacketServerInfo() {
    }

    public PacketServerInfo(ServerPing responseIn) {
        this.response = responseIn;
    }

    public void readPacketData(PacketByteBuffer buf) throws IOException {
        this.response = (ServerPing) GSON.fromJson(buf.readStringFromBuffer(32767), ServerPing.class);
    }

    public void writePacketData(PacketByteBuffer buf) throws IOException {
        String s = GSON.toJson((Object) this.response);

        buf.writeString(s);
    }

    public void processPacket(NetworkManager manager, NetHandle handler) {

        ((StatusServer)handler).handleServerInfo(this);
    }
}
