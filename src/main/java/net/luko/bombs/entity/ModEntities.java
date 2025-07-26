package net.luko.bombs.entity;

import net.luko.bombs.Bombs;
import net.luko.bombs.entity.bomb.ThrownBombEntity;
import net.luko.bombs.entity.bomb.ThrownDynamiteEntity;
import net.luko.bombs.entity.bomb.ThrownGrenadeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Bombs.MODID);

    public static final Supplier<EntityType<ThrownDynamiteEntity>> THROWN_DYNAMITE = ENTITIES.register(
            "thrown_dynamite",
            () -> EntityType.Builder.<ThrownDynamiteEntity>of(ThrownDynamiteEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(32)
                    .updateInterval(10)
                    .build("thrown_dynamite"));

    public static final Supplier<EntityType<ThrownGrenadeEntity>> THROWN_GRENADE = ENTITIES.register(
            "thrown_grenade",
            () -> EntityType.Builder.<ThrownGrenadeEntity>of(ThrownGrenadeEntity::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .clientTrackingRange(32)
                    .updateInterval(10)
                    .build("thrown_grenade"));

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
