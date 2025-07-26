package net.luko.bombs.client;

import net.luko.bombs.Bombs;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.util.BombTextureUtil;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class BombsClient {
    public static void init(){
        registerItemProperties();
    }

    private static void registerItemProperties(){
        ItemProperties.register(ModItems.DYNAMITE.get(),
                ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "index"),
                (pStack, pLevel, pEntity, pSeed) -> BombTextureUtil.getTextureIndex(pStack));

        ItemProperties.register(ModItems.GRENADE.get(),
                ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "index"),
                (pStack, pLevel, pEntity, pSeed) -> BombTextureUtil.getTextureIndex(pStack));
    }
}
