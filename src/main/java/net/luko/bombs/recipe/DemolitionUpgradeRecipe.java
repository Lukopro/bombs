package net.luko.bombs.recipe;

import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.item.BombItem;
import net.luko.bombs.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record DemolitionUpgradeRecipe(Ingredient inputUpgrade, int minTier, int maxTier) implements Recipe<DemolitionUpgradeRecipeInput> {

    @Override
    public boolean matches(DemolitionUpgradeRecipeInput recipeInput, Level level) {
        ItemStack bomb = recipeInput.getItem(0);
        return bomb.getItem() instanceof BombItem &&
                inputUpgrade.test(recipeInput.getItem(1)) &&
                bomb.getOrDefault(ModDataComponents.TIER.get(), 1) < this.maxTier;
    }

    @Override
    public ItemStack assemble(DemolitionUpgradeRecipeInput recipeInput, HolderLookup.Provider provider) {
        ItemStack bomb = recipeInput.getItem(0).copy();

        if (bomb.isEmpty()) return ItemStack.EMPTY;

        bomb.setCount(1);

        int oldTier = bomb.getOrDefault(ModDataComponents.TIER.get(), 1);

        bomb.set(ModDataComponents.TIER.get(),
                 oldTier < minTier ? minTier : oldTier + 1);

        return bomb;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        ItemStack result = new ItemStack(ModItems.DYNAMITE.get());

        if (!result.isEmpty()) {
            result.set(ModDataComponents.TIER.get(), minTier);
        }

        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.DEMOLITION_UPGRADE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get();
    }
}