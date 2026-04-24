package net.luko.bombs.network;

import com.mojang.serialization.Codec;
import net.luko.bombs.Bombs;
import net.luko.bombs.client.data.ClientModifierStore;
import net.luko.bombs.data.modifiers.Modifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record ModifiersPacket(Map<String, Modifier> data) implements CustomPacketPayload {
    public static final Type<ModifiersPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "modifiers"));

    public static final StreamCodec<FriendlyByteBuf, ModifiersPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(Codec.unboundedMap(Codec.STRING, Modifier.CODEC)),
                    ModifiersPacket::data,
                    ModifiersPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ModifiersPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientModifierStore.setModifiers(packet.data);
        });
    }
}
