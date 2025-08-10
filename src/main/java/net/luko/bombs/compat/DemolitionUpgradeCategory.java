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
import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.recipe.demolition.DemolitionUpgradeRecipe;
import net.luko.bombs.recipe.demolition.DemolitionUpgradeRecipeInput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DemolitionUpgradeCategory implements IRecipeCategory<JEIBombsPlugin.UpgradeRecipeDisplay> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "demolition_upgrade");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Bombs.MODID,
            "textures/gui/demolition_jei_recipe.png");

    public static final RecipeType<JEIBombsPlugin.UpgradeRecipeDisplay> DEMOLITION_UPGRADE_TYPE =
            new RecipeType<>(UID, JEIBombsPlugin.UpgradeRecipeDisplay.class);

    private final IDrawable background;
    private final IDrawable icon;

    public DemolitionUpgradeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 122, 64);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.DEMOLITION_TABLE.get()));
    }

    @Override
    public RecipeType<JEIBombsPlugin.UpgradeRecipeDisplay> getRecipeType() {
        return DEMOLITION_UPGRADE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.bombs.demolition_upgrade");
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
    public void setRecipe(IRecipeLayoutBuilder builder, JEIBombsPlugin.UpgradeRecipeDisplay recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 19, 25)
                .addItemStack(recipe.bomb());

        builder.addSlot(RecipeIngredientRole.INPUT, 53, 25)
                .addIngredients(recipe.upgrade());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 25)
                .addItemStack(recipe.output());
    }
}
