package net.luko.bombs.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
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

import java.util.*;

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
    public boolean matches(Container isolatedContainer, Level level){
        ItemStack bomb = isolatedContainer.getItem(0);
        ItemStack modifier = isolatedContainer.getItem(1);

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
    public ItemStack assemble(Container isolatedContainer, RegistryAccess access){
        ItemStack bomb = isolatedContainer.getItem(0).copy();

        if (bomb.isEmpty()) return ItemStack.EMPTY;

        bomb.setCount(1);

        CompoundTag tag = bomb.getOrCreateTag();

        ListTag oldModifiers = tag.getList("Modifiers", CompoundTag.TAG_STRING);
        oldModifiers.add(StringTag.valueOf(modifierName));

        ListTag newModifiers = sortedModifiers(oldModifiers);

        tag.put("Modifiers", newModifiers);

        return bomb;
    }

    private ListTag sortedModifiers(ListTag modifiersTag){

        // Extract String tags to a String ArrayList
        ArrayList<String> modifiersArray = new ArrayList<>();
        for(int i = 0; i < modifiersTag.size(); i++){
            modifiersArray.add(modifiersTag.getString(i));
        }

        // Sort modifiersArray using a preset order
        Map<String, Integer> orderMap = Map.ofEntries(
                Map.entry("golden", 0),
                Map.entry("flame", 1),
                Map.entry("light", 2),
                Map.entry("contained", 3),
                Map.entry("pacified", 4),
                Map.entry("dampened", 5),
                Map.entry("shatter", 6),
                Map.entry("lethal", 7),
                Map.entry("shockwave", 8),
                Map.entry("evaporate", 9),
                Map.entry("gentle", 10)
        );

        Collections.sort(modifiersArray, (a, b) -> {
            int indexA = orderMap.getOrDefault(a, Integer.MAX_VALUE);
            int indexB = orderMap.getOrDefault(b, Integer.MAX_VALUE);
            return Integer.compare(indexA, indexB);
        });


        // Rebuild ListTag
        ListTag sortedModifiersTag = new ListTag();
        for(String s : modifiersArray){
            sortedModifiersTag.add(StringTag.valueOf(s));
        }

        return sortedModifiersTag;
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

            ListTag oldModifiers = tag.getList("Modifiers", CompoundTag.TAG_STRING);
            oldModifiers.add(StringTag.valueOf(modifierName));

            ListTag newModifiers = sortedModifiers(oldModifiers);
            tag.put("Modifiers", newModifiers);
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
