package net.luko.bombs.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

public class DemolitionUpgradeRecipeSerializer implements RecipeSerializer<DemolitionUpgradeRecipe> {

    @Override
    public DemolitionUpgradeRecipe fromJson(ResourceLocation id, JsonObject json){
        Ingredient inputBomb = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_bomb"));
        Ingredient inputUpgrade = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_upgrade"));
        Ingredient inputCasing = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_casing"));
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        return new DemolitionUpgradeRecipe(id, inputBomb, inputUpgrade, inputCasing, result);
    }

    @Override
    public @Nullable DemolitionUpgradeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf){
        Ingredient inputBomb = Ingredient.fromNetwork(buf);
        Ingredient inputUpgrade = Ingredient.fromNetwork(buf);
        Ingredient inputCasing = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        return new DemolitionUpgradeRecipe(id, inputBomb, inputUpgrade, inputCasing, result);
    }

    public void toNetwork(FriendlyByteBuf buf, DemolitionUpgradeRecipe recipe){
        recipe.getInputBomb().toNetwork(buf);
        recipe.getInputUpgrade().toNetwork(buf);
        recipe.getInputCasing().toNetwork(buf);
        buf.writeItem(recipe.getResultItem());
    }
}
