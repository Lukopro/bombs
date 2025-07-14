package net.luko.bombs.item;

import net.luko.bombs.Bombs;
import net.luko.bombs.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Bombs.MODID);

    public static final DeferredItem<Item> DYNAMITE = ITEMS.register("dynamite",
            () -> new BombItem(new Item.Properties()));

    public static final DeferredItem<Item> PROSPECTOR_SPAWN_EGG = ITEMS.register("prospector_spawn_egg",
            () -> new SpawnEggItem(
                    ModEntities.PROSPECTOR.get(),
                    0x252525,
                    0x959B9B,
                    new Item.Properties()
            ));

    public static final DeferredItem<Item> HONSE_SPAWN_EGG = ITEMS.register("honse_spawn_egg",
            () -> new SpawnEggItem(
                    ModEntities.HONSE.get(),
                    0x8B4513,
                    0xEEE8AA,
                    new Item.Properties()
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
