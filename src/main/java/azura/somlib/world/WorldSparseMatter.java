package azura.somlib.world;

import azura.somlib.sparse_material.SparseMaterialState;
import azura.somlib.storage.SomLibAttachments;
import azura.somlib.storage.SparseMaterialChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

public class WorldSparseMatter {
    public static Map<ChunkPos, SparseMaterialChunkStorage> dirty = new HashMap<>();

    public static void addMaterial(World world, BlockPos pos, SparseMaterialState state){
        SparseMaterialChunkStorage storage =  getStorage(world, pos);
        dirty.put(new ChunkPos(pos), storage);
        storage.add(pos, state);
    }

    public static void clear(World world, BlockPos pos){
        SparseMaterialChunkStorage storage =  getStorage(world, pos);
        storage.clear(pos);
        dirty.put(new ChunkPos(pos), storage);
    }

    public static SparseMaterialChunkStorage getStorage(World world, BlockPos pos){
        return world.getChunk(pos).getAttachedOrCreate(SomLibAttachments.SPARSE_MATTER);
    }

    public static void removeMaterial(World world, BlockPos pos, SparseMaterialState state){
        SparseMaterialChunkStorage storage =  getStorage(world, pos);
        dirty.put(new ChunkPos(pos), storage);
        storage.remove(pos, state.getType());
    }

    public static Collection<SparseMaterialState> getMaterials(World world, BlockPos pos){
        SparseMaterialChunkStorage storage =  getStorage(world, pos);
        dirty.put(new ChunkPos(pos), storage);
        return storage.get(pos).values();
    }
}
