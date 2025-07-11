package net.luko.bombs.util;

import net.luko.bombs.recipe.ModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BombRecipeUtil {
    public static boolean validUpgradeIngredient(Level level, ItemStack stack){
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get()).stream()
                .anyMatch(recipe -> recipe.getInputUpgrade().test(stack));
    }
    public static boolean validModifierIngredient(Level level, ItemStack stack){
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get()).stream()
                .anyMatch(recipe -> recipe.getInputModifier().test(stack));
    }
}
