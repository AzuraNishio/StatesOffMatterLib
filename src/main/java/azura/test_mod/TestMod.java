package azura.test_mod;

import azura.somlib.sparse_material.SparseMaterialRegistry;
import azura.test_mod.materials.ModMaterials;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMod implements ModInitializer {
	public static final String MOD_ID = "test_mod_som";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

		LOGGER.info("Oooohh thas a cool (test) mod!");

		testFacility.register();
		ModMaterials.register();
		LiveTweaker.register();




	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
