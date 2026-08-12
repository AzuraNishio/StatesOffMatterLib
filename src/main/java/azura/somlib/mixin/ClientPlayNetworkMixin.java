package azura.somlib.mixin;

import azura.somlib.SomLib;
import azura.somlib.mixin_interfaces.ChunkDataExtraInterface;
import azura.somlib.storage.SomLibAttachments;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkMixin {
	@Inject(at = @At("TAIL"), method = "loadChunk(IILnet/minecraft/network/packet/s2c/play/ChunkData;)V")
	private void loadChunk(int x, int z, ChunkData chunkData, CallbackInfo ci){
		WorldChunk chunk = MinecraftClient.getInstance().world.getChunk(x, z);
		chunk.setAttached(SomLibAttachments.SPARSE_MATTER, ((ChunkDataExtraInterface) chunkData).som_lib$getSparseMatterAttachment());
	}
}