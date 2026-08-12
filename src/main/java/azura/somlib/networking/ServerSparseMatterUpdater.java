package azura.somlib.networking;

import azura.somlib.storage.SparseMaterialChunkStorage;
import azura.somlib.world.WorldSparseMatter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class ServerSparseMatterUpdater {
    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register((world) -> {
            for (var entry : WorldSparseMatter.dirty.entrySet()) {
                ChunkPos chunkPos = entry.getKey();
                SparseMaterialChunkStorage storage = entry.getValue();

                PacketByteBuf buf = PacketByteBufs.create();

                buf.writeInt(chunkPos.x);
                buf.writeInt(chunkPos.z);

                storage.writeUpdatePacketAndClear(buf);

                for (ServerPlayerEntity player : world.getPlayers()) {
                    ChunkPos playerChunk = player.getChunkPos();

                    int dx = Math.abs(playerChunk.x - chunkPos.x);
                    int dz = Math.abs(playerChunk.z - chunkPos.z);

                    if (dx <= 10 && dz <= 10) {
                        ServerPlayNetworking.send(player, PacketIds.CHUNK_SINC_PACKET, buf);
                    }
                }
            }
            WorldSparseMatter.dirty.clear();
        });
    }
}