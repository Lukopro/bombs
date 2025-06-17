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
import net.luko.bombs.recipe.DemolitionUpgradeRecipe;
import net.luko.bombs.screen.DemolitionTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DemolitionUpgradeCategory implements IRecipeCategory<DemolitionUpgradeRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "demolition_upgrade");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Bombs.MODID,
            "textures/gui/demolition_table_gui_upgrade.png");

    public static final RecipeType<DemolitionUpgradeRecipe> DEMOLITION_UPGRADE_TYPE =
            new RecipeType<>(UID, DemolitionUpgradeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public DemolitionUpgradeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 20, 176, 55);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.DEMOLITION_TABLE.get()));
    }

    @Override
    public RecipeType<DemolitionUpgradeRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, DemolitionUpgradeRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT,
                DemolitionTableMenu.DEMOLITION_TABLE_SLOT_0_X,
                DemolitionTableMenu.DEMOLITION_TABLE_SLOTS_Y - 20).
                addIngredients(recipe.getInputBomb());

        builder.addSlot(RecipeIngredientRole.INPUT,
                        DemolitionTableMenu.DEMOLITION_TABLE_SLOT_1_X,
                        DemolitionTableMenu.DEMOLITION_TABLE_SLOTS_Y - 20).
                addIngredients(recipe.getInputUpgrade());

        builder.addSlot(RecipeIngredientRole.INPUT,
                        DemolitionTableMenu.DEMOLITION_TABLE_SLOT_2_X,
                        DemolitionTableMenu.DEMOLITION_TABLE_SLOTS_Y - 20).
                addIngredients(recipe.getInputCasing());

        builder.addSlot(RecipeIngredientRole.OUTPUT,
                DemolitionTableMenu.DEMOLITION_TABLE_SLOT_3_X,
                DemolitionTableMenu.DEMOLITION_TABLE_SLOTS_Y - 20)
                .addItemStack(recipe.getResultItem(null));
    }
}
