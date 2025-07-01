package net.luko.bombs.compat;

import com.mojang.serialization.Decoder;
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
import net.luko.bombs.item.ModItems;
import net.luko.bombs.recipe.DemolitionUpgradeRecipe;
import net.luko.bombs.screen.DemolitionTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DemolitionUpgradeCategory implements IRecipeCategory<DemolitionUpgradeRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "demolition_upgrade");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Bombs.MODID,
            "textures/gui/demolition_jei_recipe.png");

    public static final RecipeType<DemolitionUpgradeRecipe> DEMOLITION_UPGRADE_TYPE =
            new RecipeType<>(UID, DemolitionUpgradeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public DemolitionUpgradeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 122, 64);
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
        List<ItemStack> bombInputs = new ArrayList<>();
        bombInputs.add(new ItemStack(ModItems.DYNAMITE.get()));
        for(int i = 2; i <= 5; i++){
            ItemStack bomb = new ItemStack(ModItems.DYNAMITE.get());
            bomb.getOrCreateTag().putInt("Tier", i);
            bombInputs.add(bomb);
        }


        List<ItemStack> validBombInputs = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();
        for(ItemStack input : bombInputs){
            SimpleContainer tempIsolatedContainer = new SimpleContainer(2);
            tempIsolatedContainer.setItem(0, input);
            tempIsolatedContainer.setItem(1, recipe.getInputUpgrade().getItems()[0]);

            if(recipe.matches(tempIsolatedContainer, null)) {
                validBombInputs.add(input);
                ItemStack result = recipe.assemble(tempIsolatedContainer, null);
                if (!result.isEmpty()) {
                    outputs.add(result);
                }
            }
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 19, 25)
                        .addItemStacks(validBombInputs);

        builder.addSlot(RecipeIngredientRole.INPUT, 53, 25)
                .addIngredients(recipe.getInputUpgrade());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 25)
                .addItemStacks(outputs);
    }
}
