package net.luko.bombs.entity.villager;

import com.google.common.collect.ImmutableSet;
import net.luko.bombs.Bombs;
import net.luko.bombs.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPoiTypes {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, Bombs.MODID);

    public static final RegistryObject<PoiType> DEMOLITION_TABLE_POI =
            POI_TYPES.register("demolition_table_poi", () ->
                    new PoiType(
                            ImmutableSet.copyOf(
                                    ModBlocks.DEMOLITION_TABLE.get().getStateDefinition().getPossibleStates()
                            ), 1, 1
                    )
            );

    public static void register(IEventBus bus) {
        POI_TYPES.register(bus);
    }
}
