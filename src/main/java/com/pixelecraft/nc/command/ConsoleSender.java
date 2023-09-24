package com.pixelecraft.nc.command;

import api.pixelecraft.Server;
import api.pixelecraft.command.CommandSender;

public final class ConsoleSender implements CommandSender {

    Server server;
    public ConsoleSender(Server server){
        this.server = server;
    }

    @Override
    public void sendMessage(String message) {
        server.getLogger().info(message);
    }

    @Override
    public void sendMessage(String[] message) {
        for(String s:message) server.getLogger().info(s);
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public boolean hasPermission(String name) {
        return true;
    }

    @Override
    public boolean isConsole() {
        return true;
    }
}
