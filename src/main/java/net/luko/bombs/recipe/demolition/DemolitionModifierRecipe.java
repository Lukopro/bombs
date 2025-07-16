package net.luko.bombs.recipe.demolition;

import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.data.modifiers.ModifierPriorityManager;
import net.luko.bombs.recipe.ModRecipeSerializers;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
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
    private final String specialTag;

    private final Map<String, Set<String>> incompatibleModifierLists = Map.ofEntries(
            Map.entry("laden", Set.of("imbued")),
            Map.entry("imbued", Set.of("laden")),
            Map.entry("frost", Set.of("dirt")),
            Map.entry("dirt", Set.of("frost"))
    );


    public DemolitionModifierRecipe(ResourceLocation id, Ingredient inputBomb, Ingredient inputModifier, String modifierName, String specialTag){
        this.id = id;
        this.inputBomb = inputBomb;
        this.inputModifier = inputModifier;
        this.modifierName = modifierName;
        this.specialTag = specialTag;
    }

    @Override
    public boolean matches(Container isolatedContainer, Level level){
        ItemStack bombItem = isolatedContainer.getItem(0);
        ItemStack modifierItem = isolatedContainer.getItem(1);

        if(!inputBomb.test(bombItem) || !inputModifier.test(modifierItem)) return false;

        if(BombsConfig.CRAFTING_RESTRICTED_MODIFIERS.get().contains(modifierName)) return false;

        CompoundTag tag = bombItem.getOrCreateTag();
        ListTag modifiers = tag.getList("Modifiers", CompoundTag.TAG_STRING);

        for(int i = 0; i < modifiers.size(); i++){
            if(modifiers.getString(i).equals(modifierName) ||
                    incompatibleModifierLists.getOrDefault(modifiers.getString(i), Set.of()).contains(modifierName)){
                return false; // Already has this modifier or incompatible modifier
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

        if(specialTag != null){
            switch (specialTag){
                case "Potion":{
                    ItemStack potion = isolatedContainer.getItem(1).copy();
                    if(!potion.hasTag()){
                        System.out.println("[BOMBS] Wtf is that potion?? Where did you get that?");
                        return ItemStack.EMPTY;
                    }
                    if(potion.getTag().contains("CustomPotionEffects")) {
                        tag.put("CustomPotionEffects",
                                potion.getTag().getList("CustomPotionEffects", Tag.TAG_COMPOUND).copy());
                    }else{
                        tag.putString("Potion",
                                potion.getTag().getString("Potion"));
                    }
                }
                case "Theme":{
                    tag.putString("Theme", modifierName);
                }
            }

        }

        return bomb;
    }

    private ListTag sortedModifiers(ListTag modifiersTag){

        // Extract String tags to a String ArrayList
        ArrayList<String> modifiersArray = new ArrayList<>();
        for(int i = 0; i < modifiersTag.size(); i++){
            modifiersArray.add(modifiersTag.getString(i));
        }

        Map<String, Integer> orderMap = ModifierPriorityManager.INSTANCE.getPriorities();

        modifiersArray.sort((a, b) -> {
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

    public String getSpecialTag(){
        return specialTag;
    }

    public RecipeSerializer<?> getSerializer(){
        return ModRecipeSerializers.DEMOLITION_MODIFIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType(){
        return ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get();
    }
}
