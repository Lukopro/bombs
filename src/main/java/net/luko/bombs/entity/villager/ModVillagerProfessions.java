package net.luko.bombs.entity.villager;

import com.google.common.collect.ImmutableSet;
import net.luko.bombs.Bombs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModVillagerProfessions {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, Bombs.MODID);

    public static final Supplier<VillagerProfession> DEMOLITIONIST =
            PROFESSIONS.register("demolitionist", () ->
                    new VillagerProfession(
                            "demolitionist",
                            holder -> holder.value() == ModPoiTypes.DEMOLITION_TABLE_POI.get(),
                            holder -> holder.value() == ModPoiTypes.DEMOLITION_TABLE_POI.get(),
                            ImmutableSet.of(),
                            ImmutableSet.of(),
                            SoundEvents.VILLAGER_WORK_TOOLSMITH
                    )
            );

    public static void register(IEventBus bus) {
        PROFESSIONS.register(bus);
    }
}
