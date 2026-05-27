package net.luko.bombs.event;

import net.luko.bombs.Bombs;
import net.luko.bombs.entity.villager.ModVillagerProfessions;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Bombs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModVillagerTrades {
    @SubscribeEvent
    public static void addTrades(VillagerTradesEvent event) {
        if (!(event.getType() == ModVillagerProfessions.DEMOLITIONIST.get())) return;

        List<VillagerTrades.ItemListing> level1 = event.getTrades().get(1);

        level1.add((trader, random) ->
                new MerchantOffer(
                        new ItemStack(Items.GUNPOWDER, 4),
                        new ItemStack(Items.EMERALD, 1),
                        16, 2, 0.05F
                )
        );
    }
}
