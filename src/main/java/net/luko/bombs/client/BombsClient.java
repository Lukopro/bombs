package net.luko.bombs.client;

import net.luko.bombs.Bombs;
import net.luko.bombs.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class BombsClient {
    public static void init(){
        registerItemProperties();
    }

    private static void registerItemProperties(){


        ItemProperties.register(ModItems.DYNAMITE.get(),
                ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "index"),
                (pStack, pLevel, pEntity, pSeed) -> {
                    float index = 1.0F;
                    if(pStack.hasTag() && pStack.getTag().contains("Tier", Tag.TAG_INT)){
                        index += (float) pStack.getTag().getInt("Tier");
                        index--;
                    }
                    if(pStack.hasTag() && pStack.getTag().contains("Modifiers")){
                        index += 5.0F;
                    }
                    return index;
                });
    }
}
