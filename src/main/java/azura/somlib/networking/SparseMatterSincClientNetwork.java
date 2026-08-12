package azura.somlib.networking;

import azura.somlib.SomLib;
import azura.somlib.storage.SomLibAttachments;
import azura.somlib.storage.SparseMaterialChunkStorage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class SparseMatterSincClientNetwork {


    public static void start() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.CHUNK_SINC_PACKET, (client, handler, buffer, res) -> {
            try {
                int chunkX = buffer.readInt();
                int chunkZ = buffer.readInt();

                buffer.retain();

                client.execute(() -> {
                    try {
                         SparseMaterialChunkStorage storage = client.world.getChunk(chunkX, chunkZ).getAttachedOrCreate(SomLibAttachments.SPARSE_MATTER);
                         storage.applyUpdatePacket(buffer);
                    } finally {
                        buffer.release();
                    }
                });
            } catch (Exception e) {
                SomLib.LOGGER.error("Error while sincronizing sparse matter chunk data from network!", e);
                throw e;
            }
        });
    }
}