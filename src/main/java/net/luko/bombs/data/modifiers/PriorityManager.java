package net.luko.bombs.data.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.luko.bombs.Bombs;
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
    private static final ResourceLocation FILE_NAME = ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "priorities");

    public static final PriorityManager INSTANCE = new PriorityManager();
    private final Map<String, Integer> priorities = new HashMap<>();

    public PriorityManager(){
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        priorities.clear();

        JsonElement element = jsonMap.get(FILE_NAME);
        if(element == null){
            Bombs.LOGGER.error("PriorityManager couldn't find priorities.json at " + FILE_NAME);
            return;
        }

        try {
            JsonObject json = GsonHelper.convertToJsonObject(element, "modifier_priorities");

            for(Map.Entry<String, JsonElement> priorityEntry : json.entrySet()){
                String modifier = priorityEntry.getKey();
                int priorityValue = priorityEntry.getValue().getAsInt();
                priorities.put(modifier, priorityValue);
            }
        } catch (Exception e){
            Bombs.LOGGER.error("PriorityManager failed to parse priorities.json: " + e.getMessage());
        }
    }

    public int getPriority(String modifier){
        return priorities.getOrDefault(modifier, Integer.MAX_VALUE);
    }

    public Map<String, Integer> getPriorities(){
        return Collections.unmodifiableMap(priorities);
    }
}
