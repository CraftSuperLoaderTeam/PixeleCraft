package api.pixelecraft.event.server;

import api.pixelecraft.Server;
import api.pixelecraft.event.Event;

public abstract class ServerEvent extends Event {
    abstract Server getServer();
}
