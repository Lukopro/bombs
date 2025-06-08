package net.luko.bombs.recipe;

import net.luko.bombs.Bombs;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Bombs.MODID);

    public static final RegistryObject<RecipeType<DemolitionTableRecipe>> DEMOLITION_TYPE =
            RECIPE_TYPES.register("demolition", () -> new RecipeType<>() {
                public String toString() {
                    return Bombs.MODID + ":demolition";
                }
            });

    public static void register(IEventBus eventBus){
        RECIPE_TYPES.register(eventBus);
    }
}
