package com.cjburkey.jautomata.world;

import com.cjburkey.wireworld.Util;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Optional;
import org.joml.Vector2i;
import org.joml.Vector3dc;

/**
 * Created by CJ Burkey on 2018/11/25
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class World {
    
    public static final byte CHUNK_SIZE = 32;
    
    private final Object2ObjectOpenHashMap<Vector2i, Chunk> chunks = new Object2ObjectOpenHashMap<>();
    
    public void setTile(Vector2i worldPos, byte tileType) {
        Vector2i containing = getContainingChunk(worldPos);
        if (!hasChunk(containing)) addChunk(new Chunk(containing));
        
        // This should always work, but IDEA doesn't want me doing an "unchecked get() call" on Optional instances
        getChunk(containing).ifPresent(
                chunk -> chunk.set(Util.mod((byte) worldPos.x, CHUNK_SIZE), Util.mod((byte) worldPos.y, CHUNK_SIZE), tileType));
    }
    
    public Optional<Chunk> getChunk(Vector2i chunkPos) {
        if (hasChunk(chunkPos)) return Optional.ofNullable(chunks.get(chunkPos));
        return Optional.empty();
    }
    
    public void addChunk(Chunk chunk) {
        if (hasChunk(chunk.getChunkPos())) return;
        chunks.put(chunk.getChunkPos(), chunk);
        update(chunk.getChunkPos().x, chunk.getChunkPos().y, chunk);
    }
    
    private void update(int x, int y, Chunk newChunk) {
        getChunk(new Vector2i(x, y - 1)).ifPresent(chunk -> chunk.adjacentS = newChunk);
        getChunk(new Vector2i(x, y + 1)).ifPresent(chunk -> chunk.adjacentN = newChunk);
        getChunk(new Vector2i(x + 1, y)).ifPresent(chunk -> chunk.adjacentW = newChunk);
        getChunk(new Vector2i(x - 1, y)).ifPresent(chunk -> chunk.adjacentE = newChunk);
        getChunk(new Vector2i(x + 1, y - 1)).ifPresent(chunk -> chunk.adjacentSW = newChunk);
        getChunk(new Vector2i(x - 1, y - 1)).ifPresent(chunk -> chunk.adjacentSE = newChunk);
        getChunk(new Vector2i(x + 1, y + 1)).ifPresent(chunk -> chunk.adjacentNW = newChunk);
        getChunk(new Vector2i(x - 1, y + 1)).ifPresent(chunk -> chunk.adjacentNE = newChunk);
    }
    
    public void update() {
        for (Chunk chunk : chunks.values()) chunk.update();
        // Update needs to be done before the front-back buffers are flipped
        for (Chunk chunk : chunks.values()) chunk.swapBuffers();
    }
    
    public void render(IRenderFunction renderFunction) {
        for (Chunk chunk : chunks.values()) chunk.render(renderFunction);
    }
    
    public Vector2i getContainingChunk(Vector2i worldPos) {
        return new Vector2i((int) Math.floor((float) worldPos.x / CHUNK_SIZE), (int) Math.floor((float) worldPos.y / CHUNK_SIZE));
    }
    
    public boolean hasChunk(Vector2i chunkPos) {
        return chunks.containsKey(chunkPos);
    }
    
    @FunctionalInterface
    public interface IRenderFunction {
        void render(int x, int y, Vector3dc color);
    }
    
}
