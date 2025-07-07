package net.luko.bombs.recipe;

import net.luko.bombs.Bombs;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipe;
import net.luko.bombs.recipe.demolition.DemolitionUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Bombs.MODID);

    public static final RegistryObject<RecipeType<DemolitionUpgradeRecipe>> DEMOLITION_UPGRADE_TYPE =
            RECIPE_TYPES.register("demolition_upgrade", () -> new RecipeType<>() {
                public String toString() {
                    return Bombs.MODID + ":demolition_upgrade";
                }
            });

    public static final RegistryObject<RecipeType<DemolitionModifierRecipe>> DEMOLITION_MODIFIER_TYPE =
            RECIPE_TYPES.register("demolition_modifier", () -> new RecipeType<>() {
                public String toString() {
                    return Bombs.MODID + ":demolition_modifier";
                }
            });

    public static final RegistryObject<RecipeType<BombRecipe>> BOMB_TYPE =
            RECIPE_TYPES.register("bomb", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Bombs.MODID + ":bomb";
                }
            });

    public static void register(IEventBus eventBus){
        RECIPE_TYPES.register(eventBus);
    }
}
