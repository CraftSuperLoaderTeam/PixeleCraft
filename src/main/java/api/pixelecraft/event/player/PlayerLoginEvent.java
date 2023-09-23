package api.pixelecraft.event.player;

import api.pixelecraft.entity.Player;
import api.pixelecraft.event.Cancelable;

public class PlayerLoginEvent extends PlayerEvent implements Cancelable {

    boolean cancel;

    @Override
    public boolean isCancel() {
        return cancel;
    }

    @Override
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public Player getPlayer() {
        return null;
    }
}
