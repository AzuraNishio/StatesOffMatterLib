package azura.somlib;

import nishio.lazuli_lib.core.registry.LazuliShaderRegistry;
import nishio.lazuli_lib.core.shaders.LazuliShader;
import nishio.lazuli_lib.core.tools.LazuliShaderDevTools;
import nishio.lazuli_lib.core.warp.LazuliWarp;
import nishio.lazuli_lib.core.warp.LazuliWarpDefaultTargets;

public class SomLibShaders {

    public static LazuliWarp test;
    public static LazuliShader gazShader;

    public static void register(){
        LazuliShaderDevTools.enableFastShaderReloading();
        gazShader = new LazuliShader(SomLib.id("gaz_base")).addDefaultUniforms().addSampler("Sampler0").addGlobalTimeUniform().register();
        //test = new LazuliWarp(SomLib.id("lightmap_probe")).addTargets(LazuliWarpDefaultTargets.WORLD_TERRAIN).register();
        LazuliShaderRegistry.close();
    }
}
