package net.luko.bombs.util;

import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BombRecipeUtil {
    public static boolean validUpgradeIngredient(Level level, ItemStack stack){
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get()).stream()
                .map(RecipeHolder::value)
                .anyMatch(recipe -> recipe.inputUpgrade().test(stack));
    }
    public static boolean validModifierIngredient(Level level, ItemStack stack){
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get()).stream()
                .map(RecipeHolder::value)
                .anyMatch(recipe -> recipe.inputModifier().test(stack));
    }

    public static List<ItemStack> allBombsAllTiers(){
        List<ItemStack> bombs = new ArrayList<>();
        bombs.add(new ItemStack(ModItems.DYNAMITE.get()));
        bombs.add(new ItemStack(ModItems.GRENADE.get()));
        for(int i = 2; i <= 6; i++){
            ItemStack dynamite = new ItemStack(ModItems.DYNAMITE.get());
            ItemStack grenade = new ItemStack(ModItems.GRENADE.get());

            dynamite.set(ModDataComponents.TIER, i);
            grenade.set(ModDataComponents.TIER, i);

            bombs.add(dynamite);
            bombs.add(grenade);
        }

        return bombs;
    }
}
