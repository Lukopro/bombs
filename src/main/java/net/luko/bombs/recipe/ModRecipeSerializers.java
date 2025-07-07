package net.luko.bombs.recipe;

import net.luko.bombs.Bombs;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipe;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipeSerializer;
import net.luko.bombs.recipe.demolition.DemolitionUpgradeRecipe;
import net.luko.bombs.recipe.demolition.DemolitionUpgradeRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Bombs.MODID);

    public static final RegistryObject<RecipeSerializer<DemolitionUpgradeRecipe>> DEMOLITION_UPGRADE_SERIALIZER =
            SERIALIZERS.register("demolition_upgrade", DemolitionUpgradeRecipeSerializer::new);

    public static final RegistryObject<RecipeSerializer<DemolitionModifierRecipe>> DEMOLITION_MODIFIER_SERIALIZER =
            SERIALIZERS.register("demolition_modifier", DemolitionModifierRecipeSerializer::new);

    public static final RegistryObject<RecipeSerializer<BombRecipe>> BOMB_SERIALIZER =
            SERIALIZERS.register("bomb", BombRecipeSerializer::new);

    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
    }

}
