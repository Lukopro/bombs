package net.luko.bombs;

import net.luko.bombs.block.ModBlocks;
import net.luko.bombs.block.entity.ModBlockEntities;
import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.data.ModManagers;
import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.item.bomb.BombItem;
import net.luko.bombs.item.ModCreativeModeTabs;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.recipe.ModRecipeSerializers;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.screen.ModMenuTypes;
import net.luko.bombs.util.BombConfigSync;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod(Bombs.MODID)
public class Bombs
{
    public static final String MODID = "bombs";
    public static final Logger LOGGER = LoggerFactory.getLogger("Bombs");

    public Bombs(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, BombsConfig.COMMON_CONFIG);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);
        ModDataComponents.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        ModManagers.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(BombConfigSync::syncBombExplosionPowers);
        event.enqueueWork(() -> {
            List<Item> bombItems = List.of(
                    ModItems.DYNAMITE.get(),
                    ModItems.GRENADE.get()
            );

            DefaultDispenseItemBehavior bombBehavior = new DefaultDispenseItemBehavior(){
                @Override
                protected ItemStack execute(BlockSource source, ItemStack stack){
                    if(!(stack.getItem() instanceof BombItem bomb)) return stack;
                    bomb.throwBomb(source.level(), source, stack);
                    return stack;
                }
            };

            for(Item item : bombItems){
                DispenserBlock.registerBehavior(item, bombBehavior);
            }
        });
    }
}
