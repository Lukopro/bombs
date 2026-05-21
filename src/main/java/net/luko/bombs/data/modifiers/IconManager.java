package net.luko.bombs.data.modifiers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.luko.bombs.Bombs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class IconManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "modifier/icons";

    public static final IconManager INSTANCE = new IconManager();
    private final Map<String, ResourceLocation> icons = new HashMap<>();

    public IconManager(){
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        icons.clear();

        var jsonEntries = jsonMap.entrySet();
        if (jsonEntries.isEmpty()) {
            Bombs.LOGGER.warn("IconManager did not find any data in {}", DIRECTORY);
        }

        for(var entry : jsonEntries) {
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "modifier_icons");

                for(Map.Entry<String, JsonElement> colorEntry : json.entrySet()){
                    String modifier = colorEntry.getKey();
                    String icon = colorEntry.getValue().getAsString();
                    ResourceLocation iconRL = ResourceLocation.parse(icon);
                    icons.put(modifier, iconRL);
                    Bombs.LOGGER.info("Loaded modifier icon {}", modifier);
                }
            } catch (Exception e){
                Bombs.LOGGER.error("IconManager failed to parse {} in {}: {}",
                        entry.getKey(), DIRECTORY, e.getMessage());
            }
        }
    }

    public ItemStack getIconItemStack(String mod) {
        ResourceLocation id = icons.get(mod);

        if (id == null) {
            Bombs.LOGGER.warn("Could not find modifier {} in IconManager", mod);
            return ItemStack.EMPTY;
        }

        Item item = ForgeRegistries.ITEMS.getValue(id);

        if (item == null) {
            Bombs.LOGGER.warn("Could not find item {}", id);
            return ItemStack.EMPTY;
        }

        return new ItemStack(item);
    }
}
