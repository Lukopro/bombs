package net.luko.bombs.entity.villager;

import com.google.common.collect.ImmutableSet;
import net.luko.bombs.Bombs;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModVillagerProfessions {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, Bombs.MODID);

    public static final RegistryObject<VillagerProfession> DEMOLITIONIST =
            PROFESSIONS.register("demolitionist", () ->
                    new VillagerProfession(
                            "demolitionist",
                            holder -> holder.get() == ModPoiTypes.DEMOLITION_TABLE_POI.get(),
                            holder -> holder.get() == ModPoiTypes.DEMOLITION_TABLE_POI.get(),
                            ImmutableSet.of(),
                            ImmutableSet.of(),
                            SoundEvents.VILLAGER_WORK_TOOLSMITH
                    )
            );

    public static void register(IEventBus bus) {
        PROFESSIONS.register(bus);
    }
}
