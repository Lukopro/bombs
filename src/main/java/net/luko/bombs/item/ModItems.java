package net.luko.bombs.item;

import net.luko.bombs.Bombs;
import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.item.bomb.BombItem;
import net.luko.bombs.item.bomb.DynamiteItem;
import net.luko.bombs.item.bomb.GrenadeItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Bombs.MODID);

    public static final RegistryObject<Item> DYNAMITE = ITEMS.register("dynamite",
            () -> new DynamiteItem(new Item.Properties()));

    public static final RegistryObject<Item> GRENADE = ITEMS.register("grenade",
            () -> new GrenadeItem(new Item.Properties()));

    public static final RegistryObject<Item> PROSPECTOR_SPAWN_EGG = ITEMS.register("prospector_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.PROSPECTOR,
                    0x252525,
                    0x959B9B,
                    new Item.Properties()
            ));

    public static final RegistryObject<Item> HONSE_SPAWN_EGG = ITEMS.register("honse_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.HONSE,
                    0x8B4513,
                    0xEEE8AA,
                    new Item.Properties()
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
