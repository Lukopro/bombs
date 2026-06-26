package net.luko.bombs.data.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.luko.bombs.Bombs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PriorityManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "modifier/priorities";

    public static final PriorityManager INSTANCE = new PriorityManager();
    private final Map<String, Integer> priorities = new HashMap<>();

    public PriorityManager(){
        super(GSON, DIRECTORY);
    }

    public Map<String, Integer> getPriorities(){
        return Collections.unmodifiableMap(priorities);
    }

    public void set(Map<String, Integer> data) {
        this.priorities.clear();
        this.priorities.putAll(data);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        priorities.clear();

        var jsonEntries = jsonMap.entrySet();
        if (jsonEntries.isEmpty()) {
            Bombs.LOGGER.warn("PriorityManager did not find any data in {}", DIRECTORY);
        }

        for(var entry : jsonEntries) {
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "modifier_priorities");

                for (Map.Entry<String, JsonElement> priorityEntry : json.entrySet()) {
                    String modifier = priorityEntry.getKey();
                    int priorityValue = priorityEntry.getValue().getAsInt();
                    priorities.put(modifier, priorityValue);
                }
            } catch (Exception e) {
                Bombs.LOGGER.error("PriorityManager failed to parse {} in {}: {}",
                        entry.getKey(), DIRECTORY, e.getMessage());
            }
        }
    }

    public int getPriority(String modifier){
        return priorities.getOrDefault(modifier, Integer.MAX_VALUE);
    }

    public static class Serializer {
        public static void encode(Map<String, Integer> data, FriendlyByteBuf buf) {
            buf.writeVarInt(data.size());
            for (var entry : data.entrySet()) {
                buf.writeUtf(entry.getKey());
                buf.writeVarInt(entry.getValue());
            }
        }

        public static Map<String, Integer> decode(FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            Map<String, Integer> data = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                data.put(buf.readUtf(), buf.readVarInt());
            }
            return data;
        }
    }
}
