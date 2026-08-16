package azura.somlib.render;

import azura.somlib.SomLib;
import nishio.lazuli_lib.core.registry.LazuliShaderRegistry;
import nishio.lazuli_lib.core.shaders.LazuliShader;
import nishio.lazuli_lib.core.tools.LazuliShaderDevTools;
import nishio.lazuli_lib.core.warp.LazuliWarp;

public class SomLibShaders {

    public static LazuliShader gazShader;

    public static void register(){
        //LazuliShaderDevTools.enableFastShaderReloading();
        gazShader = new LazuliShader(SomLib.id("gaz_base")).addDefaultUniforms().addSampler("Sampler0").addGlobalTimeUniform().register();
        //test = new LazuliWarp(SomLib.id("lightmap_probe")).addTargets(LazuliWarpDefaultTargets.WORLD_TERRAIN).register();
        LazuliShaderRegistry.close();
    }
}
