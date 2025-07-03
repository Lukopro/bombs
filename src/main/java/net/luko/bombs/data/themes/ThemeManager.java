package net.luko.bombs.data.themes;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ThemeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "themes";

    public static final ThemeManager INSTANCE = new ThemeManager();

    private static final Map<ResourceLocation, ThemeData> THEMES = new HashMap<>();

    public ThemeManager(){
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        THEMES.clear();
        for(Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()){
            ResourceLocation id = entry.getKey();
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "theme");
                ThemeData themeData = ThemeData.fromJson(json);
                THEMES.put(id, themeData);
            } catch (JsonParseException | IllegalArgumentException e) {
                System.err.println("[ThemeManager] Failed to load theme " + id + ": " + e.getMessage());
            }
        }
    }

    public static ThemeData get(ResourceLocation id){
        return THEMES.get(id);
    }

    public static Collection<ResourceLocation> getAvailableThemes(){
        return THEMES.keySet();
    }

    public static boolean hasTheme(ResourceLocation id){
        return THEMES.containsKey(id);
    }

    public static void printThemes(){
        for (ResourceLocation s : THEMES.keySet()){
            System.out.println(s);
        }
    }
}
