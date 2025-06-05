package net.luko.bombs.item;

import net.luko.bombs.Bombs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Bombs.MODID);

    public static final RegistryObject<Item> DYNAMITE = ITEMS.register("dynamite",
            () -> new BombItem(new Item.Properties(), 1.5F));

    public static final RegistryObject<Item> STRONG_DYNAMITE = ITEMS.register("strong_dynamite",
            () -> new BombItem(new Item.Properties(), 2.5F));

    public static final RegistryObject<Item> BLAZE_DYNAMITE = ITEMS.register("blaze_dynamite",
            () -> new BombItem(new Item.Properties(), 4.0F));

    public static final RegistryObject<Item> DRAGON_DYNAMITE = ITEMS.register("dragon_dynamite",
            () -> new BombItem(new Item.Properties(), 5.5F));

    public static final RegistryObject<Item> CRYSTAL_DYNAMITE = ITEMS.register("crystal_dynamite",
            () -> new BombItem(new Item.Properties(), 7.0F));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
