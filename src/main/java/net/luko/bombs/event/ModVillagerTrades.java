package net.luko.bombs.event;

import net.luko.bombs.Bombs;
import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.entity.villager.ModVillagerProfessions;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.recipe.demolition.DemolitionModifierRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = Bombs.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModVillagerTrades {
    @SubscribeEvent
    public static void addTrades(VillagerTradesEvent event) {
        if (!(event.getType() == ModVillagerProfessions.DEMOLITIONIST.get())) return;

        List<VillagerTrades.ItemListing> level1 = event.getTrades().get(1);
        List<VillagerTrades.ItemListing> level2 = event.getTrades().get(2);
        List<VillagerTrades.ItemListing> level3 = event.getTrades().get(3);
        List<VillagerTrades.ItemListing> level4 = event.getTrades().get(4);
        List<VillagerTrades.ItemListing> level5 = event.getTrades().get(5);

        level1.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.GUNPOWDER, random.nextIntBetweenInclusive(3, 6)),
                new ItemStack(Items.EMERALD, 1),
                16, 4, 0.05F
        ));

        level1.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.SAND, random.nextIntBetweenInclusive(12, 18)),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));

        level1.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.STRING, random.nextIntBetweenInclusive(5, 8)),
                new ItemStack(Items.EMERALD, 1),
                16, 3, 0.05F
        ));

        level2.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.BRICK, random.nextIntBetweenInclusive(6, 10)),
                new ItemStack(Items.EMERALD, 1),
                16, 3, 0.05F
        ));

        level2.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.WITHER_SKELETON_SKULL, 1),
                new ItemStack(Items.EMERALD, random.nextIntBetweenInclusive(4, 6)),
                16, 16, 0.05F
        ));

        level3.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, random.nextIntBetweenInclusive(3, 4)),
                new ItemStack(Items.TNT, 1),
                16, 10, 0.05F
        ));

        level3.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, random.nextIntBetweenInclusive(3, 4)),
                new ItemStack(Items.FIREWORK_ROCKET, 1),
                16, 10, 0.05F
        ));

        level3.add((trader, random) -> {
            int tier = random.nextIntBetweenInclusive(1, 3);
            int cost = random.nextIntBetweenInclusive(2, 4) + tier;

            return new MerchantOffer(
                    new ItemCost(Items.EMERALD, cost),
                    getBomb(trader.level(), random, ModItems.DYNAMITE.get(), tier, 0),
                    16, 12, 0.1F
            );
        });

        level3.add((trader, random) -> {
            int tier = random.nextIntBetweenInclusive(1, 3);
            int cost = random.nextIntBetweenInclusive(2, 4) + tier;

            return new MerchantOffer(
                    new ItemCost(Items.EMERALD, cost),
                    getBomb(trader.level(), random, ModItems.GRENADE.get(), tier, 0),
                    16, 12, 0.1F
            );
        });

        level4.add((trader, random) -> {
            int tier = random.nextIntBetweenInclusive(2, 4);
            int cost = random.nextIntBetweenInclusive(4, 8) + 2 * tier;

            return new MerchantOffer(
                    new ItemCost(Items.EMERALD, cost),
                    getBomb(trader.level(), random, ModItems.DYNAMITE.get(), tier, random.nextIntBetweenInclusive(2, 3)),
                    16, 20, 0.12F
            );
        });

        level4.add((trader, random) -> {
            int tier = random.nextIntBetweenInclusive(2, 4);
            int cost = random.nextIntBetweenInclusive(4, 8) + 2 * tier;

            return new MerchantOffer(
                    new ItemCost(Items.EMERALD, cost),
                    getBomb(trader.level(), random, ModItems.GRENADE.get(), tier, random.nextIntBetweenInclusive(2, 3)),
                    16, 20, 0.12F
            );
        });

        level5.add((trader, random) -> {
            int tier = random.nextIntBetweenInclusive(4, 6);
            int cost = random.nextIntBetweenInclusive(6, 12) + 4 * tier;

            return new MerchantOffer(
                    new ItemCost(Items.EMERALD, cost),
                    getBomb(trader.level(), random, ModItems.DYNAMITE.get(), tier, random.nextIntBetweenInclusive(4, 5)),
                    16, 30, 0.15F
            );
        });

        level5.add((trader, random) -> {
            int tier = random.nextIntBetweenInclusive(4, 6);
            int cost = random.nextIntBetweenInclusive(6, 12) + 4 * tier;

            return new MerchantOffer(
                    new ItemCost(Items.EMERALD, cost),
                    getBomb(trader.level(), random, ModItems.GRENADE.get(), tier, random.nextIntBetweenInclusive(4, 5)),
                    16, 30, 0.15F
            );
        });
    }

    private static ItemStack getBomb(Level level, RandomSource random, Item item, int tier, int numModifiers) {
        ItemStack stack = new ItemStack(item, 1);

        stack.set(ModDataComponents.TIER.get(), tier);

        List<String> defaultModifiers = new ArrayList<>(BombsConfig.CRAFTING_DEFAULT_MODIFIERS.get());

        stack.set(ModDataComponents.MODIFIERS.get(), defaultModifiers);

        if (numModifiers < 1) return stack;

        Map<String, List<String>> allModifiers = level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.inputBomb().test(stack))
                .filter(recipe -> !BombsConfig.CRAFTING_RESTRICTED_MODIFIERS.get().contains(recipe.modifierName()))
                .filter(recipe -> !defaultModifiers.contains(recipe.modifierName()))
                .collect(Collectors.toMap(
                        DemolitionModifierRecipe::modifierName,
                        DemolitionModifierRecipe::incompatibleWith,
                        (a, b) -> a
                ));
        Map<String, List<String>> tempModifiers = new HashMap<>(allModifiers);

        List<String> modifiers = new ArrayList<>();

        for (int i = 0; i < numModifiers && !allModifiers.isEmpty(); i++) {
            while (!tempModifiers.isEmpty()) {
                String mod = new ArrayList<>(tempModifiers.keySet()).get(random.nextInt(tempModifiers.size()));
                List<String> incompatible = tempModifiers.get(mod);

                if (modifiers.stream().anyMatch(incompatible::contains) || defaultModifiers.stream().anyMatch(incompatible::contains)) {
                    tempModifiers.remove(mod);
                } else {
                    modifiers.add(mod);
                    allModifiers.remove(mod);
                    tempModifiers = new HashMap<>(allModifiers);
                    break;
                }
            }
            if (tempModifiers.isEmpty()) break;
        }

        stack.set(ModDataComponents.MODIFIERS.get(), modifiers);

        if (!(modifiers.contains("imbued") || modifiers.contains("laden")
                || defaultModifiers.contains("imbued") || defaultModifiers.contains("laden"))) return stack;

        List<? extends Holder<Potion>> validPotions = BuiltInRegistries.POTION.holders()
                .filter(h -> !h.value().getEffects().isEmpty())
                .toList();

        Holder<Potion> randomPotion = validPotions.get(random.nextInt(validPotions.size()));
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(randomPotion));

        return stack;
    }
}
