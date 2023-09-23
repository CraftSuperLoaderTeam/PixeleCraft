package api.pixelecraft;

import api.pixelecraft.command.CommandSender;
import api.pixelecraft.plugin.PluginManager;
import io.github.csl.logging.Logger;

public interface Server {
    int port();
    String getVersion();
    String getServerName();
    String getServerVersion();
    String getMotd();
    World getWorld(String name);
    Logger getLogger();
    int getMaxPlayer();
    boolean isServerInOnlineMode();
    PluginManager getPluginManager();
    CommandSender getConsoleSender();
    void stop();
}
