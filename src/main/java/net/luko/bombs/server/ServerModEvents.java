package net.luko.bombs.server;

import net.luko.bombs.Bombs;
import net.luko.bombs.data.modifiers.ModifierManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Bombs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerModEvents {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        ModifierManager.INSTANCE.syncToPlayer(player);
    }
}
