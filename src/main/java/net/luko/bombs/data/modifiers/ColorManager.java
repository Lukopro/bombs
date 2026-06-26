package net.luko.bombs.data.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.luko.bombs.Bombs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ColorManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "modifier/colors";

    public static final ColorManager INSTANCE = new ColorManager();
    private final Map<String, TextColor> colors = new HashMap<>();

    public ColorManager(){
        super(GSON, DIRECTORY);
    }

    public Map<String, TextColor> getColors() {
        return Collections.unmodifiableMap(colors);
    }

    public void set(Map<String, TextColor> data) {
        this.colors.clear();
        this.colors.putAll(data);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        colors.clear();

        var jsonEntries = jsonMap.entrySet();
        if (jsonEntries.isEmpty()) {
            Bombs.LOGGER.warn("ColorManager did not find any data in {}", DIRECTORY);
        }

        for(var entry : jsonEntries) {
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "modifier_colors");

                for(Map.Entry<String, JsonElement> colorEntry : json.entrySet()){
                    String modifier = colorEntry.getKey();
                    String hex = colorEntry.getValue().getAsString();

                    try{
                        int rbg = Integer.decode(hex);
                        colors.put(modifier, TextColor.fromRgb(rbg));
                    } catch (NumberFormatException e){
                        Bombs.LOGGER.error("ColorManager found invalid color value in colors.json for '{}': {}", modifier, hex);
                    }
                }
            } catch (Exception e){
                Bombs.LOGGER.error("ColorManager failed to parse {} in {}: {}",
                        entry.getKey(), DIRECTORY, e.getMessage());
            }
        }
    }

    public TextColor getColor(String modifier){
        return colors.getOrDefault(modifier, TextColor.fromRgb(0x3d372e));
    }

    public static class Serializer {
        public static void encode(Map<String, TextColor> data, FriendlyByteBuf buf) {
            buf.writeVarInt(data.size());
            for (var entry : data.entrySet()) {
                buf.writeUtf(entry.getKey());
                buf.writeInt(entry.getValue().getValue());
            }
        }

        public static Map<String, TextColor> decode(FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            Map<String, TextColor> data = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                data.put(buf.readUtf(), TextColor.fromRgb(buf.readInt()));
            }
            return data;
        }
    }
}
