package api.pixelecraft.event.player;

import api.pixelecraft.entity.Player;

import java.net.InetAddress;
import java.util.UUID;

public class AsyncPlayerPreLoginEvent extends PlayerEvent{
    String playerName,message;
    UUID uuid;
    Result result;
    InetAddress address;

    public AsyncPlayerPreLoginEvent(String playerName,InetAddress address,UUID uuid){
        this.playerName = playerName;
        this.address = address;
        this.message = "";
        this.result = Result.ALLOWED;
        this.uuid = uuid;
    }

    public void setKickMessage(String message) {
        this.message = message;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getKickMessage() {
        return message;
    }

    public Result getLoginResult() {
        return result;
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    public enum Result {
        ALLOWED,
        KICK_FULL,
        KICK_BANNED,
        KICK_WHITELIST,
        KICK_OTHER;
    }
}
