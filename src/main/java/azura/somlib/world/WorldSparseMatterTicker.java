package azura.somlib.world;

import azura.somlib.sparse_material.SparseMaterial;
import azura.somlib.sparse_material.SparseMaterialState;
import azura.somlib.storage.SomLibAttachments;
import azura.somlib.storage.SparseMaterialChunkStorage;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

public class WorldSparseMatterTicker {
    static Random r = Random.create();
    static int looper = 0;
    public static void register(){
        ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
            looper++;
            looper = looper % 4;
            List<WorldChunk> loadedChunks = WorldLoadedChunkTracker.loadedChunks.get(serverWorld);
            if (loadedChunks == null) return;

            for (WorldChunk chunk : new ArrayList<>(loadedChunks)){
                if (Math.floorMod(chunk.getPos().x + chunk.getPos().z, 2) * 2 == looper) continue;
                processChunk(chunk, serverWorld);
            }
        });
    }

    public static void processChunk(WorldChunk chunk, ServerWorld serverWorld){
        SparseMaterialChunkStorage storage = chunk.getAttachedOrCreate(SomLibAttachments.SPARSE_MATTER);

        for (var entry : new ArrayList<>(storage.values().entrySet())) {
            BlockPos pos = entry.getKey();
            BlockState block = chunk.getBlockState(pos);
            for (SparseMaterialState state : new ArrayList<>(entry.getValue().values())) {
                SparseMaterial type = state.getType();
                boolean isSolid = block.isSolid();
                boolean shouldMove = isSolid || type.maxConcentration() < state.getConcentration() || (type.diffusionChance() > r.nextFloat());
                float diffusionDivision = (isSolid) ? 1f : type.diffusionDivision();

                if (shouldMove) {
                    Direction direction = type.getRandomMoveDirection(r);
                    BlockPos moveCandidate = pos.offset(direction);

                    if (serverWorld.isChunkLoaded(moveCandidate)) {
                        if (!serverWorld.getBlockState(moveCandidate).isSolid()) {
                            SparseMaterialState delta = state.copy().multiplyConcentration(diffusionDivision);
                            WorldSparseMatter.addMaterial(serverWorld, moveCandidate, delta);
                            WorldSparseMatter.addMaterial(serverWorld, pos, delta.multiplyConcentration(-1));
                        }
                    }
                }
            }
        }
        storage.updateStable();
    }
}
