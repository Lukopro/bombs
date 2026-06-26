package net.luko.bombs.network;

import net.luko.bombs.Bombs;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPackets {
    private static int packetId = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "main"),
            () -> "1.0", s -> true, s -> true
    );

    public static void register() {
        CHANNEL.registerMessage(packetId++, ModifiersPacket.class,
                ModifiersPacket::encode,
                ModifiersPacket::decode,
                ModifiersPacket::handle);
    }
}
