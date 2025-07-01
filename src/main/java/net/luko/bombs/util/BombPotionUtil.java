package net.luko.bombs.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BombPotionUtil {
    public static String getDescriptionId(ItemStack stack){
        ItemStack tempPotionStack = new ItemStack(Items.POTION);
        if(stack.isEmpty()) return Items.POTION.getDescriptionId(tempPotionStack);
        CompoundTag tempPotionTag = new CompoundTag();
        if(stack.hasTag()){
            if(stack.getTag().contains("CustomPotionEffects")){
                tempPotionTag.put("CustomPotionEffects", stack.getTag().get("CustomPotionEffects"));
            } else {
                String potionId = stack.getTag().getString("Potion");
                if(potionId.isEmpty()) potionId = "minecraft:empty";
                tempPotionTag.putString("Potion", potionId);
            }
        }
        tempPotionStack.setTag(tempPotionTag);
        return Items.POTION.getDescriptionId(tempPotionStack);
    }
}
