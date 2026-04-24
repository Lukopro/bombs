package net.luko.bombs.server;

import net.luko.bombs.Bombs;
import net.luko.bombs.data.modifiers.ModifierManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Bombs.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ServerGameEvents {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        ModifierManager.INSTANCE.syncToPlayer(player);
    }
}
