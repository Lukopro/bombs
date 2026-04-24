package net.luko.bombs.network;

import net.luko.bombs.client.data.ClientModifierStore;
import net.luko.bombs.data.modifiers.Modifier;
import net.luko.bombs.data.modifiers.ModifierSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModifiersPacket {
    private final Map<String, Modifier> data;

    public ModifiersPacket(Map<String, Modifier> data) {
        this.data = data;
    }

    public static void encode(ModifiersPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.data.size());
        for (var entry : packet.data.entrySet()) {
            buf.writeUtf(entry.getKey());
            ModifierSerializer.write(buf, entry.getValue());
        }
    }

    public static ModifiersPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Map<String, Modifier> data = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String s = buf.readUtf(32767);
            Modifier m = ModifierSerializer.read(buf);
            data.put(s, m);
        }
        return new ModifiersPacket(data);
    }

    public static void handle(ModifiersPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientModifierStore.setModifiers(packet.data);
        });
        context.setPacketHandled(true);
    }
}
