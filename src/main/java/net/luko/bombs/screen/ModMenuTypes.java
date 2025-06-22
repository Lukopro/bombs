package net.luko.bombs.screen;

import net.luko.bombs.Bombs;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Bombs.MODID);

    public static final RegistryObject<MenuType<DemolitionTableMenu>> DEMOLITION_TABLE_MENU =
            MENUS.register("demolition_table_menu", () -> IForgeMenuType.create(DemolitionTableMenu::new));

    public static void register(IEventBus eventBus){
        MENUS.register(eventBus);
    }
}
