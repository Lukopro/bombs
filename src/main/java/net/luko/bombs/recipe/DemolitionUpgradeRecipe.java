package net.luko.bombs.recipe;

import net.luko.bombs.item.BombItem;
import net.luko.bombs.item.ModItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
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
    private final Ingredient inputUpgrade;
    private final int minTier;
    private final int maxTier;

    public DemolitionUpgradeRecipe(ResourceLocation id, Ingredient inputUpgrade, int minTier, int maxTier) {
        this.id = id;
        this.inputUpgrade = inputUpgrade;
        this.minTier = minTier;
        this.maxTier = maxTier;
    }

    public Ingredient getInputUpgrade(){
        return this.inputUpgrade;
    }

    public int getMinTier(){
        return this.minTier;
    }

    public int getMaxTier(){
        return this.maxTier;
    }

    @Override
    public boolean matches(Container isolatedContainer, Level level) {
        ItemStack bomb = isolatedContainer.getItem(0);
        boolean alreadyHasTier = bomb.hasTag() && bomb.getTag().getInt("Tier") >= this.maxTier;
        return (bomb.getItem() instanceof BombItem) &&
                !alreadyHasTier &&
                inputUpgrade.test(isolatedContainer.getItem(1));
    }

    @Override
    public ItemStack assemble(Container isolatedContainer, RegistryAccess registryAccess){
        ItemStack bomb = isolatedContainer.getItem(0).copy();

        if (bomb.isEmpty()) return ItemStack.EMPTY;

        bomb.setCount(1);

        CompoundTag tag = bomb.getOrCreateTag();

        int oldTier = tag.getInt("Tier");
        if(oldTier == 0) oldTier = 1;

        int newTier = oldTier < minTier ? minTier : oldTier + 1;

        tag.putInt("Tier", newTier);

        return bomb;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height){
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess){
        ItemStack result = new ItemStack(ModItems.DYNAMITE.get());

        if(!result.isEmpty()){
            CompoundTag tag = result.getOrCreateTag();
            tag.putInt("Tier", minTier);
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