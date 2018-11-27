package com.cjburkey.wireworld;

import com.cjburkey.jautomata.util.Helpers;
import com.cjburkey.jautomata.world.AdjacentChunks;
import com.cjburkey.jautomata.world.AutomataWorld;
import com.cjburkey.jautomata.world.Chunk;
import com.cjburkey.jautomata.IAutomataHandler;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import org.joml.Vector2i;
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
            case 0x03: return processConductor(chunk, x, y);    // Conductor ->? Electron Head
            default: return 0x00;
        }
    }
    
    private byte processConductor(Chunk chunk, byte x, byte y) {
        int neighborElectronHeads = 0;
        
        // TODO: MAKE THIS WORK; IT DOESN'T WORK RIGHT NOW
        for (int px = x - 1; px <= x + 1; px ++) {
            for (int py = y - 1; py <= y + 1; py ++) {
                if (px == x && py == y) continue;
                int posDiffX = Helpers.div(px, CHUNK_SIZE);
                int posDiffY = Helpers.div(py, CHUNK_SIZE);
                if (posDiffX != 0 && posDiffY != 0) {
                    Chunk neighbor = chunk.adjacentChunks.getNeighbor(posDiffX, posDiffY);
                    if (neighbor == null) continue;
                    if (neighbor.getRaw(Helpers.mod(px, CHUNK_SIZE), Helpers.mod(py, CHUNK_SIZE)) == 0x01) neighborElectronHeads ++;
                } else {
                    if (chunk.getRaw(px, py) == 0x01) neighborElectronHeads ++;
                }
                // If there are more than two neighboring electron heads, remain a conductor
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
