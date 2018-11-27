package com.cjburkey.jautomata.world;

import com.cjburkey.jautomata.IAutomataHandler;
import com.cjburkey.jautomata.util.Render;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3dc;

import static com.cjburkey.jautomata.world.AutomataWorld.*;

/**
 * Created by CJ Burkey on 2018/11/25
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Chunk {
    
    private byte[] front = new byte[CHUNK_AREA];
    private byte[] back = new byte[CHUNK_AREA];
    private final Vector2i chunkPos = new Vector2i();
    private final IAutomataHandler automataHandler;
    
    public final AdjacentChunks adjacentChunks = new AdjacentChunks();
    
    Chunk(Vector2i chunkPos, IAutomataHandler automataHandler) {
        this.chunkPos.set(chunkPos);
        this.automataHandler = automataHandler;
    }
    
    void update() {
        int index = 0;
        for (byte y = 0; y < CHUNK_SIZE; y ++) {
            for (byte x = 0; x < CHUNK_SIZE; x ++) {
                back[index] = automataHandler.process(this, front[index], index, x, y);
                index ++;
            }
        }
    }
    
    // Once an update is finished, update all the chunks front-end buffers separately
    // Using a swap-buffers system prevents the new data from interfering with the old data
    void swapBuffers() {
        byte[] tmp = front;
        front = back;
        back = tmp;
    }
    
    @SuppressWarnings("SameParameterValue")
    void reset(boolean swap) {
        for (int index = 0; index < CHUNK_AREA; index ++) {
            back[index] = automataHandler.getResetTile(front[index]);
        }
        if (swap) {
            swapBuffers();
        }
    }
    
    boolean render(IRenderFunction renderFunction) {
        Vector2i worldPos = getWorldPos();
        int worldX = worldPos.x;
        int worldY = worldPos.y;
        if (!Render.getIsOnScreen(new Vector2d(worldX, worldY), CHUNK_SIZE)) return false;
        
        Byte2ObjectArrayMap<Vector3dc> colorMap = automataHandler.getColorMap();
        int index = 0;
        for (byte y = 0; y < CHUNK_SIZE; y++) {
            for (byte x = 0; x < CHUNK_SIZE; x++) {
                Vector3dc color = colorMap.get(front[index]);
                if (color != null) renderFunction.render(worldX + x, worldY + y, 1, 1, color);
                index++;
            }
        }
        return true;
    }
    
    public void set(byte x, byte y, byte type) {
        int at = getIndex(x, y);
        if (at >= 0) front[at] = type;
    }
    
    public byte getRaw(int x, int y) {
        int at = y * CHUNK_SIZE + x;
        if (at < 0 || at >= CHUNK_AREA) return -0x01;
        return front[at];
    }
    
    public Vector2i getChunkPos() {
        return new Vector2i(chunkPos);
    }
    
    public Vector2i getWorldPos() {
        return new Vector2i(chunkPos).mul(CHUNK_SIZE);
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
