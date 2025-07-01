package net.luko.bombs.util;

import net.luko.bombs.data.ModDataComponents;
import net.minecraft.world.item.ItemStack;

public class BombTextureUtil {
    public static float getTextureIndex(ItemStack stack){
        float index = 1.0F;
        if(stack.has(ModDataComponents.TIER.get())){
            index += (float) stack.getOrDefault(ModDataComponents.TIER.get(), 1);
            index--;
        }
        return index;
    }
}
