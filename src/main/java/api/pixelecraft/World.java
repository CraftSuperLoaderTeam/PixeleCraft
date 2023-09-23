package api.pixelecraft;

import api.pixelecraft.block.Block;

public interface World {
    String getName();
    Chunk getChunkAt(Location location);
    Block getBlockAt(Location location);
}
