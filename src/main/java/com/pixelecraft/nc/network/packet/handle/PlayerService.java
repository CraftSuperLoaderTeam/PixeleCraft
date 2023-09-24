package com.pixelecraft.nc.network.packet.handle;

import com.mojang.authlib.GameProfile;
import com.pixelecraft.nc.craft.CraftServer;
import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.util.IChatComponent;

public class PlayerService implements NetHandle{
    CraftServer server;
    NetworkManager manager;
    GameProfile gameProfile;

    public PlayerService(CraftServer server,NetworkManager manager,GameProfile gameProfile){
        this.server = server;
        this.manager = manager;
        this.gameProfile = gameProfile;

        this.manager.setConnectionState(ServerProtocolManager.PLAY);
        this.manager.setNetHandle(this);
    }

    public NetworkManager getNetworkManager() {
        return manager;
    }

    @Override
    public void onDisconnect(IChatComponent reason) {

    }
}
