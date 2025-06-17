package net.luko.bombs.util;

import net.luko.bombs.recipe.ModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

public class RecipeUtil {
    public static boolean validUpgradeIngredient(Level level, ItemStack stack){
        RecipeManager manager = level.getRecipeManager();
        return manager.getAllRecipesFor(ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get()).stream()
                .anyMatch(recipe -> recipe.getInputUpgrade().test(stack))
                || manager.getAllRecipesFor(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get()).stream()
                .anyMatch(recipe -> recipe.getInputModifier().test(stack));
    }
    public static boolean validCasingIngredient(Level level, ItemStack stack){
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get()).stream()
                .anyMatch(recipe -> recipe.getInputCasing().test(stack));
    }
}
