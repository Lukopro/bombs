package net.luko.bombs.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DemolitionUpgradeRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient inputBomb;
    private final Ingredient inputUpgrade;
    private final Ingredient inputCasing;
    private final ItemStack result;

    public DemolitionUpgradeRecipe(ResourceLocation id, Ingredient inputBomb, Ingredient inputUpgrade, Ingredient inputCasing, ItemStack result) {
        this.id = id;
        this.inputBomb = inputBomb;
        this.inputUpgrade = inputUpgrade;
        this.inputCasing = inputCasing;
        this.result = result;
    }

    public Ingredient getInputBomb(){
        return inputBomb;
    }

    public Ingredient getInputUpgrade(){
        return inputUpgrade;
    }

    public Ingredient getInputCasing(){
        return inputCasing;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return inputBomb.test(container.getItem(0)) &&
                inputUpgrade.test(container.getItem(1)) &&
                inputCasing.test(container.getItem(2));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess){
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height){
        return true;
    }

    public ItemStack getResultItem(){
        return result;
    }
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess){
        return result;
    }

    @Override
    public ResourceLocation getId(){
          return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer(){
        return ModRecipeSerializers.DEMOLITION_UPGRADE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType(){
        return ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get();
    }
}