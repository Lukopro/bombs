package net.luko.bombs.block.entity;

import net.luko.bombs.Bombs;
import net.luko.bombs.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Bombs.MODID);

    public static final Supplier<BlockEntityType<DemolitionTableBlockEntity>> DEMOLITION_TABLE_BE =
            BLOCK_ENTITIES.register("demolition_table_be",
                    () -> BlockEntityType.Builder.of(DemolitionTableBlockEntity::new,
                            ModBlocks.DEMOLITION_TABLE.get()).build(null));

    public static void  register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
