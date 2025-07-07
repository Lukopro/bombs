package net.luko.bombs.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

public class BombRecipeSerializer implements RecipeSerializer<BombRecipe> {
    @Override
    public BombRecipe fromJson(ResourceLocation id, JsonObject json) {
        ShapelessRecipe vanilla = RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json);
        return new BombRecipe(vanilla.getId(), vanilla.getGroup(), vanilla.category(),
                vanilla.getResultItem(RegistryAccess.EMPTY), vanilla.getIngredients());
    }

    @Override
    public @Nullable BombRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        ShapelessRecipe vanilla = RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buf);
        if(vanilla == null){
            throw new JsonParseException("Failed to parse vanilla shapeless recipe for custom dynamite recipe: " + id);
        }
        return new BombRecipe(vanilla.getId(), vanilla.getGroup(), vanilla.category(),
                vanilla.getResultItem(RegistryAccess.EMPTY), vanilla.getIngredients());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, BombRecipe recipe) {
        RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buf, recipe);
    }
}
