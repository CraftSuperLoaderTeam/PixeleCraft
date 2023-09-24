package com.pixelecraft.nc.craft;

public class CraftServerShutdownHook extends Thread{
    CraftServer server;
    public CraftServerShutdownHook(CraftServer server){
        this.server = server;
        this.setName("ShutdownHook");
    }

    @Override
    public void run() {
        try {
            server.stop();
        } finally {
            try {
                server.reader.getTerminal().close();
            } catch (Exception e) {
            }
        }
    }
}
