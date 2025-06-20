package net.luko.bombs.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record DemolitionUpgradeRecipeInput(ItemStack inputBomb, ItemStack inputUpgrade, ItemStack inputCasing) implements RecipeInput {
    @Override
    public ItemStack getItem(int pSlot) {
        return switch (pSlot) {
            case (1) -> inputUpgrade;
            case (2) -> inputCasing;
            default -> inputBomb;
        };

    }

    @Override
    public int size() {
        return 3;
    }

}
