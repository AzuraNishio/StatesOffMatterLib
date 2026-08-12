package azura.somlib.mixin;

import azura.somlib.mixin_interfaces.ChunkDataExtraInterface;
import azura.somlib.storage.SomLibAttachments;
import azura.somlib.storage.SparseMaterialChunkStorage;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkData.class)
public class ChunkDataSparseMaterialSincMixin implements ChunkDataExtraInterface{

	@Unique
	public SparseMaterialChunkStorage sparseMatterAttachment = null;


	@Inject(at = @At("TAIL"), method = "Lnet/minecraft/network/packet/s2c/play/ChunkData;<init>(Lnet/minecraft/world/chunk/WorldChunk;)V")
	private void chunkDataFromWorldChunk(WorldChunk chunk, CallbackInfo ci){
		sparseMatterAttachment = chunk.getAttachedOrCreate(SomLibAttachments.SPARSE_MATTER);
	}

	@Inject(at = @At("TAIL"), method = "Lnet/minecraft/network/packet/s2c/play/ChunkData;<init>(Lnet/minecraft/network/PacketByteBuf;II)V")
	private void chunkDataFromBuff(PacketByteBuf buf, int x, int z, CallbackInfo ci){
		this.sparseMatterAttachment = SparseMaterialChunkStorage.fromBuff(buf);
	}

	@Inject(at = @At("TAIL"), method = "Lnet/minecraft/network/packet/s2c/play/ChunkData;write(Lnet/minecraft/network/PacketByteBuf;)V")
	private void writeBuff(PacketByteBuf buf, CallbackInfo ci){
		sparseMatterAttachment.writeBuff(buf);
	}

	@Override
	public SparseMaterialChunkStorage som_lib$getSparseMatterAttachment() {
		return sparseMatterAttachment;
	}
}