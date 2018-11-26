package com.cjburkey.wireworld.automata;

import com.cjburkey.jautomata.world.Chunk;
import com.cjburkey.jautomata.world.IAutomataHandler;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import static com.cjburkey.jautomata.world.World.*;

/**
 * Created by CJ Burkey on 2018/11/26
 */
public class AutomataWireWorld implements IAutomataHandler {
    
    private static Byte2ObjectArrayMap<Vector3dc> colorMap = new Byte2ObjectArrayMap<>();
    
    static {
        colorMap.put((byte) 0x00, null);
        colorMap.put((byte) 0x01, new Vector3d(0.3d, 0.3d, 1.0d));
        colorMap.put((byte) 0x02, new Vector3d(1.0d, 0.3d, 0.3d));
        colorMap.put((byte) 0x03, new Vector3d(1.0d, 1.0d, 0.3d));
    }
    
    private Chunk adjacentN = null;
    private Chunk adjacentNE = null;
    private Chunk adjacentNW = null;
    private Chunk adjacentS = null;
    private Chunk adjacentSE = null;
    private Chunk adjacentSW = null;
    private Chunk adjacentE = null;
    private Chunk adjacentW = null;
    
    public void onNeighborChunkChanged(Neighbor neighbor, Chunk newChunk) {
        switch (neighbor) {
            case NORTH:
                adjacentN = newChunk;
                break;
            case SOUTH:
                adjacentS = newChunk;
                break;
            case EAST:
                adjacentE = newChunk;
                break;
            case WEST:
                adjacentW = newChunk;
                break;
            case NORTH_EAST:
                adjacentNE = newChunk;
                break;
            case NORTH_WEST:
                adjacentNW = newChunk;
                break;
            case SOUTH_EAST:
                adjacentNE = newChunk;
                break;
            case SOUTH_WEST:
                adjacentNW = newChunk;
                break;
        }
    }
    
    public byte process(Chunk chunk, byte tileType, int dataIndex, byte x, byte y) {
        switch (tileType) {
            case 0x00: return 0x00;
            case 0x01: return 0x02;
            case 0x02: return 0x03;
            case 0x03: return processConductor(chunk, x, y);
            default: return 0x00;
        }
    }
    
    private byte processConductor(Chunk chunk, byte x, byte y) {
        // Whether neighbors are outside of this chunk
        boolean overNorth = (y == 0);
        boolean overSouth = (y == CHUNK_SIZE - 1);
        boolean overEast = (x == CHUNK_SIZE - 1);
        boolean overWest = (x == 0);
        
        // Whether the neighbors could have a value
        boolean haveNorth = ((overNorth && adjacentN != null) || y > 0);
        boolean haveNorthEast = ((overNorth && overEast && adjacentNE != null) || (y > 0 && x < CHUNK_SIZE - 1));
        boolean haveNorthWest = ((overNorth && overWest && adjacentNW != null) || (y > 0 && x > 0));
        boolean haveSouth = ((overSouth && adjacentS != null) || y < CHUNK_SIZE - 1);
        boolean haveSouthEast = ((overSouth && overEast && adjacentSE != null) || (y < CHUNK_SIZE - 1 && x < CHUNK_SIZE - 1));
        boolean haveSouthWest = ((overSouth && overWest && adjacentSW != null) || (y < CHUNK_SIZE - 1 && x > 0));
        boolean haveEast = ((overEast && adjacentE != null) || x < CHUNK_SIZE - 1);
        boolean haveWest = ((overWest && adjacentW != null) || x > 0);
        
        // Neighbors
        byte n;
        byte ne;
        byte nw;
        byte s;
        byte se;
        byte sw;
        byte e;
        byte w;
        
        int neighborElectronHeads = 0;
        
        n = (haveNorth ? (overNorth ? adjacentN.getRaw(x, CHUNK_SIZE - 1) : chunk.getRaw(x, y - 1)) : 0x00);
        if (n == 0x01) neighborElectronHeads ++;
        
        s = (haveSouth ? (overSouth ? adjacentS.getRaw(x, 0) : chunk.getRaw(x, y + 1)) : 0x00);
        if (s == 0x01) neighborElectronHeads ++;
        
        e = (haveEast ? (overEast ? adjacentE.getRaw(0, y) : chunk.getRaw(x + 1, y)) : 0x00);
        if (e == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return 0x03; // Only up to 2 neighbor heads allowed for multiplication
        }
        
        w = (haveWest ? (overWest ? adjacentW.getRaw(CHUNK_SIZE - 1, y) : chunk.getRaw(x - 1, y)) : 0x00);
        if (w == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return 0x03;
        }
        
        ne = (haveNorthEast ? (overNorth && overEast ? adjacentNE.getRaw(0, CHUNK_SIZE - 1) :chunk.getRaw(x + 1, y - 1)) : 0x00);
        if (ne == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return 0x03;
        }
        
        nw = (haveNorthWest ? (overNorth && overWest ? adjacentNW.getRaw(CHUNK_SIZE - 1, CHUNK_SIZE - 1) : chunk.getRaw(x - 1, y - 1)) : 0x00);
        if (nw == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return 0x03;
        }
        
        se = (haveSouthEast ? (overSouth && overEast ? adjacentSE.getRaw(0, 0) : chunk.getRaw(x + 1, y + 1)) : 0x00);
        if (se == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return 0x03;
        }
        
        sw = (haveSouthWest ? (overSouth && overWest ? adjacentSW.getRaw(CHUNK_SIZE - 1, 0) : chunk.getRaw(x - 1, y + 1)) : 0x00);
        if (sw == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return 0x03;
        }
        
        // If there are no adjacent electron heads, don't become an electron head
        if (neighborElectronHeads == 0) return 0x03;
        
        // Become an electron head if there are 1 or 2 neighboring electron heads
        return 0x01;
    }
    
    public Byte2ObjectArrayMap<Vector3dc> getColorMap() {
        return colorMap;
    }
    
    public byte getResetType(byte input) {
        if (input == 0x01 || input == 0x02) return 0x03;
        return input;
    }
    
}
