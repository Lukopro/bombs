package net.luko.bombs.item;

import net.luko.bombs.Bombs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Bombs.MODID);

    public static final RegistryObject<Item> BASIC_DYNAMITE = ITEMS.register("basic_dynamite",
            () -> new BombItem(new Item.Properties(), 1.0F));

    public static final RegistryObject<Item> STRONG_DYNAMITE = ITEMS.register("strong_dynamite",
            () -> new BombItem(new Item.Properties(), 2.5F));

    public static final RegistryObject<Item> REDSTONE_DYNAMITE = ITEMS.register("redstone_dynamite",
            () -> new BombItem(new Item.Properties(), 4.0F));

    public static final RegistryObject<Item> CHORAL_DYNAMITE = ITEMS.register("choral_dynamite",
            () -> new BombItem(new Item.Properties(), 5.5F));

    public static final RegistryObject<Item> CRYSTAL_DYNAMITE = ITEMS.register("crystal_dynamite",
            () -> new BombItem(new Item.Properties(), 7.0F));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
