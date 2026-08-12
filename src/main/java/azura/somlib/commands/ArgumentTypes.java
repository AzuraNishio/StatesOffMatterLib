package azura.somlib.commands;

import azura.somlib.SomLib;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class ArgumentTypes {
    public static void register(){
        ArgumentTypeRegistry.registerArgumentType(
                SomLib.id("material"),
                MaterialArgumentType.class,
                ConstantArgumentSerializer.of(MaterialArgumentType::material)
        );
    }
}
