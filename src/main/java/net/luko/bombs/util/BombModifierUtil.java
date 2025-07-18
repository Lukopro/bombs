package net.luko.bombs.util;

import net.luko.bombs.components.ModDataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BombModifierUtil {
    public static boolean hasModifier(ItemStack stack, String modifier){
        List<String> modifiers = stack.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of());
        return hasModifier(modifiers, modifier);
    }
    public static boolean hasModifier(List<String> modifiers, String modifier){
        for(int i = 0; i < modifiers.size(); i++){
            if(modifiers.get(i).equals(modifier)) return true;
        }
        return false;
    }
}
