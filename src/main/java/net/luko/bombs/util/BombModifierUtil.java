package net.luko.bombs.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class BombModifierUtil {
    public static boolean hasModifier(ItemStack stack, String modifier){
        if(!stack.hasTag()) return false;
        ListTag tag = stack.getTag().getList("Modifiers", CompoundTag.TAG_STRING);
        return hasModifier(tag, modifier);
    }
    public static boolean hasModifier(ListTag tag, String modifier){
        for(int i = 0; i < tag.size(); i++){
            if(tag.getString(i).equals(modifier)) return true;
        }
        return false;
    }
}
