package net.luko.bombs.recipe.demolition;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class DemolitionUpgradeRecipeSerializer implements RecipeSerializer<DemolitionUpgradeRecipe> {

    @Override
    public DemolitionUpgradeRecipe fromJson(ResourceLocation id, JsonObject json){
        Ingredient inputUpgrade = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_upgrade"));
        int minTier = GsonHelper.getAsInt(json, "min_tier");
        int maxTier = GsonHelper.getAsInt(json, "max_tier");
        return new DemolitionUpgradeRecipe(id, inputUpgrade, minTier, maxTier);
    }

    @Override
    public @Nullable DemolitionUpgradeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf){
        Ingredient inputUpgrade = Ingredient.fromNetwork(buf);
        int minTier = buf.readInt();
        int maxTier = buf.readInt();
        return new DemolitionUpgradeRecipe(id, inputUpgrade, minTier, maxTier);
    }

    public void toNetwork(FriendlyByteBuf buf, DemolitionUpgradeRecipe recipe){
        recipe.getInputUpgrade().toNetwork(buf);
        buf.writeInt(recipe.getMinTier());
        buf.writeInt(recipe.getMaxTier());
    }
}
