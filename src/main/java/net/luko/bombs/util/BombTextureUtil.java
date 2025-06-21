package net.luko.bombs.util;

import net.luko.bombs.data.ModDataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BombTextureUtil {
    public static float getTextureIndex(ItemStack stack){
        float index = 1.0F;
        if(stack.has(ModDataComponents.TIER.get())){
            index += (float) stack.getOrDefault(ModDataComponents.TIER.get(), 1);
            index--;
        }
        if(stack.has(ModDataComponents.MODIFIERS.get()) && !stack.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of()).isEmpty()){
            index += 5.0F;
        }
        return index;
    }
}
