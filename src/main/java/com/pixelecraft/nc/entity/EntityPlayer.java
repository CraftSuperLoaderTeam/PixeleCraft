package com.pixelecraft.nc.entity;

import api.pixelecraft.Location;
import api.pixelecraft.Piexele;
import api.pixelecraft.Server;
import api.pixelecraft.entity.Player;
import com.mojang.authlib.GameProfile;
import com.pixelecraft.nc.network.packet.handle.PlayerService;
import com.pixelecraft.nc.util.IChatComponent;

import java.util.List;
import java.util.UUID;

public class EntityPlayer implements Player {
    public String displayName;
    public PlayerService service;
    public IChatComponent listName;
    GameProfile profile;
    public Location compassTarget;
    public int newExp = 0;
    public int newLevel = 0;
    public int newTotalExp = 0;
    public boolean keepLevel = false;
    public double maxHealthCache;
    public boolean joining = true;
    public List<String> permission;
    boolean op;
    public boolean sentListPacket = false;

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendMessage(String[] message) {

    }

    @Override
    public Server getServer() {
        return Piexele.getServer();
    }

    @Override
    public boolean hasPermission(String name) {
        return permission.contains(name);
    }

    @Override
    public Location getLocation() {
        return compassTarget;
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getName() {
        return displayName;
    }

    @Override
    public boolean isOp() {
        return op;
    }

    public GameProfile getProfile() {
        return profile;
    }
}
