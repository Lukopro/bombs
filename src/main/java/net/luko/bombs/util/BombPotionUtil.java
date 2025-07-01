package net.luko.bombs.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BombPotionUtil {
    public static String getDescriptionId(ItemStack stack){
        ItemStack tempPotionStack = new ItemStack(Items.POTION);
        tempPotionStack.set(DataComponents.POTION_CONTENTS, stack.get(DataComponents.POTION_CONTENTS));
        return Items.POTION.getDescriptionId(tempPotionStack);
    }
}
