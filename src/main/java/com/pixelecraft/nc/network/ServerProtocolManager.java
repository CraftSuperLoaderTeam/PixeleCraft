package com.pixelecraft.nc.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.pixelecraft.nc.network.controller.PacketByteBuffer;
import com.pixelecraft.nc.network.packet.*;
import com.pixelecraft.nc.network.packet.handle.NetHandle;

import java.io.IOException;
import java.util.Map;

public enum ServerProtocolManager {
    HANDSHAKING(-1) {
        {
            this.registerPacket(Direction.SERVERBOUND, Handshake.class);
        }
    },
    PLAY(0){
        {

        }
    },
    STATUS(1) {
        {
            this.registerPacket(Direction.SERVERBOUND, PacketServerQuery.class);
            this.registerPacket(Direction.CLIENTBOUND, PacketServerInfo.class);
            this.registerPacket(Direction.SERVERBOUND, PacketPing.class);
            this.registerPacket(Direction.CLIENTBOUND, PacketPong.class);
        }
    },
    LOGIN(2) {
        {
            this.registerPacket(Direction.CLIENTBOUND, PacketDisconnect.class);
            this.registerPacket(Direction.CLIENTBOUND, PacketEncryptionRequest.class);
            this.registerPacket(Direction.CLIENTBOUND, PacketLoginSuccess.class);
            this.registerPacket(Direction.CLIENTBOUND, PacketEnableCompression.class);
            this.registerPacket(Direction.SERVERBOUND, PacketLoginStart.class);
            this.registerPacket(Direction.SERVERBOUND, PacketEncryptionResponse.class);
        }
    };

    private static int field_181136_e = -1;
    private static int field_181137_f = 2;
    private static final ServerProtocolManager[] STATES_BY_ID = new ServerProtocolManager[field_181137_f - field_181136_e + 1];
    private static final Map<Class<? extends Packet>, ServerProtocolManager> STATES_BY_CLASS = Maps.newHashMap();

    static {
        for (ServerProtocolManager enumconnectionstate : values()) {
            int i = enumconnectionstate.getId();
            if (i < field_181136_e || i > field_181137_f) {
                throw new Error("Invalid protocol ID " + i);
            }
            STATES_BY_ID[i - field_181136_e] = enumconnectionstate;
            for (Direction enumpacketdirection : enumconnectionstate.directionMaps.keySet()) {
                for (Class<? extends Packet> oclass : (enumconnectionstate.directionMaps.get(enumpacketdirection)).values()) {
                    if (STATES_BY_CLASS.containsKey(oclass) && STATES_BY_CLASS.get(oclass) != enumconnectionstate) {
                        throw new Error("Packet " + oclass + " is already assigned to protocol " + STATES_BY_CLASS.get(oclass) + " - can\'t reassign to " + enumconnectionstate);
                    }
                    try {
                        oclass.newInstance();
                    } catch (Throwable var10) {
                        throw new Error("Packet " + oclass + " fails instantiation checks! " + oclass);
                    }
                    STATES_BY_CLASS.put(oclass, enumconnectionstate);
                }
            }
        }
    }

    private final int id;

    public enum Direction {
        SERVERBOUND,
        CLIENTBOUND;
    }

    private final Map<Direction, BiMap<Integer, Class<? extends Packet>>> directionMaps;

    ServerProtocolManager(int id) {
        this.directionMaps = Maps.newEnumMap(Direction.class);
        this.id = id;
    }

    public Packet getPacket(Direction direction, int id) throws InstantiationException, IllegalAccessException {
        try {
            Class<? extends Packet> oclass = (Class) ((BiMap) this.directionMaps.get(direction)).get(Integer.valueOf(id));
            return oclass == null ? null : (Packet) oclass.newInstance();
        }catch (NullPointerException e){
            return null;
        }
    }

    public Integer getPacketId(Direction direction, Packet packetIn) {
        return (Integer) ((BiMap) this.directionMaps.get(direction)).inverse().get(packetIn.getClass());
    }

    public static ServerProtocolManager getFromPacket(Packet packetIn) {
        return (ServerProtocolManager) STATES_BY_CLASS.get(packetIn.getClass());
    }

    public ServerProtocolManager registerPacket(Direction direction, Class<? extends Packet> packetClass) {
        BiMap<Integer, Class<? extends Packet>> bimap = this.directionMaps.computeIfAbsent(direction, k -> HashBiMap.<Integer, Class<? extends Packet>>create());

        if (bimap.containsValue(packetClass)) {
            String s = direction + " packet " + packetClass + " is already known to ID " + bimap.inverse().get(packetClass);
            throw new IllegalArgumentException(s);
        } else {
            bimap.put(Integer.valueOf(bimap.size()), packetClass);
            return this;
        }
    }

    public static ServerProtocolManager getById(int stateId) {
        return stateId >= field_181136_e && stateId <= field_181137_f ? STATES_BY_ID[stateId - field_181136_e] : null;
    }

    public int getId() {
        return this.id;
    }

    public static interface Packet {

        void readPacketData(PacketByteBuffer buffer) throws IOException;

        void writePacketData(PacketByteBuffer buf) throws IOException;

        void processPacket(NetworkManager manager, NetHandle handle);
    }
}
