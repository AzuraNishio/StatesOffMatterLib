package azura.somlib.storage;

import azura.somlib.SomLib;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class SomLibAttachments {
    // Unknown attachment type statesofmatterlib:sparse_material_chunk_storage found when deserializing, skipping
    @SuppressWarnings("UnstableApiUsage")
    public static final AttachmentType<SparseMaterialChunkStorage> SPARSE_MATTER =
            AttachmentRegistry. <SparseMaterialChunkStorage> builder()
                    .persistent(SparseMaterialChunkStorage.CODEC)
                    .initializer(SparseMaterialChunkStorage::new)
                    .buildAndRegister(
                            SomLib.id("sparse_material_chunk_storage")
                    );

    public static void register(){

    }
}