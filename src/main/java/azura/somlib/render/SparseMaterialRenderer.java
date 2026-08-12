package azura.somlib.render;

import azura.somlib.sparse_material.SparseMaterial;
import azura.somlib.sparse_material.SparseMaterialState;
import azura.somlib.storage.SomLibAttachments;
import azura.somlib.storage.SparseMaterialChunkStorage;
import azura.somlib.world.WorldSparseMatter;
import azura.test_mod.materials.ModMaterials;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import nishio.lazuli_lib.core.events.LazuliRenderEvents;
import nishio.lazuli_lib.core.world_rendering.*;
import nishio.lazuli_lib.internals.LazuliLog;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

import static java.lang.Math.min;
import static java.lang.Math.signum;

public class SparseMaterialRenderer {
    public static void register(){
//        ClientTickEvents.START_WORLD_TICK.register(world ->{
//
//            ChunkPos center = new ChunkPos(
//                    MinecraftClient.getInstance().player.getBlockPos()
//            );
//
//            int radius = 10;
//            Map<ChunkPos, Map<BlockPos, Map<SparseMaterial, SparseMaterialState>>> renderQueue;
//            for (int cx = center.x - radius; cx <= center.x + radius; cx++) {
//                for (int cz = center.z - radius; cz <= center.z + radius; cz++) {
//                    if (world.isChunkLoaded(cx, cz)) {
//                        WorldChunk chunk = world.getChunk(cx, cz);
//
//                        if (chunk == null) {
//                            continue;
//                        }
//
//                        SparseMaterialChunkStorage storage =
//                                chunk.getAttached(SomLibAttachments.SPARSE_MATTER);
//
//                        if(storage == null) continue;
//
//                    }
//                }
//            }
//
//            List<Map.Entry<BlockPos, Map<SparseMaterial, SparseMaterialState>>> entries =
//                    new ArrayList<>(storage.values().entrySet());
//
//            entries.sort(Comparator.comparingDouble(
//                    e -> -e.getKey().getSquaredDistance(camera)
//            ));
//        });


        LazuliRenderEvents.registerRenderCallback(ctx ->{
            ClientWorld world = MinecraftClient.getInstance().world;

            ChunkPos center = new ChunkPos(
                    MinecraftClient.getInstance().player.getBlockPos()
            );

            int radius = 10;

            for (int currentRadius = radius; currentRadius >= 0; currentRadius--) {
                for (int borderIn = currentRadius; borderIn >= 0; borderIn--) {
                    for (int side = -4 * min(1, borderIn); side <= 3 * min(1, currentRadius); side++) {
                        int cx = currentRadius;
                        int cz = (int) (borderIn * signum(((float) side) + 0.1));
                        int dir = (side + 4) % 4;
                        cx = (dir == 0)? cx : (dir == 2)?  -cx : (dir == 1)? cz : -cz;
                        cz = (dir == 0)? cz : (dir == 2)? -cz : (dir == 1)? -currentRadius : currentRadius;

                        cx += center.x;
                        cz += center.z;


                        if (world.isChunkLoaded(cx, cz)) {
                            WorldChunk chunk = world.getChunk(cx, cz);

                            if (chunk == null) {
                                continue;
                            }

                            SparseMaterialChunkStorage storage =
                                    chunk.getAttached(SomLibAttachments.SPARSE_MATTER);

                            if (storage == null) continue;

                            renderChunk(storage, chunk, ctx);
                        }
                    }
                }
            }
        });
    }

    public static void renderChunk(SparseMaterialChunkStorage storage, WorldChunk chunk, LazuliRenderContext ctx){
        //LazuliLog.Shaders.info("I'm surelly trying!");
        if (!storage.getStableStorage().isEmpty()){
            LazuliBufferBuilder bb = null;
            LapisRenderer.cleanupRenderSystem();
            LapisRenderer.enableCull();
            RenderSystem.enableBlend();
            LapisRenderer.setShader(GameRenderer.getPositionColorProgram());

            Vector3f cameraVec = ctx.camera().getVerticalPlane().cross(ctx.camera().getHorizontalPlane()).normalize();
            BlockPos cameraPos = ctx.camera().getBlockPos();
            List<Map.Entry<BlockPos, Map<SparseMaterial, SparseMaterialState>>> ordered = new ArrayList<>(storage.getStableStorage().entrySet());

            ordered.sort(Comparator.comparingDouble(entry -> -entry.getKey().getSquaredDistance(cameraPos)));

            float test = 0;
            for (var entry : ordered){
                test += 0.01f;
                //LazuliLog.Shaders.info("Rendering at " + entry.getKey().toShortString());
                Map<SparseMaterial, SparseMaterialState> materials =
                        new HashMap<>(entry.getValue());

                if (materials.isEmpty()) continue;

                if (bb == null){
                    bb = ctx.getLazuliBB(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
                }

                SparseMaterialState state =
                        materials.values().iterator().next();

                LapisRenderer.setShaderTexture(0, state.getType().textureId());

                float a = 1f;
                //a = 0.9f;
                double s = 0.1f;

                bb.setRenderSpace(new Transform3D(entry.getKey().toCenterPos(), new Quaternionf()));
                LazuliVertex AAA = new LazuliVertex().pos(-s, -s, -s).color(0.7f,0.7f,0.7f,a);
                LazuliVertex BAA = new LazuliVertex().pos( s, -s, -s).color(0.7f,0.7f,0.7f,a);
                LazuliVertex ABA = new LazuliVertex().pos(-s,  s, -s).color(0.7f,0.7f,0.7f,a);
                LazuliVertex BBA = new LazuliVertex().pos( s,  s, -s).color(0.7f,0.7f,0.7f,a);
                LazuliVertex AAB = new LazuliVertex().pos(-s, -s,  s).color(0.7f,0.7f,0.7f,a);
                LazuliVertex BAB = new LazuliVertex().pos( s, -s,  s).color(0.7f,0.7f,0.7f,a);
                LazuliVertex BBB = new LazuliVertex().pos( s,  s,  s).color(0.7f,0.7f,0.7f,a);
                LazuliVertex ABB = new LazuliVertex().pos(-s,  s,  s).color(0.7f,0.7f,0.7f,a);

                // Z
                bb.addVertex(AAA.uv(0, 1)).addVertex(ABA.uv(0, 0)).addVertex(BBA.uv(1, 0)).addVertex(BAA.uv(1, 1));
                bb.addVertex(AAB.uv(1, 1)).addVertex(BAB.uv(0, 1)).addVertex(BBB.uv(0, 0)).addVertex(ABB.uv(1, 0));

                // X
                bb.addVertex(AAA.uv(1, 1)).addVertex(BAA.uv(0, 1)).addVertex(BAB.uv(0, 0)).addVertex(AAB.uv(1, 0));
                bb.addVertex(ABA.uv(0, 1)).addVertex(ABB.uv(1, 1)).addVertex(BBB.uv(1, 0)).addVertex(BBA.uv(0, 0));

                // Y
                bb.addVertex(AAA.uv(0, 0)).addVertex(AAB.uv(0, 1)).addVertex(ABB.uv(1, 1)).addVertex(ABA.uv(1, 0));
                bb.addVertex(BAA.uv(0, 1)).addVertex(BBA.uv(0, 0)).addVertex(BBB.uv(1, 0)).addVertex(BAB.uv(1, 1));

                a = min(state.getConcentration() * 0.25f, 1f);
                s = 0.5f;

                bb.setRenderSpace(new Transform3D(entry.getKey().toCenterPos(), new Quaternionf()));
                AAA = new LazuliVertex().pos(-s, -s, -s).color(0.7f,0.7f,0.7f,a);
                BAA = new LazuliVertex().pos( s, -s, -s).color(0.7f,0.7f,0.7f,a);
                ABA = new LazuliVertex().pos(-s,  s, -s).color(0.7f,0.7f,0.7f,a);
                BBA = new LazuliVertex().pos( s,  s, -s).color(0.7f,0.7f,0.7f,a);
                AAB = new LazuliVertex().pos(-s, -s,  s).color(0.7f,0.7f,0.7f,a);
                BAB = new LazuliVertex().pos( s, -s,  s).color(0.7f,0.7f,0.7f,a);
                ABB = new LazuliVertex().pos(-s,  s,  s).color(0.7f,0.7f,0.7f,a);
                BBB = new LazuliVertex().pos( s,  s,  s).color(0.7f,0.7f,0.7f,a);

                // Z
                bb.addVertex(AAA.uv(0, 1)).addVertex(ABA.uv(0, 0)).addVertex(BBA.uv(1, 0)).addVertex(BAA.uv(1, 1));
                bb.addVertex(AAB.uv(1, 1)).addVertex(BAB.uv(0, 1)).addVertex(BBB.uv(0, 0)).addVertex(ABB.uv(1, 0));

                // X
                bb.addVertex(AAA.uv(1, 1)).addVertex(BAA.uv(0, 1)).addVertex(BAB.uv(0, 0)).addVertex(AAB.uv(1, 0));
                bb.addVertex(ABA.uv(0, 1)).addVertex(ABB.uv(1, 1)).addVertex(BBB.uv(1, 0)).addVertex(BBA.uv(0, 0));

                // Y
                bb.addVertex(AAA.uv(0, 0)).addVertex(AAB.uv(0, 1)).addVertex(ABB.uv(1, 1)).addVertex(ABA.uv(1, 0));
                bb.addVertex(BAA.uv(0, 1)).addVertex(BBA.uv(0, 0)).addVertex(BBB.uv(1, 0)).addVertex(BAB.uv(1, 1));


            }
            if (bb != null) {
                bb.draw();
            }
        }
    }
}













