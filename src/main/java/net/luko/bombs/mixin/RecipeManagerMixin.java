package net.luko.bombs.mixin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import net.luko.bombs.data.modifiers.ModifierManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeHolder;
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
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Shadow
    private Map<ResourceLocation, RecipeHolder<?>> byName;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void bombs$injectModifierRecipes(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler, CallbackInfo ci){
        ModifierManager.INSTANCE.apply(pResourceManager);
        var generatedRecipes = ModifierManager.INSTANCE.generateRecipes();
        Multimap<RecipeType<?>, RecipeHolder<?>> modifierRecipesByType = generatedRecipes.a;
        if((modifierRecipesByType == null || modifierRecipesByType.isEmpty())) return;
        var modifierRecipesByName = generatedRecipes.b;

        Multimap<RecipeType<?>, RecipeHolder<?>> newByType = ArrayListMultimap.create();
        newByType.putAll(this.byType);
        Map<ResourceLocation, RecipeHolder<?>> newByName = new HashMap<>(this.byName);

        newByType.putAll(modifierRecipesByType);
        newByName.putAll(modifierRecipesByName);

        this.byType = newByType;
        this.byName = newByName;
    }
}
