package net.luko.bombs.client;

import net.luko.bombs.Bombs;
import net.luko.bombs.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class BombsClient {
    public static void init(){
        registerItemProperties();
    }

    private static void registerItemProperties(){
        ItemProperties.register(ModItems.DYNAMITE.get(),
                new ResourceLocation(Bombs.MODID, "modifier"),
                (pStack, pLevel, pEntity, pSeed) -> {
                    if(pStack.hasTag() && pStack.getTag().contains("Modifiers")){
                        return 1.0F;
                    }
                    return 0.0F;
                });
    }

}
