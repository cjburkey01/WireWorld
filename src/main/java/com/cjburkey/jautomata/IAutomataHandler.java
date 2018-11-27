package com.cjburkey.jautomata;

import com.cjburkey.jautomata.world.Chunk;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import org.joml.Vector3dc;

/**
 * Created by CJ Burkey on 2018/11/26
 */
public interface IAutomataHandler {
    
    byte process(Chunk chunk, byte tileType, int dataIndex, byte x, byte y);
    byte getResetTile(byte input);
    Byte2ObjectArrayMap<Vector3dc> getColorMap();
    byte getTileCount();
    byte[] getTiles();
    
    @SuppressWarnings("unused")
    String[] getTileNames();
    
}
