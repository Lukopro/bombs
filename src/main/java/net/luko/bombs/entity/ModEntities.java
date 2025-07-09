package net.luko.bombs.entity;

import net.luko.bombs.Bombs;
import net.luko.bombs.entity.bomb.ThrownBombEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Bombs.MODID);

    // THROWN_BOMB encapsulates all tiers of dynamite.
    public static final RegistryObject<EntityType<ThrownBombEntity>> THROWN_BOMB = ENTITIES.register(
            "thrown_bomb",
            () -> EntityType.Builder.<ThrownBombEntity>of(ThrownBombEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(32)
                    .updateInterval(10)
                    .build("thrown_bomb"));

    public static final RegistryObject<EntityType<ProspectorEntity>> PROSPECTOR = ENTITIES.register(
            "prospector",
            () -> EntityType.Builder.<ProspectorEntity>of(ProspectorEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("prospector")
    );

    public static final RegistryObject<EntityType<HonseEntity>> HONSE = ENTITIES.register(
            "honse",
            () -> EntityType.Builder.<HonseEntity>of(HonseEntity::new, MobCategory.CREATURE)
                    .sized(1.3965F, 1.6F)
                    .clientTrackingRange(10)
                    .build("honse")
    );

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
