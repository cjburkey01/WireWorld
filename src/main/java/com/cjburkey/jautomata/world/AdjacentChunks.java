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
    
    public Chunk getChunkFromOffset(int ix, int iy) {
        int x = Integer.compare(ix, 0); // Essentially a 'sign' function
        int y = Integer.compare(iy, 0);
        if (y == -1) {
            if (x == -1) return adjacentNW;
            if (x == 1) return adjacentNE;
            return adjacentN;
        }
        if (y == 1) {
            if (x == -1) return adjacentSW;
            if (x == 1) return adjacentSE;
            return adjacentS;
        }
        if (y == 0) {
            if (x == -1) return adjacentW;
            if (x == 1) return adjacentE;
        }
        return null;
    }
    
}
