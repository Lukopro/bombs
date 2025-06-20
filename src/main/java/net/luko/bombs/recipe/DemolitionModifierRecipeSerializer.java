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

public class DemolitionModifierRecipeSerializer implements RecipeSerializer<DemolitionModifierRecipe> {

    public static final MapCodec<DemolitionModifierRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("input_bomb").forGetter(DemolitionModifierRecipe::getInputBomb),
                    Ingredient.CODEC.fieldOf("input_modifier").forGetter(DemolitionModifierRecipe::getInputModifier),
                    Codec.STRING.fieldOf("modifier").forGetter(DemolitionModifierRecipe::getModifierName)
            ).apply(instance, DemolitionModifierRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DemolitionModifierRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, DemolitionModifierRecipe::getInputBomb,
                    Ingredient.CONTENTS_STREAM_CODEC, DemolitionModifierRecipe::getInputModifier,
                    ByteBufCodecs.STRING_UTF8, DemolitionModifierRecipe::getModifierName,
                    DemolitionModifierRecipe::new
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
