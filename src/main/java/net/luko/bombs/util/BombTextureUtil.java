package net.luko.bombs.util;

import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BombTextureUtil {
    public static float getTextureIndex(ItemStack stack){
        float index = 1.0F;
        if(stack.hasTag() && stack.getTag().contains("Tier", Tag.TAG_INT)){
            index += (float) stack.getTag().getInt("Tier");
            index--;
        }
        if(stack.hasTag() && stack.getTag().contains("Modifiers")){
            index += 5.0F;
        }
        return index;
    }
}
