package azura.somlib.commands;

import azura.somlib.sparse_material.SparseMaterial;
import azura.somlib.sparse_material.SparseMaterialRegistry;
import azura.somlib.sparse_material.SparseMaterialState;
import azura.somlib.world.WorldSparseMatter;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.rmi.registry.Registry;

import static net.minecraft.server.command.CommandManager.literal;

public class StatesOfMatterCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(
                                literal("spawnmaterial")
                                        .requires(source -> source.hasPermissionLevel(3))
                                        .then(
                                                CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                                        .then(
                                                                CommandManager.argument("concentration", FloatArgumentType.floatArg())
                                                                        .then(
                                                                                CommandManager.argument("material", MaterialArgumentType.material())
                                                                                .executes(context -> {
                                                                                    BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");
                                                                                    SparseMaterial material = MaterialArgumentType.getMaterial(context, "material");
                                                                                    float concentration = FloatArgumentType.getFloat(context, "concentration");
                                                                                    World world = context.getSource().getWorld();
                                                                                    WorldSparseMatter.addMaterial(world, pos, new SparseMaterialState(material, concentration));
                                                                                    context.getSource().sendMessage(Text.literal("Successfully added " + SparseMaterialRegistry.MATERIAL_TYPES.getId(material).toString() + " at " + pos.toString() + "!"));
                                                                                    return 1;
                                                                                })
                                                                        )
                                                        )
                                        )
                        )
        );

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(
                                literal("checkmaterial")
                                        .requires(source -> source.hasPermissionLevel(3))
                                        .then(
                                                CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                                .executes(context -> {
                                                    BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");
                                                    World world = context.getSource().getWorld();
                                                    String feedback = "Found ";
                                                    boolean first = true;
                                                    for (SparseMaterialState state : WorldSparseMatter.getMaterials(world, pos)){
                                                        if (!first){
                                                            feedback += ", ";
                                                        }
                                                        feedback += SparseMaterialRegistry.MATERIAL_TYPES.getId(state.getType());

                                                        first = false;

                                                    }
                                                    feedback += "!";

                                                    context.getSource().sendMessage(Text.literal(feedback));
                                                    return 1;
                                                })
                                        )
                        )
        );

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(
                                literal("clearmaterial")
                                        .requires(source -> source.hasPermissionLevel(3))
                                        .then(
                                                CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                                        .executes(context -> {
                                                            BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");
                                                            World world = context.getSource().getWorld();
                                                            String feedback = "Cleared! ";
                                                            WorldSparseMatter.clear(world, pos);

                                                            context.getSource().sendMessage(Text.literal(feedback));
                                                            return 1;
                                                        })
                                        )
                        )
        );
    }
}
