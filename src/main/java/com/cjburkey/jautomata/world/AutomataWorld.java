package com.cjburkey.jautomata.world;

import com.cjburkey.jautomata.IAutomataHandler;
import com.cjburkey.jautomata.util.Helpers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Optional;
import org.joml.Vector2i;
import org.joml.Vector3dc;

/**
 * Created by CJ Burkey on 2018/11/25
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AutomataWorld {
    
    public static final byte CHUNK_SIZE = 64;
    public static final int CHUNK_AREA = CHUNK_SIZE * CHUNK_SIZE;
    
    private final Object2ObjectOpenHashMap<Vector2i, Chunk> chunks = new Object2ObjectOpenHashMap<>();
    private final IAutomataHandler automataHandler;
    private int renderingChunks;
    
    public AutomataWorld(IAutomataHandler automataHandler) {
        this.automataHandler = automataHandler;
    }
    
    public void setTile(Vector2i worldPos, byte tileType, boolean create) {
        Vector2i containing = getContainingChunk(worldPos);
        if (!hasChunk(containing)) {
            if (create) addChunk(containing);
            else return;
        }
        
        // This should always work, but IDEA doesn't want me doing an "unchecked get() call" on Optional instances
        getChunk(containing).ifPresent(
                chunk -> chunk.set(Helpers.mod((byte) worldPos.x, CHUNK_SIZE), Helpers.mod((byte) worldPos.y, CHUNK_SIZE), tileType));
    }
    
    public Optional<Chunk> getChunk(Vector2i chunkPos) {
        if (hasChunk(chunkPos)) return Optional.ofNullable(chunks.get(chunkPos));
        return Optional.empty();
    }
    
    public void addChunk(Vector2i pos) {
        if (hasChunk(pos)) return;
        Chunk chunk = new Chunk(pos, automataHandler);
        chunks.put(pos, chunk);
        update(pos.x, pos.y, chunk);
    }
    
    private void update(int x, int y, Chunk newChunk) {
        Optional<Chunk> n = getChunk(new Vector2i(x, y - 1));
        Optional<Chunk> s = getChunk(new Vector2i(x, y + 1));
        Optional<Chunk> e = getChunk(new Vector2i(x + 1, y));
        Optional<Chunk> w = getChunk(new Vector2i(x - 1, y));
        Optional<Chunk> ne = getChunk(new Vector2i(x + 1, y - 1));
        Optional<Chunk> nw = getChunk(new Vector2i(x - 1, y - 1));
        Optional<Chunk> se = getChunk(new Vector2i(x + 1, y + 1));
        Optional<Chunk> sw = getChunk(new Vector2i(x - 1, y + 1));
        
        n.ifPresent(chunk -> chunk.adjacentChunks.adjacentS = newChunk);
        s.ifPresent(chunk -> chunk.adjacentChunks.adjacentN = newChunk);
        e.ifPresent(chunk -> chunk.adjacentChunks.adjacentW = newChunk);
        w.ifPresent(chunk -> chunk.adjacentChunks.adjacentE = newChunk);
        ne.ifPresent(chunk -> chunk.adjacentChunks.adjacentSW = newChunk);
        nw.ifPresent(chunk -> chunk.adjacentChunks.adjacentSE = newChunk);
        se.ifPresent(chunk -> chunk.adjacentChunks.adjacentNW = newChunk);
        sw.ifPresent(chunk -> chunk.adjacentChunks.adjacentNE = newChunk);
        
        newChunk.adjacentChunks.adjacentN = n.orElse(null);
        newChunk.adjacentChunks.adjacentS = s.orElse(null);
        newChunk.adjacentChunks.adjacentE = e.orElse(null);
        newChunk.adjacentChunks.adjacentW = w.orElse(null);
        newChunk.adjacentChunks.adjacentNE = ne.orElse(null);
        newChunk.adjacentChunks.adjacentNW = nw.orElse(null);
        newChunk.adjacentChunks.adjacentSE = se.orElse(null);
        newChunk.adjacentChunks.adjacentSW = sw.orElse(null);
    }
    
    public void tick() {
        for (Chunk chunk : chunks.values()) chunk.update();
        // Update needs to be done before the front-back buffers are flipped
        for (Chunk chunk : chunks.values()) chunk.swapBuffers();
    }
    
    public void reset() {
        for (Chunk chunk : chunks.values()) chunk.reset(true);
    }
    
    public void render(IRenderFunction renderFunction) {
        renderingChunks = 0;
        for (Chunk chunk : chunks.values()) {
            if (chunk.render(renderFunction)) renderingChunks ++;
        }
    }
    
    public Vector2i getContainingChunk(Vector2i worldPos) {
        return new Vector2i((int) Math.floor((float) worldPos.x / CHUNK_SIZE), (int) Math.floor((float) worldPos.y / CHUNK_SIZE));
    }
    
    public boolean hasChunk(Vector2i chunkPos) {
        return chunks.containsKey(chunkPos);
    }
    
    public int getRenderingChunks() {
        return renderingChunks;
    }
    
    @FunctionalInterface
    public interface IRenderFunction {
        void render(int x, int y, int w, int h, Vector3dc color);
    }
    
}
