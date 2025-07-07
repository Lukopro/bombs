package net.luko.bombs.recipe.demolition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class DemolitionUpgradeRecipeSerializer implements RecipeSerializer<DemolitionUpgradeRecipe> {
    public static final MapCodec<DemolitionUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("input_upgrade").forGetter(DemolitionUpgradeRecipe::inputUpgrade),
                    Codec.INT.fieldOf("min_tier").forGetter(DemolitionUpgradeRecipe::minTier),
                    Codec.INT.fieldOf("max_tier").forGetter(DemolitionUpgradeRecipe::maxTier)
            ).apply(instance, DemolitionUpgradeRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DemolitionUpgradeRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, DemolitionUpgradeRecipe::inputUpgrade,
                    ByteBufCodecs.INT, DemolitionUpgradeRecipe::minTier,
                    ByteBufCodecs.INT, DemolitionUpgradeRecipe::maxTier,
                    DemolitionUpgradeRecipe::new
            );

    @Override
    public MapCodec<DemolitionUpgradeRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DemolitionUpgradeRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
