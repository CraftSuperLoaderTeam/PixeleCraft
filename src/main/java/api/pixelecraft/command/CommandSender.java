package api.pixelecraft.command;

import api.pixelecraft.Server;

public interface CommandSender {
    void sendMessage(String message);
    void sendMessage(String[] message);
    Server getServer();
    public boolean hasPermission(String name);
    default boolean isConsole(){return false;}
}
