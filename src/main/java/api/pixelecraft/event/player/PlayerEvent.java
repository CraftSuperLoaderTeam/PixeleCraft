package api.pixelecraft.event.player;

import api.pixelecraft.entity.Player;
import api.pixelecraft.event.Event;

public abstract class PlayerEvent extends Event {
    public abstract Player getPlayer();
}
