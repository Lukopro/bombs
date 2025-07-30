package net.luko.bombs.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.luko.bombs.Bombs;
import net.luko.bombs.data.modifiers.ModifierManager;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Shadow
    private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;

    @Shadow
    private Map<ResourceLocation, Recipe<?>> byName;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void bombs$injectModifierRecipes(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler, CallbackInfo ci){
        ModifierManager.INSTANCE.apply(pResourceManager);
        var modifierRecipes = ModifierManager.INSTANCE.getRecipes();

        if((modifierRecipes == null || modifierRecipes.isEmpty())) return;

        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipes = new HashMap<>(this.recipes);
        Map<ResourceLocation, Recipe<?>> newByName = new HashMap<>(this.byName);

        int before = newRecipes.size();

        newRecipes.put(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get(), modifierRecipes);
        newByName.putAll(modifierRecipes);

        int after = newRecipes.size();

        this.recipes = ImmutableMap.copyOf(newRecipes);
        this.byName = ImmutableMap.copyOf(newByName);

        Bombs.LOGGER.info("Injected {} modifier recipes.", after - before);
    }
}
