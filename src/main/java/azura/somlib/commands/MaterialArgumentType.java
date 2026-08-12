package azura.somlib.commands;

import azura.somlib.sparse_material.SparseMaterial;
import azura.somlib.sparse_material.SparseMaterialRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class MaterialArgumentType implements ArgumentType<SparseMaterial> {

	private static final SimpleCommandExceptionType INVALID_MATERIAL =
			new SimpleCommandExceptionType(
					Text.literal("Unknown sparse material")
			);

	public static MaterialArgumentType material() {
		return new MaterialArgumentType();
	}

	@Override
	public SparseMaterial parse(StringReader reader) throws CommandSyntaxException {
		Identifier id = Identifier.fromCommandInput(reader);

		SparseMaterial material =
				SparseMaterialRegistry.MATERIAL_TYPES.get(id);

		if (material == null) {
			throw INVALID_MATERIAL.createWithContext(reader);
		}

		return material;
	}

	public static <S> SparseMaterial getMaterial(
			CommandContext<S> context,
			String name
	) {
		return context.getArgument(name, SparseMaterial.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(
			CommandContext<S> context,
			SuggestionsBuilder builder
	) {
		for (Identifier id : SparseMaterialRegistry.MATERIAL_TYPES.getIds()) {
			builder.suggest(id.toString());
		}

		return builder.buildFuture();
	}
}