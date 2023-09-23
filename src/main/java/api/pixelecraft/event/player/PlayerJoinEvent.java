package api.pixelecraft.event.player;

import api.pixelecraft.entity.Player;

public class PlayerJoinEvent extends PlayerEvent{
    Player player;
    String message;

    public PlayerJoinEvent(Player player,String message){
        this.player = player;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
