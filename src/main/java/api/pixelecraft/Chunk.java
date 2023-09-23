package api.pixelecraft;

import api.pixelecraft.block.Block;
import api.pixelecraft.block.BlockState;
import api.pixelecraft.entity.Entity;

public interface Chunk {
    int getX();

    int getZ();

    World getWorld();

    Block getBlock(int x, int y, int z);

    ChunkSnapshot getChunkSnapshot();

    ChunkSnapshot getChunkSnapshot(boolean includeMaxblocky, boolean includeBiome, boolean includeBiomeTempRain);

    Entity[] getEntities();

    BlockState[] getTileEntities();

    boolean isLoaded();

    boolean load(boolean generate);

    boolean load();

    boolean unload(boolean save);

    boolean unload();

    boolean isSlimeChunk();
}
