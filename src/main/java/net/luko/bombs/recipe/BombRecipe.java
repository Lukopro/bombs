package net.luko.bombs.recipe;

import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.config.BombsConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BombRecipe extends ShapelessRecipe {
    public BombRecipe(String pGroup, CraftingBookCategory pCategory, ItemStack pResult, NonNullList<Ingredient> pIngredients) {
        super(pGroup, pCategory, pResult, pIngredients);
    }

    @Override
    public boolean matches(CraftingInput input, Level level){
        if(!BombsConfig.isBombEnabled(this.getResultItem(level.registryAccess()))) return false;
        return super.matches(input, level);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries){
        ItemStack stack = super.assemble(input, registries);
        List<String> defaultModifiers = new ArrayList<>(BombsConfig.CRAFTING_DEFAULT_MODIFIERS.get());

        if(!defaultModifiers.isEmpty()){
            stack.set(ModDataComponents.MODIFIERS.get(), defaultModifiers);
        }

        return stack;
    }
}
