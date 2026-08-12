package azura.somlib.sparse_material;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public final class SparseMaterialRegistry {

    public static final RegistryKey<Registry<SparseMaterial>> MATERIAL_TYPE_KEY =
            RegistryKey.ofRegistry(
                    Identifier.of("states_of_matter_lib", "sparse_material_type")
            );

    public static final Registry<SparseMaterial> MATERIAL_TYPES =
            FabricRegistryBuilder
                    .createSimple(MATERIAL_TYPE_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static void bootstrap(){}
}