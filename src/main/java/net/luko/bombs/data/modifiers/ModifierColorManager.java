package net.luko.bombs.data.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.luko.bombs.Bombs;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class ModifierColorManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "modifiers";
    private static final ResourceLocation FILE_NAME = ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "colors");

    public static final ModifierColorManager INSTANCE = new ModifierColorManager();
    private final Map<String, TextColor> colors = new HashMap<>();

    public ModifierColorManager(){
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        colors.clear();

        JsonElement element = jsonMap.get(FILE_NAME);
        if(element == null){
            Bombs.LOGGER.error("ModifierColorManager couldn't find colors.json at " + FILE_NAME);
            return;
        }

        try {
            JsonObject json = GsonHelper.convertToJsonObject(element, "modifier_colors");

            for(Map.Entry<String, JsonElement> colorEntry : json.entrySet()){
                String modifier = colorEntry.getKey();
                String hex = colorEntry.getValue().getAsString();

                try{
                    int rbg = Integer.decode(hex);
                    colors.put(modifier, TextColor.fromRgb(rbg));
                } catch (NumberFormatException e){
                    Bombs.LOGGER.error("ModifierColorManager found invalid color value in colors.json for '{}': {}", modifier, hex);
                }
            }
        } catch (Exception e){
            Bombs.LOGGER.error("ModifierColorManager failed to parse colors.json: " + e.getMessage());
        }
    }

    public TextColor getColor(String modifier){
        return colors.getOrDefault(modifier, TextColor.fromRgb(0x3d372e));
    }
}
