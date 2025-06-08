package net.luko.bombs.block.entity;

import net.luko.bombs.Bombs;
import net.luko.bombs.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Bombs.MODID);

    public static final RegistryObject<BlockEntityType<DemolitionTableBlockEntity>> DEMOLITION_TABLE_BE =
            BLOCK_ENTITIES.register("demolition_table_be",
                    () -> BlockEntityType.Builder.of(DemolitionTableBlockEntity::new,
                            ModBlocks.DEMOLITION_TABLE.get()).build(null));

    public static void  register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
