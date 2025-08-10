package net.luko.bombs.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.luko.bombs.Bombs;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipe;
import net.luko.bombs.recipe.demolition.DemolitionUpgradeRecipe;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.screen.DemolitionTableScreen;
import net.luko.bombs.util.BombRecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
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

        List<DemolitionUpgradeRecipe> upgradeRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get());
        List<UpgradeRecipeDisplay> upgradeDisplayRecipes = new ArrayList<>();
        List<ItemStack> bombInputs = BombRecipeUtil.allBombsAllTiers();

        for(DemolitionUpgradeRecipe recipe : upgradeRecipes){
            for(ItemStack bomb : bombInputs){

                SimpleContainer temp = new SimpleContainer(2);
                temp.setItem(0, bomb);
                temp.setItem(1, recipe.getInputUpgrade().getItems()[0]);

                if(recipe.matches(temp, Minecraft.getInstance().level)){
                    ItemStack output = recipe.assemble(temp, Minecraft.getInstance().level.registryAccess());
                    if(!output.isEmpty()){
                        upgradeDisplayRecipes.add(new UpgradeRecipeDisplay(
                                bomb.copy(), recipe.getInputUpgrade(), output.copy()
                        ));
                    }
                }
            }
        }

        List<DemolitionModifierRecipe> modifierRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get());
        registration.addRecipes(DemolitionUpgradeCategory.DEMOLITION_UPGRADE_TYPE, upgradeDisplayRecipes);
        registration.addRecipes(DemolitionModifierCategory.DEMOLITION_MODIFIER_TYPE, modifierRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(DemolitionTableScreen.class, 68, 11, 6, 6,
                DemolitionUpgradeCategory.DEMOLITION_UPGRADE_TYPE);
        registration.addRecipeClickArea(DemolitionTableScreen.class, 113, 11, 6, 6,
                DemolitionModifierCategory.DEMOLITION_MODIFIER_TYPE);
    }

    public record UpgradeRecipeDisplay(ItemStack bomb, Ingredient upgrade, ItemStack output){}
}
