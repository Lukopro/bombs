package net.luko.bombs.recipe.demolition;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record DemolitionModifierRecipeInput(ItemStack inputBomb, ItemStack inputModifier) implements RecipeInput {
    @Override
    public ItemStack getItem(int pSlot) {
        if(pSlot == 0){
            return inputBomb;
        }
        return inputModifier;
    }

    @Override
    public int size() {
        return 2;
    }
}
