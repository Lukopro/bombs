package net.luko.bombs.client;

import net.luko.bombs.Bombs;
import net.luko.bombs.data.ModDataComponents;
import net.luko.bombs.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BombsClient {
    public static void init(){
        registerItemProperties();
    }

    private static void registerItemProperties(){
        ItemProperties.register(ModItems.DYNAMITE.get(),
                ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "index"),
                (pStack, pLevel, pEntity, pSeed) -> {
                    float index = 1.0F;
                    if(pStack.has(ModDataComponents.TIER.get())){
                        index += (float) pStack.getOrDefault(ModDataComponents.TIER.get(), 1);
                        index--;
                    }
                    if(pStack.has(ModDataComponents.MODIFIERS.get()) && !pStack.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of()).isEmpty()){
                        index += 5.0F;
                    }
                    return index;
                });
    }
}
