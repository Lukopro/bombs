package net.luko.bombs.item;

import net.luko.bombs.Bombs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Bombs.MODID);

    public static final RegistryObject<CreativeModeTab> BOMBS_TAB = CREATIVE_MODE_TABS.register("bombs_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.BOMB.get()))
                    .title(Component.translatable("creativetab.bombs_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        /* Add items to Bombs creative mode tab */

                        pOutput.accept(ModItems.BOMB.get());

                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
