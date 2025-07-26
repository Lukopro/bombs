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
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        bombInputs.add(new ItemStack(ModItems.GRENADE.get()));
        for(int i = 2; i <= 5; i++){
            ItemStack dynamite = new ItemStack(ModItems.DYNAMITE.get());
            dynamite.set(ModDataComponents.TIER.get(), i);
            bombInputs.add(dynamite);
            ItemStack grenade = new ItemStack(ModItems.GRENADE.get());
            grenade.set(ModDataComponents.TIER.get(), i);
            bombInputs.add(grenade);
        }

        List<ItemStack> inputFocuses = focuses.getFocuses(RecipeIngredientRole.INPUT)
                .map(focus -> focus.getTypedValue().getItemStack())
                .flatMap(Optional::stream)
                .filter(stack -> !stack.isEmpty())
                .toList();

        if(!inputFocuses.isEmpty())
            bombInputs.removeIf(input -> inputFocuses.stream()
                    .noneMatch(focus -> ItemStack.isSameItem(input, focus)));

        List<ItemStack> validBombInputs = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();
        for(ItemStack input : bombInputs){
            DemolitionUpgradeRecipeInput recipeInput =
                    new DemolitionUpgradeRecipeInput(input, recipe.inputUpgrade().getItems()[0]);

            if(recipe.matches(recipeInput, null)) {
                ItemStack result = recipe.assemble(recipeInput, null);
                if (!result.isEmpty()) {
                    validBombInputs.add(input);
                    outputs.add(result);
                }
            }
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 19, 25)
                .addItemStacks(validBombInputs);

        builder.addSlot(RecipeIngredientRole.INPUT, 53, 25)
                .addIngredients(recipe.inputUpgrade());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 25)
                .addItemStacks(outputs);
    }
}
