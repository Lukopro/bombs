package net.luko.bombs.data.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

public class ModifierIncompatibilityManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "modifiers/incompatible";

    public static final ModifierIncompatibilityManager INSTANCE = new ModifierIncompatibilityManager();

    private final Map<String, Set<String>> incompatibilities = new HashMap<>();

    public ModifierIncompatibilityManager() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        incompatibilities.clear();

        for (var entry : jsonMap.entrySet()) {
            try {
                JsonObject json = entry.getValue().getAsJsonObject();
                JsonArray array = json.getAsJsonArray("incompatible_with");

                String modifier = entry.getKey().getPath();
                Set<String> incompatibleModifiers = new HashSet<>();

                for (JsonElement element : array) {
                    incompatibleModifiers.add(element.getAsString());
                }

                incompatibilities.put(modifier, Set.copyOf(incompatibleModifiers));

            } catch (Exception e) {
                System.err.println("[ModifierIncompatibilityManager] Failed to parse " + entry.getKey().getPath() + ".json: " + e.getMessage());
            }
        }
    }

    public boolean isCompatible(String mod1, String mod2){
        Set<String> mod1Incompatible = incompatibilities.getOrDefault(mod1, Set.of());
        Set<String> mod2Incompatible = incompatibilities.getOrDefault(mod2, Set.of());

        return !mod1Incompatible.contains(mod2) && !mod2Incompatible.contains(mod1);
    }

}