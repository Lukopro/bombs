package net.luko.bombs.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class BombRecipeSerializer implements RecipeSerializer<BombRecipe> {
    public static final MapCodec<BombRecipe> CODEC = RecipeSerializer.SHAPELESS_RECIPE.codec().flatXmap(
            vanilla -> {
                if(vanilla instanceof CraftingRecipe crafting){
                    return DataResult.success(new BombRecipe(
                            vanilla.getGroup(),
                            crafting.category(),
                            vanilla.getResultItem(RegistryAccess.EMPTY),
                            vanilla.getIngredients()
                    ));
                } else {
                    return DataResult.error(() -> "Expected a CraftingRecipe but got: " + vanilla.getClass().getName());
                }
            },
            recipe -> DataResult.success(new ShapelessRecipe(
                    recipe.getGroup(),
                    recipe.category(),
                    recipe.getResultItem(RegistryAccess.EMPTY),
                    recipe.getIngredients())
            )
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BombRecipe> STREAM_CODEC =
            ShapelessRecipe.STREAM_CODEC.map(
                    vanilla -> new BombRecipe(
                            vanilla.getGroup(),
                            ((CraftingRecipe) vanilla).category(),
                            vanilla.getResultItem(RegistryAccess.EMPTY),
                            vanilla.getIngredients()
                    ),
                    recipe -> new ShapelessRecipe(
                            recipe.getGroup(),
                            recipe.category(),
                            recipe.getResultItem(RegistryAccess.EMPTY),
                            recipe.getIngredients()
                    )
            );

    @Override
    public MapCodec<BombRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BombRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}