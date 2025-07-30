package net.luko.bombs.data.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.luko.bombs.Bombs;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipe;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.*;

public class ModifierManager{
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "modifier/modifiers";

    public static final ModifierManager INSTANCE = new ModifierManager();

    private final Map<String, Modifier> modifiers = new HashMap<>();

    public ModifierManager() {}

    public static Modifier parseModifierFromJson(JsonObject json){
        Ingredient bomb = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_bomb"));
        Ingredient modifierItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input_modifier"));
        String specialTag = json.has("special_tag") ? GsonHelper.getAsString(json, "special_tag") : null;

        JsonArray array = json.getAsJsonArray("incompatible_with");
        Set<String> incompatibleWith = new HashSet<>();
        for (JsonElement element : array) incompatibleWith.add(element.getAsString());
        int color = Integer.decode(GsonHelper.getAsString(json, "color"));

        return new Modifier(bomb, modifierItem, specialTag, incompatibleWith, color);
    }

    public void apply(ResourceManager resourceManager) {
        modifiers.clear();

        Map<ResourceLocation, JsonElement> jsonElements = new HashMap<>();

        try{
            SimpleJsonResourceReloadListener.scanDirectory(resourceManager, DIRECTORY, GSON, jsonElements);
        } catch(Exception e){
            Bombs.LOGGER.error("Failed to scan {} directory: {}", DIRECTORY, e);
        }

        for(var entry : jsonElements.entrySet()){
            JsonObject json = entry.getValue().getAsJsonObject();

            String id = entry.getKey().getPath();

            try {
                Modifier modifier = parseModifierFromJson(json);

                modifiers.put(id, modifier);
            } catch (Exception e) {
                Bombs.LOGGER.error("Failed to load modifier '{}', {}", id, e);
            }
        }

        Bombs.LOGGER.info("Loaded {} modifiers.", modifiers.size());
    }

    public Map<ResourceLocation, Recipe<?>> getRecipes(){
        Map<ResourceLocation, Recipe<?>> recipes = new HashMap<>();

        for(var entry : modifiers.entrySet()){
            String id = entry.getKey();
            Modifier modifier = entry.getValue();

            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                    Bombs.MODID, "demolition_modifier/" + id);

            Recipe<?> recipe = new DemolitionModifierRecipe(
                    recipeId, modifier.bomb(), modifier.modifierItem(), id, modifier.specialTag());

            recipes.put(recipeId, recipe);
        }

        return recipes;
    }

    public boolean isCompatible(String mod1, String mod2){
        Modifier modEntry1 = modifiers.get(mod1);
        Modifier modEntry2 = modifiers.get(mod2);

        if(modEntry1 == null || modEntry2 == null) return false;

        Set<String> mod1Incompatible = modEntry1.incompatibleWith();
        Set<String> mod2Incompatible = modEntry2.incompatibleWith();

        return !mod1Incompatible.contains(mod2) && !mod2Incompatible.contains(mod1);
    }

    public TextColor getColor(String mod){
        Modifier modifier = modifiers.get(mod);
        return modifier == null ? TextColor.fromRgb(0x3d372e) : TextColor.fromRgb(modifier.color());
    }
}
