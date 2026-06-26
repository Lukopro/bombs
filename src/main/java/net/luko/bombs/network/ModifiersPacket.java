package net.luko.bombs.network;

import net.luko.bombs.data.modifiers.ColorManager;
import net.luko.bombs.data.modifiers.IconManager;
import net.luko.bombs.data.modifiers.PriorityManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import javax.swing.*;
import java.nio.file.WatchEvent;
import java.util.Map;
import java.util.function.Supplier;

public class ModifiersPacket {
    private final Map<String, TextColor> colorData;
    private final Map<String, ResourceLocation> iconData;
    private final Map<String, Integer> priorityData;

    public ModifiersPacket(Map<String, TextColor> colorData,
                           Map<String, ResourceLocation> iconData,
                           Map<String, Integer> priorityData) {
        this.colorData = colorData;
        this.iconData = iconData;
        this.priorityData = priorityData;
    }

    public static void encode(ModifiersPacket packet, FriendlyByteBuf buf) {
        ColorManager.Serializer.encode(packet.colorData, buf);
        IconManager.Serializer.encode(packet.iconData, buf);
        PriorityManager.Serializer.encode(packet.priorityData, buf);
    }

    public static ModifiersPacket decode(FriendlyByteBuf buf) {
        return new ModifiersPacket(
                ColorManager.Serializer.decode(buf),
                IconManager.Serializer.decode(buf),
                PriorityManager.Serializer.decode(buf)
        );
    }

    public static void handle(ModifiersPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ColorManager.INSTANCE.set(packet.colorData);
            IconManager.INSTANCE.set(packet.iconData);
            PriorityManager.INSTANCE.set(packet.priorityData);
        });
        context.setPacketHandled(true);
    }
}
