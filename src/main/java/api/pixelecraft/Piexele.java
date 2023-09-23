package api.pixelecraft;

import api.pixelecraft.command.CommandSender;
import api.pixelecraft.plugin.PluginManager;
import io.github.csl.logging.Logger;

import java.util.logging.Level;

public final class Piexele {
    private Piexele(){}
    private static Server server;

    public static void setServer(Server server) {
        Piexele.server = server;
        server.getLogger().log(Level.INFO,"This server is running " + server.getServerName() + " version " + server.getServerVersion() + " (Implementing API version " + server.getVersion() + ")");
    }

    public static Server getServer() {
        return server;
    }

    public static PluginManager getPluginManager(){
        return server.getPluginManager();
    }

    public static Logger getLogger(){
        return server.getLogger();
    }

    public static CommandSender getConsoleSender(){
        return server.getConsoleSender();
    }

    public static World getWorld(String name){
        return server.getWorld(name);
    }
}
