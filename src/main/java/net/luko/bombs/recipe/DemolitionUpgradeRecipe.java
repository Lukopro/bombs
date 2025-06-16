package net.luko.bombs.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class DemolitionUpgradeRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient inputBomb;
    private final Ingredient inputUpgrade;
    private final Ingredient inputCasing;
    private final int tier;

    public DemolitionUpgradeRecipe(ResourceLocation id, Ingredient inputBomb, Ingredient inputUpgrade, Ingredient inputCasing, int tier) {
        this.id = id;
        this.inputBomb = inputBomb;
        this.inputUpgrade = inputUpgrade;
        this.inputCasing = inputCasing;
        this.tier = tier;
    }

    public Ingredient getInputBomb(){
        return this.inputBomb;
    }

    public Ingredient getInputUpgrade(){
        return this.inputUpgrade;
    }

    public Ingredient getInputCasing(){
        return this.inputCasing;
    }

    public int getTier(){
        return this.tier;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return inputBomb.test(container.getItem(0)) &&
                inputUpgrade.test(container.getItem(1)) &&
                inputCasing.test(container.getItem(2));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess){
        ItemStack bomb = container.getItem(0).copy();

        if (bomb.isEmpty()) return ItemStack.EMPTY;

        bomb.setCount(1);

        CompoundTag tag = bomb.getOrCreateTag();

        tag.putInt("Tier", tier);

        return bomb;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height){
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess){
        ItemStack result = inputBomb.getItems().length > 0
                ? inputBomb.getItems()[0].copy()
                : ItemStack.EMPTY;

        if(!result.isEmpty()){
            CompoundTag tag = result.getOrCreateTag();
            tag.putInt("Tier", tier);
        }

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