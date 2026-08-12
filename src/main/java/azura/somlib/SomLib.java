package azura.somlib;

import azura.somlib.commands.ArgumentTypes;
import azura.somlib.commands.StatesOfMatterCommands;
import azura.somlib.networking.ServerSparseMatterUpdater;
import azura.somlib.render.SparseMaterialRenderer;
import azura.somlib.sparse_material.SparseMaterialRegistry;
import azura.somlib.storage.SomLibAttachments;
import azura.somlib.world.WorldLoadedChunkTracker;
import azura.somlib.world.WorldSparseMatterTicker;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SomLib implements ModInitializer {
	public static final String MOD_ID = "statesofmatterlib";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("I wonder what gases you'll meet today.... ˙⋆✮⋆˚࿔");

		SomLibAttachments.register();
		SparseMaterialRegistry.bootstrap();
		StatesOfMatterCommands.register();
		SparseMaterialRenderer.register();
		ArgumentTypes.register();
		ServerSparseMatterUpdater.register();
		WorldLoadedChunkTracker.register();
		WorldSparseMatterTicker.register();

	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
