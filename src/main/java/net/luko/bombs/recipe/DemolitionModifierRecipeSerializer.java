package net.luko.bombs.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class DemolitionModifierRecipeSerializer implements RecipeSerializer<DemolitionModifierRecipe> {
    @Override
    public DemolitionModifierRecipe fromJson(ResourceLocation id, JsonObject json){
        Ingredient inputBomb = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_bomb"));
        Ingredient inputModifier = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_modifier"));
        String modifierName = GsonHelper.getAsString(json, "modifier");

        return new DemolitionModifierRecipe(id, inputBomb, inputModifier, modifierName);
    }

    @Override
    public @Nullable DemolitionModifierRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf){
        Ingredient inputBomb = Ingredient.fromNetwork(buf);
        Ingredient inputModifier = Ingredient.fromNetwork(buf);
        String modifierName = buf.readUtf();

        return new DemolitionModifierRecipe(id, inputBomb, inputModifier, modifierName);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, DemolitionModifierRecipe recipe){
        recipe.getInputBomb().toNetwork(buf);
        recipe.getInputModifier().toNetwork(buf);
        buf.writeUtf(recipe.getModifierName());
    }
}
