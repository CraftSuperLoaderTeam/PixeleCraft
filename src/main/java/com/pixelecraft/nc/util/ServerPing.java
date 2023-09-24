package com.pixelecraft.nc.util;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;

import java.lang.reflect.Type;
import java.util.UUID;

public class ServerPing {
    private IChatComponent description;
    private ServerPing.PlayerCountData playerCount;
    private ServerPing.Version version;
    private String favicon;

    public IChatComponent getServerDescription() {
        return this.description;
    }

    public void setServerDescription(IChatComponent motd) {
        this.description = motd;
    }

    public PlayerCountData getPlayerCountData() {
        return this.playerCount;
    }

    public void setPlayerCountData(PlayerCountData countData) {
        this.playerCount = countData;
    }

    public ServerPing.Version getProtocolVersionInfo() {
        return this.version;
    }

    public void setProtocolVersionInfo(ServerPing.Version protocolVersionData) {
        this.version = protocolVersionData;
    }

    public void setFavicon(String faviconBlob) {
        this.favicon = faviconBlob;
    }

    public String getFavicon() {
        return this.favicon;
    }

    public static class Version {
        private final String name;
        private final int protocol;

        public Version(String nameIn, int protocolIn) {
            this.name = nameIn;
            this.protocol = protocolIn;
        }

        public String getName() {
            return this.name;
        }

        public int getProtocol() {
            return this.protocol;
        }

        public static class Serializer implements JsonDeserializer<ServerPing.Version>, JsonSerializer<ServerPing.Version> {
            public ServerPing.Version deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
                JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "version");
                return new ServerPing.Version(JsonUtils.getString(jsonobject, "name"), JsonUtils.getInt(jsonobject, "protocol"));
            }

            public JsonElement serialize(ServerPing.Version p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
                JsonObject jsonobject = new JsonObject();
                jsonobject.addProperty("name", p_serialize_1_.getName());
                jsonobject.addProperty("protocol", Integer.valueOf(p_serialize_1_.getProtocol()));
                return jsonobject;
            }
        }
    }

    public static class PlayerCountData {
        private final int maxPlayers;
        private final int onlinePlayerCount;
        private GameProfile[] players;

        public PlayerCountData(int maxOnlinePlayers, int onlinePlayers) {
            this.maxPlayers = maxOnlinePlayers;
            this.onlinePlayerCount = onlinePlayers;
        }

        public int getMaxPlayers() {
            return this.maxPlayers;
        }

        public int getOnlinePlayerCount() {
            return this.onlinePlayerCount;
        }

        public GameProfile[] getPlayers() {
            return this.players;
        }

        public void setPlayers(GameProfile[] playersIn) {
            this.players = playersIn;
        }

        public static class Serializer implements JsonDeserializer<PlayerCountData>, JsonSerializer<PlayerCountData> {
            public PlayerCountData deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
                JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "players");
                PlayerCountData serverstatusresponse$playercountdata = new PlayerCountData(JsonUtils.getInt(jsonobject, "max"), JsonUtils.getInt(jsonobject, "online"));

                if (JsonUtils.isJsonArray(jsonobject, "sample")) {
                    JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "sample");

                    if (jsonarray.size() > 0) {
                        GameProfile[] agameprofile = new GameProfile[jsonarray.size()];

                        for (int i = 0; i < agameprofile.length; ++i) {
                            JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonarray.get(i), "player[" + i + "]");
                            String s = JsonUtils.getString(jsonobject1, "id");
                            agameprofile[i] = new GameProfile(UUID.fromString(s), JsonUtils.getString(jsonobject1, "name"));
                        }

                        serverstatusresponse$playercountdata.setPlayers(agameprofile);
                    }
                }

                return serverstatusresponse$playercountdata;
            }

            public JsonElement serialize(PlayerCountData p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
                JsonObject jsonobject = new JsonObject();
                jsonobject.addProperty("max", (Number) Integer.valueOf(p_serialize_1_.getMaxPlayers()));
                jsonobject.addProperty("online", (Number) Integer.valueOf(p_serialize_1_.getOnlinePlayerCount()));

                if (p_serialize_1_.getPlayers() != null && p_serialize_1_.getPlayers().length > 0) {
                    JsonArray jsonarray = new JsonArray();

                    for (int i = 0; i < p_serialize_1_.getPlayers().length; ++i) {
                        JsonObject jsonobject1 = new JsonObject();
                        UUID uuid = p_serialize_1_.getPlayers()[i].getId();
                        jsonobject1.addProperty("id", uuid == null ? "" : uuid.toString());
                        jsonobject1.addProperty("name", p_serialize_1_.getPlayers()[i].getName());
                        jsonarray.add(jsonobject1);
                    }

                    jsonobject.add("sample", jsonarray);
                }

                return jsonobject;
            }
        }
    }

    public static class Serializer implements JsonDeserializer<ServerPing>, JsonSerializer<ServerPing> {
        public ServerPing deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "status");
            ServerPing serverstatusresponse = new ServerPing();

            if (jsonobject.has("description")) {
                serverstatusresponse.setServerDescription((IChatComponent) p_deserialize_3_.deserialize(jsonobject.get("description"), IChatComponent.class));
            }

            if (jsonobject.has("players")) {
                serverstatusresponse.setPlayerCountData((PlayerCountData) p_deserialize_3_.deserialize(jsonobject.get("players"), PlayerCountData.class));
            }

            if (jsonobject.has("version")) {
                serverstatusresponse.setProtocolVersionInfo((ServerPing.Version) p_deserialize_3_.deserialize(jsonobject.get("version"), ServerPing.Version.class));
            }

            if (jsonobject.has("favicon")) {
                serverstatusresponse.setFavicon(JsonUtils.getString(jsonobject, "favicon"));
            }

            return serverstatusresponse;
        }

        public JsonElement serialize(ServerPing p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            JsonObject jsonobject = new JsonObject();

            if (p_serialize_1_.getServerDescription() != null) {
                jsonobject.add("description", p_serialize_3_.serialize(p_serialize_1_.getServerDescription()));
            }

            if (p_serialize_1_.getPlayerCountData() != null) {
                jsonobject.add("players", p_serialize_3_.serialize(p_serialize_1_.getPlayerCountData()));
            }

            if (p_serialize_1_.getProtocolVersionInfo() != null) {
                jsonobject.add("version", p_serialize_3_.serialize(p_serialize_1_.getProtocolVersionInfo()));
            }

            if (p_serialize_1_.getFavicon() != null) {
                jsonobject.addProperty("favicon", p_serialize_1_.getFavicon());
            }

            return jsonobject;
        }
    }
}
