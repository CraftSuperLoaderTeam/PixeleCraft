package api.pixelecraft.entity;

import api.pixelecraft.Location;
import api.pixelecraft.command.CommandSender;

import java.util.UUID;

public interface Entity extends CommandSender {
    Location getLocation();
    UUID getUUID();
}
