package net.luko.bombs.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class DemolitionModifierRecipeSerializer implements RecipeSerializer<DemolitionModifierRecipe> {

    public static final MapCodec<DemolitionModifierRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("input_bomb").forGetter(DemolitionModifierRecipe::inputBomb),
                    Ingredient.CODEC.fieldOf("input_modifier").forGetter(DemolitionModifierRecipe::inputModifier),
                    Codec.STRING.fieldOf("modifier").forGetter(DemolitionModifierRecipe::modifierName),
                    Codec.STRING.optionalFieldOf("special_tag").forGetter(r -> Optional.ofNullable(r.specialTag()))
            ).apply(instance, (bomb, modifier, modName, optionalTag) ->
        new DemolitionModifierRecipe(bomb, modifier, modName, optionalTag.orElse(null)))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DemolitionModifierRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, DemolitionModifierRecipe::inputBomb,
                    Ingredient.CONTENTS_STREAM_CODEC, DemolitionModifierRecipe::inputModifier,
                    ByteBufCodecs.STRING_UTF8, DemolitionModifierRecipe::modifierName,
                    ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), r -> Optional.ofNullable(r.specialTag()),
                    (bomb, mod, name, special) -> new DemolitionModifierRecipe(bomb, mod, name, special.orElse(null))
            );

    @Override
    public MapCodec<DemolitionModifierRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DemolitionModifierRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
