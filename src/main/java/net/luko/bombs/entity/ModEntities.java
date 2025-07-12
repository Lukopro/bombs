package net.luko.bombs.entity;

import net.luko.bombs.Bombs;
import net.luko.bombs.entity.bomb.ThrownBombEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Bombs.MODID);

    // THROWN_BOMB encapsulates all tiers of dynamite.
    public static final Supplier<EntityType<ThrownBombEntity>> THROWN_BOMB = ENTITIES.register(
            "thrown_bomb",
            () -> EntityType.Builder.<ThrownBombEntity>of(ThrownBombEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(32)
                    .updateInterval(10)
                    .build("thrown_bomb"));

    public static final Supplier<EntityType<ProspectorEntity>> PROSPECTOR = ENTITIES.register(
            "prospector",
            () -> EntityType.Builder.<ProspectorEntity>of(ProspectorEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("prospector")
    );

    public static final Supplier<EntityType<HonseEntity>> HONSE = ENTITIES.register(
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
