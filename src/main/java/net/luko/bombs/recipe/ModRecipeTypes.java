package net.luko.bombs.recipe;

import net.luko.bombs.Bombs;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipe;
import net.luko.bombs.recipe.demolition.DemolitionUpgradeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Bombs.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DemolitionUpgradeRecipe>> DEMOLITION_UPGRADE_TYPE =
            RECIPE_TYPES.register("demolition_upgrade", () -> new RecipeType<>() {
                public String toString() {
                    return Bombs.MODID + ":demolition_upgrade";
                }
            });

    public static final DeferredHolder<RecipeType<?>, RecipeType<DemolitionModifierRecipe>> DEMOLITION_MODIFIER_TYPE =
            RECIPE_TYPES.register("demolition_modifier", () -> new RecipeType<>() {
                public String toString() {
                    return Bombs.MODID + ":demolition_modifier";
                }
            });

    public static final DeferredHolder<RecipeType<?>, RecipeType<BombRecipe>> BOMB_TYPE =
            RECIPE_TYPES.register("bomb", () -> new RecipeType<>() {
                public String toString(){
                    return Bombs.MODID + ":bomb";
                }
            });

    public static void register(IEventBus eventBus){
        RECIPE_TYPES.register(eventBus);
    }
}
