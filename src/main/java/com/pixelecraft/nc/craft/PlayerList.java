package com.pixelecraft.nc.craft;

import api.pixelecraft.entity.Player;
import api.pixelecraft.event.player.PlayerJoinEvent;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.pixelecraft.nc.entity.EntityPlayer;
import com.pixelecraft.nc.network.NetworkManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerList {
    List<Player> players;
    final Map<UUID, EntityPlayer> entityPlayerMap = Maps.newHashMap();
    final Map<String, EntityPlayer> playerByName = Maps.newConcurrentMap();
    CraftServer server;
    public PlayerList(CraftServer server){
        this.server = server;
        this.players = new CopyOnWriteArrayList<>();
    }

    public void tick(){

    }

    public void initializeConnectionToPlayer(NetworkManager manager,EntityPlayer player){
        GameProfile profile = player.getProfile();
        String name = profile.getName();


    }

    public void onPlayerJoin(EntityPlayer player,String message){
        this.players.add(player);
        this.playerByName.put(player.getName(), player);
        this.entityPlayerMap.put(player.getUUID(),player);

        PlayerJoinEvent event = new PlayerJoinEvent(player,message);
        server.getPluginManager().callEvent(event);

        if(!player.service.getNetworkManager().isConnected()){
            return;
        }

        message = event.getMessage();

        if(message != null && !message.isEmpty()){
            for (String s : message.split("\n")){

            }
        }
    }

    public EntityPlayer getPlayerByUUID(UUID uuid){
        return entityPlayerMap.get(uuid);
    }


    public int getPlayerCount(){
        return players.size();
    }
}
