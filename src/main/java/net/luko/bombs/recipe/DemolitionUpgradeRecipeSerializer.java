package net.luko.bombs.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class DemolitionUpgradeRecipeSerializer implements RecipeSerializer<DemolitionUpgradeRecipe> {
    @Override
    public MapCodec<DemolitionUpgradeRecipe> codec() {
        System.out.println("Upgrade recipe serializer CODEC retrieved!");
        return DemolitionUpgradeRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DemolitionUpgradeRecipe> streamCodec() {
        return DemolitionUpgradeRecipe.STREAM_CODEC;
    }
}
