package azura.test_mod.materials;

import azura.somlib.sparse_material.SparseMaterial;
import azura.somlib.sparse_material.SparseMaterialRegistry;
import azura.test_mod.TestMod;
import net.minecraft.registry.Registry;

public class ModMaterials {

    public static SparseMaterial gaz1 = registerSparseMaterial("test_gaz_1", new TestGaz1());
    public static SparseMaterial fluid1 = registerSparseMaterial("test_fluid_1", new TestFluid1());

    public static void register(){

    }



    private static SparseMaterial registerSparseMaterial(String name, SparseMaterial material) {
        return Registry.register(SparseMaterialRegistry.MATERIAL_TYPES, TestMod.id(name), material);
    }
}
