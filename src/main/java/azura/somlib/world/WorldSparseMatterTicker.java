package azura.somlib.world;

import azura.somlib.sparse_material.SparseMaterial;
import azura.somlib.sparse_material.SparseMaterialState;
import azura.somlib.storage.SomLibAttachments;
import azura.somlib.storage.SparseMaterialChunkStorage;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

public class WorldSparseMatterTicker {
    static Random r = Random.create();
    static int diffusionLooper = 0;
    static int entityEffectLooper = 0;
    public static void register(){
        ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
            diffusionLooper++;
            diffusionLooper = diffusionLooper % 4;
            if (diffusionLooper == 0){
                entityEffectLooper++;
                entityEffectLooper = entityEffectLooper % 2;
            }
            List<WorldChunk> loadedChunks = WorldLoadedChunkTracker.loadedChunks.get(serverWorld);
            if (loadedChunks == null) return;
            for (WorldChunk chunk : new ArrayList<>(loadedChunks)){
                if (Math.floorMod(chunk.getPos().x + chunk.getPos().z, 2) == diffusionLooper) continue;
                SparseMaterialChunkStorage storage = chunk.getAttachedOrCreate(SomLibAttachments.SPARSE_MATTER);

                for (var entry : new ArrayList<>(storage.values().entrySet())) {
                    BlockPos pos = entry.getKey();
                    BlockState block = chunk.getBlockState(pos);

                    List<Entity> entities = null;

                    if (entityEffectLooper == 0){
                        Box box = new Box(pos);
                        entities = serverWorld.getOtherEntities(null, box);
                    }

                    for (SparseMaterialState state : new ArrayList<>(entry.getValue().values())) {
                        SparseMaterial type = state.getType();
                        state.updateLastConcentration();
                        boolean shouldMove = block.isSolid() || type.maxConcentration() < state.getConcentration() || (type.diffusionChance() > r.nextFloat());
                        float diffusionDivision = (block.isSolid()) ? 1f : type.diffusionDivision();
                        
                        if (shouldMove) {
                            Direction direction = type.getRandomMoveDirection(r);
                            BlockPos moveCandidate = pos.offset(direction);

                            if (serverWorld.isChunkLoaded(moveCandidate)) {
                                if (!serverWorld.getBlockState(moveCandidate).isSolid()) {
                                    WorldSparseMatter.addMaterial(serverWorld, moveCandidate, state.copy().multiplyConcentration(diffusionDivision * 0.98f));
                                    WorldSparseMatter.addMaterial(serverWorld, pos, state.copy().multiplyConcentration(-diffusionDivision));
                                }
                            }
                        }

                        if (entityEffectLooper == 0){
                            entities.forEach(state::applyEffectOnEntity);
                        }

                    }
                }
                storage.updateStable();

            }
        });
    }
}
