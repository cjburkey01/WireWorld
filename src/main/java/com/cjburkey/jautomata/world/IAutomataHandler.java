package com.cjburkey.jautomata.world;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import org.joml.Vector3dc;

/**
 * Created by CJ Burkey on 2018/11/26
 */
public interface IAutomataHandler {
    
    void onNeighborChunkChanged(Neighbor neighbor, Chunk newChunk);
    byte process(Chunk chunk, byte tileType, int dataIndex, byte x, byte y);
    Byte2ObjectArrayMap<Vector3dc> getColorMap();
    byte getResetType(byte input);
    
    enum Neighbor {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NORTH_EAST,
        NORTH_WEST,
        SOUTH_EAST,
        SOUTH_WEST,
    }
    
}
