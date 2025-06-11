package net.luko.bombs.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class DemolitionModifierRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient inputBomb;
    private final Ingredient inputModifier;
    private final String modifierName;

    public DemolitionModifierRecipe(ResourceLocation id, Ingredient inputBomb, Ingredient inputModifier, String modifierName){
        this.id = id;
        this.inputBomb = inputBomb;
        this.inputModifier = inputModifier;
        this.modifierName = modifierName;
    }

    @Override
    public boolean matches(Container container, Level level){
        ItemStack bomb = container.getItem(0);
        ItemStack modifier = container.getItem(1);

        if(!inputBomb.test(bomb) || !inputModifier.test(modifier)) return false;

        CompoundTag tag = bomb.getOrCreateTag();
        ListTag modifiers = tag.getList("Modifiers", CompoundTag.TAG_STRING);

        for(int i = 0; i < modifiers.size(); i++){
            if(modifiers.getString(i).equals(modifierName)){
                return false; // Already has this upgrade
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access){
        ItemStack bomb = container.getItem(0).copy();

        if (bomb.isEmpty()) return ItemStack.EMPTY;

        bomb.setCount(1);

        CompoundTag tag = bomb.getOrCreateTag();

        ListTag modifiers = tag.getList("Modifiers", CompoundTag.TAG_STRING);
        modifiers.add(StringTag.valueOf(modifierName));
        tag.put("Modifiers", modifiers);

        return bomb;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height){
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access){
        ItemStack result = inputBomb.getItems().length > 0
                ? inputBomb.getItems()[0].copy()
                : ItemStack.EMPTY;

        if(!result.isEmpty()){
            CompoundTag tag = result.getOrCreateTag();
            ListTag modifiers = tag.getList("Modifiers", CompoundTag.TAG_STRING);
            modifiers.add(StringTag.valueOf(modifierName));
            tag.put("Modifiers", modifiers);
        }

        return result;
    }

    @Override
    public ResourceLocation getId(){
        return id;
    }

    public Ingredient getInputBomb(){
        return inputBomb;
    }

    public Ingredient getInputModifier(){
        return inputModifier;
    }

    public String getModifierName(){
        return modifierName;
    }

    public RecipeSerializer<?> getSerializer(){
        return ModRecipeSerializers.DEMOLITION_MODIFIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType(){
        return ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get();
    }
}
