package net.luko.bombs.recipe;

import net.luko.bombs.Bombs;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipe;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipeSerializer;
import net.luko.bombs.recipe.demolition.DemolitionUpgradeRecipe;
import net.luko.bombs.recipe.demolition.DemolitionUpgradeRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Bombs.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DemolitionUpgradeRecipe>> DEMOLITION_UPGRADE_SERIALIZER =
            SERIALIZERS.register("demolition_upgrade",
                    DemolitionUpgradeRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DemolitionModifierRecipe>> DEMOLITION_MODIFIER_SERIALIZER =
            SERIALIZERS.register("demolition_modifier",
                    DemolitionModifierRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BombRecipe>> BOMB_SERIALIZER =
            SERIALIZERS.register("bomb",
                    BombRecipeSerializer::new);

    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
    }

}
