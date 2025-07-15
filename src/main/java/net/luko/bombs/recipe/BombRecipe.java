package net.luko.bombs.recipe;

import net.luko.bombs.config.BombsConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public class BombRecipe extends ShapelessRecipe {
    public BombRecipe(ResourceLocation pId, String pGroup, CraftingBookCategory pCategory, ItemStack pResult, NonNullList<Ingredient> pIngredients) {
        super(pId, pGroup, pCategory, pResult, pIngredients);
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access){
        ItemStack stack = super.assemble(container, access);
        List<? extends String> defaultModifiers = BombsConfig.CRAFTING_DEFAULT_MODIFIERS.get();

        if(!defaultModifiers.isEmpty()){
            CompoundTag tag = stack.getOrCreateTag();
            ListTag modifiers = new ListTag();
            for(String mod : defaultModifiers){
                modifiers.add(StringTag.valueOf(mod));
            }
            tag.put("Modifiers", modifiers);
        }

        return stack;
    }
}
