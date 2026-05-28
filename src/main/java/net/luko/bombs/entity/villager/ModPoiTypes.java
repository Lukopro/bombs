package net.luko.bombs.entity.villager;

import com.google.common.collect.ImmutableSet;
import net.luko.bombs.Bombs;
import net.luko.bombs.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModPoiTypes {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Bombs.MODID);

    public static final Supplier<PoiType> DEMOLITION_TABLE_POI =
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
