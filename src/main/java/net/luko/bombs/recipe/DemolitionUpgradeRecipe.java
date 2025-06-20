package net.luko.bombs.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.luko.bombs.data.ModDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record DemolitionUpgradeRecipe(ResourceLocation id, Ingredient inputBomb, Ingredient inputUpgrade,
                                      Ingredient inputCasing, int tier)
                                      implements Recipe<DemolitionUpgradeRecipeInput> {

    public static final MapCodec<DemolitionUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(DemolitionUpgradeRecipe::id),
                    Ingredient.CODEC.fieldOf("input_bomb").forGetter(DemolitionUpgradeRecipe::inputBomb),
                    Ingredient.CODEC.fieldOf("input_upgrade").forGetter(DemolitionUpgradeRecipe::inputUpgrade),
                    Ingredient.CODEC.fieldOf("input_casing").forGetter(DemolitionUpgradeRecipe::inputCasing),
                    Codec.INT.fieldOf("tier").forGetter(DemolitionUpgradeRecipe::tier)
            ).apply(instance, DemolitionUpgradeRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DemolitionUpgradeRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, DemolitionUpgradeRecipe::id,
                    Ingredient.CONTENTS_STREAM_CODEC, DemolitionUpgradeRecipe::inputBomb,
                    Ingredient.CONTENTS_STREAM_CODEC, DemolitionUpgradeRecipe::inputUpgrade,
                    Ingredient.CONTENTS_STREAM_CODEC, DemolitionUpgradeRecipe::inputCasing,
                    ByteBufCodecs.INT, DemolitionUpgradeRecipe::tier,
                    DemolitionUpgradeRecipe::new
            );

    @Override
    public boolean matches(DemolitionUpgradeRecipeInput recipeInput, Level level) {
        return inputBomb.test(recipeInput.getItem(0)) &&
                inputUpgrade.test(recipeInput.getItem(1)) &&
                inputCasing.test(recipeInput.getItem(2));
    }

    @Override
    public ItemStack assemble(DemolitionUpgradeRecipeInput recipeInput, HolderLookup.Provider provider) {
        ItemStack bomb = recipeInput.getItem(0).copy();

        if (bomb.isEmpty()) return ItemStack.EMPTY;

        bomb.setCount(1);

        bomb.set(ModDataComponents.TIER.get(), tier);

        return bomb;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        ItemStack result = inputBomb.getItems().length > 0
                ? inputBomb.getItems()[0].copy()
                : ItemStack.EMPTY;

        if (!result.isEmpty()) {
            result.set(ModDataComponents.TIER.get(), tier);
        }

        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.DEMOLITION_UPGRADE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get();
    }
}