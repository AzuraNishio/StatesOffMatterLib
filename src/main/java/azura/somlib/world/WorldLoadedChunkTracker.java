package azura.somlib.world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldLoadedChunkTracker {
    public static Map<ServerWorld, List<WorldChunk>> loadedChunks = new HashMap<>();

    public static void register(){
        ServerChunkEvents.CHUNK_LOAD.register((serverWorld, chunk) -> {
            loadedChunks.computeIfAbsent(serverWorld, (world) -> new ArrayList<>()).add(chunk);
        });

        ServerChunkEvents.CHUNK_UNLOAD.register((serverWorld, chunk) -> {
            loadedChunks.computeIfAbsent(serverWorld, (world) -> new ArrayList<>()).remove(chunk);
        });
    }
}
