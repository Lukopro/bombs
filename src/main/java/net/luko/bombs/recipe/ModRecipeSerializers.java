package net.luko.bombs.recipe;

import net.luko.bombs.Bombs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Bombs.MODID);

    public static final RegistryObject<RecipeSerializer<DemolitionTableRecipe>> DEMOLITION_SERIALIZER =
            SERIALIZERS.register("demolition", DemolitionTableRecipeSerializer::new);

    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
    }

}
