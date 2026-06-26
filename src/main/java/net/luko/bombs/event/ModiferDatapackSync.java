package net.luko.bombs.event;

import net.luko.bombs.Bombs;
import net.luko.bombs.data.modifiers.ColorManager;
import net.luko.bombs.data.modifiers.IconManager;
import net.luko.bombs.data.modifiers.PriorityManager;
import net.luko.bombs.network.ModPackets;
import net.luko.bombs.network.ModifiersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Bombs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModiferDatapackSync {

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player != null) {
            ModPackets.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new ModifiersPacket(
                            ColorManager.INSTANCE.getColors(),
                            IconManager.INSTANCE.getIcons(),
                            PriorityManager.INSTANCE.getPriorities()
                    )
            );
        } else {
            ModPackets.CHANNEL.send(
                    PacketDistributor.ALL.noArg(),
                    new ModifiersPacket(
                            ColorManager.INSTANCE.getColors(),
                            IconManager.INSTANCE.getIcons(),
                            PriorityManager.INSTANCE.getPriorities()
                    )
            );
        }
    }
}
