package net.luko.bombs.recipe;

import net.luko.bombs.data.ModDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record DemolitionModifierRecipe(Ingredient inputBomb, Ingredient inputModifier, String modifierName, @Nullable String specialTag) implements Recipe<DemolitionModifierRecipeInput> {

    @Override
    public boolean matches(DemolitionModifierRecipeInput recipeInput, Level level){
        ItemStack bomb = recipeInput.getItem(0);
        ItemStack modifier = recipeInput.getItem(1);

        if(!inputBomb.test(bomb) || !inputModifier.test(modifier)) return false;

        List<String> modifiers = bomb.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of());

        for (String s : modifiers) {
            if (s.equals(modifierName)) {
                return false; // Already has this upgrade
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(DemolitionModifierRecipeInput recipeInput, HolderLookup.Provider provider){
        ItemStack bomb = recipeInput.getItem(0).copy();

        if (bomb.isEmpty()) return ItemStack.EMPTY;

        bomb.setCount(1);

        List<String> oldModifiers = new ArrayList<>(bomb.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of()));
        oldModifiers.add(modifierName);
        List<String> newModifiers = sortedModifiers(oldModifiers);

        bomb.set(ModDataComponents.MODIFIERS.get(), newModifiers);

        if(specialTag != null && specialTag.equals("Potion")){
            bomb.set(DataComponents.POTION_CONTENTS,
                    recipeInput.getItem(1).get(DataComponents.POTION_CONTENTS));
        }

        return bomb;
    }

    private List<String> sortedModifiers(List<String> modifiers){
        // Sort modifiersArray using a preset order
        Map<String, Integer> orderMap = Map.ofEntries(
                Map.entry("imbued", -1),
                Map.entry("golden", 0),
                Map.entry("flame", 1),
                Map.entry("light", 2),
                Map.entry("float", 3),
                Map.entry("sink", 4),
                Map.entry("contained", 5),
                Map.entry("pacified", 6),
                Map.entry("dampened", 7),
                Map.entry("shatter", 8),
                Map.entry("lethal", 9),
                Map.entry("shockwave", 10),
                Map.entry("evaporate", 11),
                Map.entry("gentle", 12)
        );

        List<String> sortedModifiers = new ArrayList<>(modifiers);

        Collections.sort(sortedModifiers, (a, b) -> {
            int indexA = orderMap.getOrDefault(a, Integer.MAX_VALUE);
            int indexB = orderMap.getOrDefault(b, Integer.MAX_VALUE);
            return Integer.compare(indexA, indexB);
        });

        return sortedModifiers;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height){
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider){
        ItemStack result = inputBomb.getItems().length > 0
                ? inputBomb.getItems()[0].copy()
                : ItemStack.EMPTY;

        if(!result.isEmpty()){
            List<String> oldModifiers = new ArrayList<>(result.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of()));
            oldModifiers.add(modifierName);

            List<String> newModifiers = sortedModifiers(oldModifiers);
            result.set(ModDataComponents.MODIFIERS.get(), newModifiers);
        }

        return result;
    }

    public RecipeSerializer<?> getSerializer(){
        return ModRecipeSerializers.DEMOLITION_MODIFIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType(){
        return ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get();
    }
}
