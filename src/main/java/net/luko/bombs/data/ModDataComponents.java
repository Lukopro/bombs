package net.luko.bombs.data;

import com.mojang.serialization.Codec;
import net.luko.bombs.Bombs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Bombs.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TIER =
            DATA_COMPONENTS.register("tier", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<String>>> MODIFIERS =
            DATA_COMPONENTS.register("modifiers", () ->
                    new DataComponentType.Builder<List<String>>()
                            .persistent(Codec.STRING.listOf())
                            .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(Codec.STRING.listOf()))
                            .build());

    public static void register(IEventBus eventBus){
        DATA_COMPONENTS.register(eventBus);
    }
}
