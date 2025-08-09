package net.luko.bombs.data.modifiers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.luko.bombs.Bombs;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipe;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.antlr.v4.runtime.misc.Pair;

import java.util.*;

public class ModifierManager{
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "modifier/modifiers";

    public static final ModifierManager INSTANCE = new ModifierManager();

    private final Map<String, Modifier> modifiers = new HashMap<>();

    public ModifierManager() {}

    public static Modifier parseModifierFromJson(JsonObject json){
        Ingredient bomb = Ingredient.CODEC
                .parse(JsonOps.INSTANCE, json.get("input_bomb"))
                .result()
                .orElseThrow(() -> new JsonParseException("Failed to parse input_bomb"));
        Ingredient modifierItem = Ingredient.CODEC
                .parse(JsonOps.INSTANCE, json.get("input_modifier"))
                .result()
                .orElseThrow(() -> new JsonParseException("Failed to parse input_modifier"));String specialTag = json.has("special_tag") ? GsonHelper.getAsString(json, "special_tag") : null;
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

    public Ingredient getModifierItem(String mod){
        Modifier modifier = modifiers.get(mod);
        assert modifier != null;
        return modifier.modifierItem();
    }

    public Pair<Multimap<RecipeType<?>, RecipeHolder<?>>, Map<ResourceLocation, RecipeHolder<?>>> generateRecipes(){
        Multimap<RecipeType<?>, RecipeHolder<?>> byType = ArrayListMultimap.create();
        Map<ResourceLocation, RecipeHolder<?>> byName = new HashMap<>();

        for(var entry : modifiers.entrySet()){
            String id = entry.getKey();
            Modifier modifier = entry.getValue();

            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                    Bombs.MODID, "demolition_modifier/" + id);

            Recipe<?> recipe = new DemolitionModifierRecipe(
                    modifier.bomb(), modifier.modifierItem(), id, modifier.specialTag());

            RecipeHolder<Recipe<?>> holder = new RecipeHolder<>(recipeId, recipe);

            byType.put(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get(), holder);
            byName.put(recipeId, holder);
        }

        return new Pair<>(byType, byName);
    }

    public boolean isCompatible(String mod1, String mod2){
        Modifier modEntry1 = modifiers.get(mod1);
        Modifier modEntry2 = modifiers.get(mod2);

        if(modEntry1 == null || modEntry2 == null) return false;

        Set<String> mod1Incompatible = modEntry1.incompatibleWith();
        Set<String> mod2Incompatible = modEntry2.incompatibleWith();

        return !mod1Incompatible.contains(mod2) && !mod2Incompatible.contains(mod1);
    }

    public Map<String, Modifier> getAllModifiers(){
        return Collections.unmodifiableMap(modifiers);
    }

    public TextColor getColor(String mod){
        Modifier modifier = modifiers.get(mod);
        return modifier == null ? TextColor.fromRgb(0x3d372e) : TextColor.fromRgb(modifier.color());
    }
}
