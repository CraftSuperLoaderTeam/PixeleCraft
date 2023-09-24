package com.pixelecraft.nc.plugin;

import api.pixelecraft.event.Event;
import api.pixelecraft.plugin.PluginManager;
import com.pixelecraft.nc.craft.CraftServer;

public class PluginManagerIml implements PluginManager {
    CraftServer server;
    public PluginManagerIml(CraftServer server){
        this.server = server;
    }

    @Override
    public void callEvent(Event event) {

    }
}
