package net.luko.bombs.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.luko.bombs.Bombs;
import net.luko.bombs.recipe.DemolitionModifierRecipe;
import net.luko.bombs.recipe.DemolitionUpgradeRecipe;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.screen.DemolitionTableScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIBombsPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new DemolitionUpgradeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new DemolitionModifierCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<DemolitionUpgradeRecipe> upgradeRecipes = recipeManager.
                getAllRecipesFor(ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();

        List<DemolitionModifierRecipe> modifierRecipes = recipeManager.
                getAllRecipesFor(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();

        registration.addRecipes(DemolitionUpgradeCategory.DEMOLITION_UPGRADE_TYPE, upgradeRecipes);
        registration.addRecipes(DemolitionModifierCategory.DEMOLITION_MODIFIER_TYPE, modifierRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(DemolitionTableScreen.class, 68, 11, 6, 6,
                DemolitionUpgradeCategory.DEMOLITION_UPGRADE_TYPE);
        registration.addRecipeClickArea(DemolitionTableScreen.class, 113, 11, 6, 6,
                DemolitionModifierCategory.DEMOLITION_MODIFIER_TYPE);
    }
}
