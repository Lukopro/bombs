package net.luko.bombs.recipe.demolition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DemolitionModifierRecipeSerializer implements RecipeSerializer<DemolitionModifierRecipe> {
    @Override
    public DemolitionModifierRecipe fromJson(ResourceLocation id, JsonObject json){
        Ingredient inputBomb = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_bomb"));
        Ingredient inputModifier = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_modifier"));
        String modifierName = GsonHelper.getAsString(json, "modifier");

        List<String> incompatibleWith = new ArrayList<>();

        JsonArray array = GsonHelper.getAsJsonArray(json, "incompatible_with");

        for (JsonElement element : array) {
            incompatibleWith.add(element.getAsString());
        }

        String specialTag = json.has("special_tag") ? GsonHelper.getAsString(json, "special_tag") : null;

        return new DemolitionModifierRecipe(id, inputBomb, inputModifier, modifierName, incompatibleWith, specialTag);
    }

    @Override
    public @Nullable DemolitionModifierRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf){
        Ingredient inputBomb = Ingredient.fromNetwork(buf);
        Ingredient inputModifier = Ingredient.fromNetwork(buf);
        String modifierName = buf.readUtf();
        List<String> incompatibleWith = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            incompatibleWith.add(buf.readUtf());
        }

        String specialTag = buf.readBoolean() ? buf.readUtf() : null;

        return new DemolitionModifierRecipe(id, inputBomb, inputModifier, modifierName, incompatibleWith, specialTag);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, DemolitionModifierRecipe recipe){
        recipe.getInputBomb().toNetwork(buf);
        recipe.getInputModifier().toNetwork(buf);
        buf.writeUtf(recipe.getModifierName());

        buf.writeInt(recipe.getIncompatibleWith().size());
        for (String mod : recipe.getIncompatibleWith()) {
            buf.writeUtf(mod);
        }

        if(recipe.getSpecialTag() != null){
            buf.writeBoolean(true);
            buf.writeUtf(recipe.getSpecialTag());
        } else {
            buf.writeBoolean(false);
        }
    }
}
