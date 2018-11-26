package com.cjburkey.jautomata.world;

import com.cjburkey.wireworld.Render;
import com.cjburkey.wireworld.Util;
import org.joml.Vector2i;
import org.joml.Vector3d;

import static com.cjburkey.jautomata.world.World.*;

/**
 * Created by CJ Burkey on 2018/11/25
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Chunk {
    
    public static final int AREA = CHUNK_SIZE * CHUNK_SIZE;
    
    private static final Vector3d COLOR_ELECTRON_HEAD = new Vector3d(0.3d, 0.3d, 1.0d);
    private static final Vector3d COLOR_ELECTRON_TAIL = new Vector3d(1.0d, 0.3d, 0.3d);
    private static final Vector3d COLOR_CONDUCTOR = new Vector3d(1.0d, 1.0d, 0.3d);
    
    private byte[] front = new byte[AREA];
    private byte[] back = new byte[AREA];
    private final Vector2i chunkPos = new Vector2i();
    
    public Chunk adjacentN = null;
    public Chunk adjacentNE = null;
    public Chunk adjacentNW = null;
    public Chunk adjacentS = null;
    public Chunk adjacentSE = null;
    public Chunk adjacentSW = null;
    public Chunk adjacentE = null;
    public Chunk adjacentW = null;
    
    public Chunk(Vector2i chunkPos) {
        this.chunkPos.set(chunkPos);
    }
    
    public void set(byte x, byte y, byte type) {
        int at = getIndex(x, y);
        if (at >= 0) front[at] = type;
    }
    
    public void fill(byte x1, byte y1, byte x2, byte y2, byte type) {
        if (valid(x1, y1) && valid(x2, y2)) {
            byte sx = Util.min(x1, x2);
            byte sy = Util.min(y1, y2);
            byte fx = Util.max(x1, x2);
            byte fy = Util.max(y1, y2);
            int index = getIndexRaw(sx, sy);
            for (byte y = sy; y <= fy; y ++) {
                for (byte x = sy; x <= fx; x ++) {
                    front[index] = type;
                    index ++;
                }
                index += CHUNK_SIZE;
                index -= ((fx - sx) + 1);
            }
        }
    }
    
    public void update() {
        int index = 0;
        for (byte y = 0; y < CHUNK_SIZE; y ++) {
            for (byte x = 0; x < CHUNK_SIZE; x ++) {
                switch (front[index]) {
                    case 0x00: break;
                    case 0x01:
                        back[index] = 0x02;
                        break;
                    case 0x02:
                        back[index] = 0x03;
                        break;
                    case 0x03:
                        processConductor(x, y, index);
                        break;
                }
                index ++;
            }
        }
    }
    
    // This is a bit lengthy, but I don't care right now
    // This is so complex because 
    private void processConductor(byte x, byte y, int index) {
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
        
        n = (haveNorth ? (overNorth ? adjacentN.getRaw(x, CHUNK_SIZE - 1) : getRaw(x, y - 1)) : 0x00);
        if (n == 0x01) neighborElectronHeads ++;
        
        s = (haveSouth ? (overSouth ? adjacentS.getRaw(x, 0) : getRaw(x, y + 1)) : 0x00);
        if (s == 0x01) neighborElectronHeads ++;
        
        e = (haveEast ? (overEast ? adjacentE.getRaw(0, y) : getRaw(x + 1, y)) : 0x00);
        if (e == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return;  // Only up to 2 neighbor heads allowed for multiplication
        }
        
        w = (haveWest ? (overWest ? adjacentW.getRaw(CHUNK_SIZE - 1, y) : getRaw(x - 1, y)) : 0x00);
        if (w == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return;  // Only up to 2 neighbor heads allowed for multiplication
        }
        
        ne = (haveNorthEast ? (overNorth && overEast ? adjacentNE.getRaw(0, CHUNK_SIZE - 1) : getRaw(x + 1, y - 1)) : 0x00);
        if (ne == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return;  // Only up to 2 neighbor heads allowed for multiplication
        }
        
        nw = (haveNorthWest ? (overNorth && overWest ? adjacentNW.getRaw(CHUNK_SIZE - 1, CHUNK_SIZE - 1) : getRaw(x - 1, y - 1)) : 0x00);
        if (nw == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return;  // Only up to 2 neighbor heads allowed for multiplication
        }
        
        se = (haveSouthEast ? (overSouth && overEast ? adjacentSE.getRaw(0, 0) : getRaw(x + 1, y + 1)) : 0x00);
        if (se == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return;  // Only up to 2 neighbor heads allowed for multiplication
        }
        
        sw = (haveSouthWest ? (overSouth && overWest ? adjacentSW.getRaw(CHUNK_SIZE - 1, 0) : getRaw(x - 1, y + 1)) : 0x00);
        if (sw == 0x01) {
            neighborElectronHeads ++;
            if (neighborElectronHeads > 2) return;  // Only up to 2 neighbor heads allowed for multiplication
        }
        
        // If there are no adjacent electron heads, don't become an electron head
        if (neighborElectronHeads == 0) return;
        
        // Become head if there are 1 or 2 neighboring electron heads
        back[index] = 0x01;
    }
    
    // Once an update is finished, update all the chunks front-end buffers separately
    public void swapBuffers() {
        byte[] tmp = front;
        front = back;
        back = tmp;
    }
    
    public void reset(boolean swap) {
        for (int index = 0; index < AREA; index ++) {
            if (front[index] == 0x01 || front[index] == 0x02) front[index] = 0x03;
            back[index] = front[index];
        }
    }
    
    public byte getRaw(int x, int y) {
        int at = y * CHUNK_SIZE + x;
        if (at < 0 || at >= AREA) return -0x01;
        return front[at];
    }
    
    public Vector2i getChunkPos() {
        return new Vector2i(chunkPos);
    }
    
    public Vector2i getWorldPos() {
        return new Vector2i(chunkPos).mul(CHUNK_SIZE);
    }
    
    public void render(IRenderFunction renderFunction) {
        Vector2i worldPos = getWorldPos();
        int worldX = worldPos.x;
        int worldY = worldPos.y;
        // TODO: DEBUG
        Render.strokeRect(worldX, worldY, CHUNK_SIZE, CHUNK_SIZE, new Vector3d(0.5d, 0.5d, 0.5d), 1.0d / Render.getZoom());
        for (byte y = 0; y < CHUNK_SIZE; y ++) {
            for (byte x = 0; x < CHUNK_SIZE; x++) {
                switch (getRaw(x, y)) {
                    case 0x00: break;
                    case 0x01:
                        renderFunction.render(worldX + x, worldY + y, COLOR_ELECTRON_HEAD);
                        break;
                    case 0x02:
                        renderFunction.render(worldX + x, worldY + y, COLOR_ELECTRON_TAIL);
                        break;
                    case 0x03:
                        renderFunction.render(worldX + x, worldY + y, COLOR_CONDUCTOR);
                        break;
                }
            }
        }
    }
    
    private static int getIndexRaw(byte x, byte y) {
        return y * CHUNK_SIZE + x;
    }
    
    private static int getIndex(byte x, byte y) {
        return valid(x, y) ? getIndexRaw(x, y) : -1;
    }
    
    private static boolean valid(byte x, byte y) {
        return (x >= 0 && y >= 0 && x < CHUNK_SIZE && y < CHUNK_SIZE);
    }
    
}
