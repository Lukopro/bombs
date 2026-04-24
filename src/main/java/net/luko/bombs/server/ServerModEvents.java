package net.luko.bombs.server;

import net.luko.bombs.Bombs;
import net.luko.bombs.network.ModifiersPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Bombs.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ServerModEvents {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                ModifiersPacket.TYPE,
                ModifiersPacket.STREAM_CODEC,
                ModifiersPacket::handle
        );
    }
}
