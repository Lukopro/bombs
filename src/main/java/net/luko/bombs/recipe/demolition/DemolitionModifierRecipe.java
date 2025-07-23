package net.luko.bombs.recipe.demolition;

import net.luko.bombs.Bombs;
import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.data.modifiers.ModifierIncompatibilityManager;
import net.luko.bombs.data.modifiers.ModifierPriorityManager;
import net.luko.bombs.recipe.ModRecipeSerializers;
import net.luko.bombs.recipe.ModRecipeTypes;
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

    public DemolitionModifierRecipe{
        if(specialTag == null)
            Bombs.LOGGER.debug(String.format("Modifier recipe registered for %s + %s gives '%s' with no special tag.",
                    inputBomb.toString(), inputModifier.toString(), modifierName));
        else Bombs.LOGGER.debug(String.format("Modifier recipe registered for %s + %s gives '%s' with special tag: %s",
                inputBomb.toString(), inputModifier.toString(), modifierName, specialTag));
    }

    @Override
    public boolean matches(DemolitionModifierRecipeInput recipeInput, Level level){
        ItemStack bomb = recipeInput.getItem(0);
        ItemStack modifier = recipeInput.getItem(1);

        if(!this.inputBomb.test(bomb) || !this.inputModifier.test(modifier)) return false;

        if(BombsConfig.CRAFTING_RESTRICTED_MODIFIERS.get().contains(this.modifierName)) return false;

        List<String> modifiers = bomb.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of());

        for (String s : modifiers) {
            if(!checkModifier(s)) return false;
        }
        return true;
    }

    private boolean checkModifier(String otherMod) {
        return this.checkModifier(this.modifierName, otherMod);
    }

    public boolean checkModifier(String mod, String otherMod){
        return !mod.equals(otherMod) &&  ModifierIncompatibilityManager.INSTANCE.isCompatible(mod, otherMod);
    }

    @Override
    public ItemStack assemble(DemolitionModifierRecipeInput recipeInput, HolderLookup.Provider provider){
        ItemStack bomb = recipeInput.getItem(0).copy();

        if (bomb.isEmpty()) return ItemStack.EMPTY;

        bomb.setCount(1);

        List<String> oldModifiers = new ArrayList<>(bomb.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of()));
        oldModifiers.add(this.modifierName);
        List<String> newModifiers = sortedModifiers(oldModifiers);

        bomb.set(ModDataComponents.MODIFIERS.get(), newModifiers);

        if(this.specialTag != null){
            switch (this.specialTag){
                case "Potion" ->
                    bomb.set(DataComponents.POTION_CONTENTS,
                            recipeInput.getItem(1).get(DataComponents.POTION_CONTENTS));

                case "Theme" ->
                    bomb.set(ModDataComponents.THEME, this.modifierName);
            }

        }

        return bomb;
    }

    private List<String> sortedModifiers(List<String> modifiers){
        // Sort modifiersArray using a preset order
        Map<String, Integer> orderMap = ModifierPriorityManager.INSTANCE.getPriorities();

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
        ItemStack result = this.inputBomb.getItems().length > 0
                ? this.inputBomb.getItems()[0].copy()
                : ItemStack.EMPTY;

        if(!result.isEmpty()){
            List<String> oldModifiers = new ArrayList<>(result.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of()));
            oldModifiers.add(this.modifierName);

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
