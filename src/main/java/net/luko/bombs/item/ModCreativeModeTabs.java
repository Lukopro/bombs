package net.luko.bombs.item;

import net.luko.bombs.Bombs;
import net.luko.bombs.block.ModBlocks;
import net.luko.bombs.components.ModDataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Bombs.MODID);

    public static final Supplier<CreativeModeTab> BOMBS_TAB = CREATIVE_MODE_TABS.register("bombs_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.DYNAMITE.get()))
                    .title(Component.translatable("creativetab.bombs_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        /* Add items to Bombs creative mode tab */

                        pOutput.accept(ModItems.DYNAMITE.get());
                        for(int i = 2; i <= 6; i++){
                            ItemStack tempStack = new ItemStack(ModItems.DYNAMITE.get());
                            tempStack.set(ModDataComponents.TIER.get(), i);
                            pOutput.accept(tempStack);
                        }

                        pOutput.accept(ModBlocks.DEMOLITION_TABLE.get());

                        pOutput.accept(ModItems.PROSPECTOR_SPAWN_EGG.get());
                        pOutput.accept(ModItems.HONSE_SPAWN_EGG.get());

                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
