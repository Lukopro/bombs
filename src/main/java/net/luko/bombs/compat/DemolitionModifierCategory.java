package net.luko.bombs.compat;


import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.luko.bombs.Bombs;
import net.luko.bombs.block.ModBlocks;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipe;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipeInput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DemolitionModifierCategory implements IRecipeCategory<DemolitionModifierRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "demolition_modifier");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Bombs.MODID,
            "textures/gui/demolition_jei_recipe.png");

    public static final RecipeType<DemolitionModifierRecipe> DEMOLITION_MODIFIER_TYPE =
            new RecipeType<>(UID, DemolitionModifierRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public DemolitionModifierCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 122, 64);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.DEMOLITION_TABLE.get()));
    }

    @Override
    public RecipeType<DemolitionModifierRecipe> getRecipeType() {
        return DEMOLITION_MODIFIER_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.bombs.demolition_modifier");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DemolitionModifierRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 25)
                .addIngredients(recipe.inputBomb());

        builder.addSlot(RecipeIngredientRole.INPUT, 53, 25)
                .addIngredients(recipe.inputModifier());

        List<ItemStack> inputFocuses = focuses.getFocuses(RecipeIngredientRole.INPUT)
                .map(focus -> focus.getTypedValue().getItemStack())
                .flatMap(Optional::stream)
                .filter(stack -> !stack.isEmpty())
                .toList();

        List<ItemStack> possibleOutputs = new ArrayList<>();
        for(ItemStack bomb : recipe.inputBomb().getItems()){
            for(ItemStack modifier : recipe.inputModifier().getItems()){
                boolean bombMatches = inputFocuses.isEmpty() || inputFocuses.stream().anyMatch(
                        focused -> ItemStack.isSameItem(bomb, focused));
                boolean modifierMatches = inputFocuses.isEmpty() || inputFocuses.stream().anyMatch(
                        focused -> ItemStack.isSameItem(modifier, focused));

                if(!bombMatches && !modifierMatches) continue;

                DemolitionModifierRecipeInput recipeInput = new DemolitionModifierRecipeInput(bomb, modifier);
                possibleOutputs.add(recipe.assemble(recipeInput, null));
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 25)
                .addItemStacks(possibleOutputs);
    }
}
