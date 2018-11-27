package com.cjburkey.wireworld;

import com.cjburkey.jautomata.IAutomataHandler;
import com.cjburkey.jautomata.util.Helpers;
import com.cjburkey.jautomata.world.Chunk;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import static com.cjburkey.jautomata.world.AutomataWorld.*;

/**
 * Created by CJ Burkey on 2018/11/26
 */
public class AutomataWireWorldHandler implements IAutomataHandler {
    
    private static final Byte2ObjectArrayMap<Vector3dc> colorMap = new Byte2ObjectArrayMap<>();
    
    static {
        colorMap.put((byte) 0x00, null);
        colorMap.put((byte) 0x01, new Vector3d(0.3d, 0.3d, 1.0d));
        colorMap.put((byte) 0x02, new Vector3d(1.0d, 0.3d, 0.3d));
        colorMap.put((byte) 0x03, new Vector3d(1.0d, 1.0d, 0.3d));
    }
    
    public byte process(Chunk chunk, byte tileType, int dataIndex, byte x, byte y) {
        switch (tileType) {
            case 0x00: return 0x00;                             // Empty -> Empty
            case 0x01: return 0x02;                             // Electron Head -> Electron Tail
            case 0x02: return 0x03;                             // Electron Tail -> Conductor
            case 0x03: return processConductorNew(chunk, x, y); // Conductor ->? Electron Head
            default: return 0x00;
        }
    }
    
    private byte processConductorNew(Chunk chunk, byte ix, byte iy) {
        int neighborElectronHeads = 0;
        
        for (int x = ix - 1; x <= ix + 1; x ++) {
            for (int y = iy - 1; y <= iy + 1; y ++) {
                int chunkOffsetX = Helpers.div(x, CHUNK_SIZE);
                int chunkOffsetY = Helpers.div(y, CHUNK_SIZE);
                if (chunkOffsetX == 0 && chunkOffsetY == 0) {
                    if (chunk.getRaw(x, y) == 0x01) neighborElectronHeads ++;
                } else {
                    Chunk nChunk = chunk.adjacentChunks.getChunkFromOffset(chunkOffsetX, chunkOffsetY);
                    if (nChunk != null && nChunk.getRaw(Helpers.mod(x, CHUNK_SIZE), Helpers.mod(y, CHUNK_SIZE)) == 0x01) neighborElectronHeads ++;
                }
                
                // If there are more than two adjacent electron heads, remain a conductor
                if (neighborElectronHeads > 2) return 0x03;
            }
        }
        
        // If there are no adjacent electron heads, remain a conductor
        if (neighborElectronHeads == 0) return 0x03;
        
        // Become an electron head if there are exactly 1 or 2 neighboring electron heads
        return 0x01;
    }
    
    public Byte2ObjectArrayMap<Vector3dc> getColorMap() {
        return colorMap;
    }
    
    public byte getResetTile(byte input) {
        if (input == 0x01 || input == 0x02) return 0x03;
        return input;
    }
    
}
