package net.luko.bombs.screen;

import net.luko.bombs.Bombs;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Bombs.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<DemolitionTableMenu>> DEMOLITION_TABLE_MENU =
            MENUS.register("demolition_table_menu", () ->
                    IMenuTypeExtension.create(DemolitionTableMenu::new));

    public static void register(IEventBus eventBus){
        MENUS.register(eventBus);
    }
}
