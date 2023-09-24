package com.pixelecraft.nc.craft;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class CraftTickThreadGroup {
    static int id;
    ExecutorService service;
    CraftServer server;

    public CraftTickThreadGroup(CraftServer server){
        this.server = server;
        this.service = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setName("MinecraftTickThread-"+(id++));
            return thread;
        });
    }

    public void addTick(Runnable tick){
        service.submit(tick);
    }

    public void stop(){
        try {
            server.getLogger().info("Shutdown minecraft service...");
            service.shutdownNow();
            server.getLogger().info("Shutdown service done!");
        }catch (Exception e){
            server.getLogger().log(Level.SEVERE,"Cannot shutdown minecraft service.",e);
        }
    }

    public void init(CraftServer server){

        addTick(() -> {
            try {
                server.getLogger().info("Launching [Network] service.");
                while (server.isRunning) server.server.networkTick();
            }catch (Throwable throwable){
                server.getLogger().log(Level.SEVERE,"NetworkServer was throw exception.",throwable);
                server.stop();
            }
        });

        addTick(() -> {
            try {
                server.getLogger().info("Launching [PlayerManager] service.");
                while (server.isRunning) server.playerList.tick();
            }catch (Throwable throwable){
                server.getLogger().log(Level.SEVERE,"PlayerManager was throw exception.",throwable);
                server.stop();
            }
        });
    }
}
