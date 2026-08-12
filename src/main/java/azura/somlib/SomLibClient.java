package azura.somlib;

import azura.somlib.networking.SparseMatterSincClientNetwork;
import azura.somlib.render.SparseMaterialRenderer;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SomLibClient implements ClientModInitializer {
	public static final String MOD_ID = SomLib.MOD_ID;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + "_client");


	@Override
	public void onInitializeClient() {
		SparseMaterialRenderer.register();
		SparseMatterSincClientNetwork.start();
    }
}