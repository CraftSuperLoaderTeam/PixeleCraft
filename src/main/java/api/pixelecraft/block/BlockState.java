package api.pixelecraft.block;

import api.pixelecraft.Chunk;
import api.pixelecraft.Location;
import api.pixelecraft.Material;
import api.pixelecraft.World;

public interface BlockState {
    Block getBlock();

    Material getType();

    byte getLightLevel();

    World getWorld();

    int getX();

    int getY();

    int getZ();

    Location getLocation();

    Location getLocation(Location loc);

    Chunk getChunk();

    void setType(Material type);

    boolean update();

    boolean update(boolean force);

    boolean update(boolean force, boolean applyPhysics);

    boolean isPlaced();
}
