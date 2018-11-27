package com.cjburkey.jautomata.world;

/**
 * Created by CJ Burkey on 2018/11/26
 */
@SuppressWarnings("WeakerAccess")
public class AdjacentChunks {
    
    public Chunk adjacentN = null;
    public Chunk adjacentNE = null;
    public Chunk adjacentNW = null;
    public Chunk adjacentS = null;
    public Chunk adjacentSE = null;
    public Chunk adjacentSW = null;
    public Chunk adjacentE = null;
    public Chunk adjacentW = null;
    
    public Chunk getNeighbor(int xDiff, int yDiff) {
        if (yDiff == -1) {
            if (xDiff == -1) return adjacentNW;
            if (xDiff == 1) return adjacentNE;
            return adjacentN;
        }
        if (yDiff == 0) {
            if (xDiff == -1) return adjacentW;
            if (xDiff == 1) return adjacentE;
            return null;
        }
        if (yDiff == 1) {
            if (xDiff == -1) return adjacentSW;
            if (xDiff == 1) return adjacentSE;
            return adjacentS;
        }
        return null;
    }
    
}
