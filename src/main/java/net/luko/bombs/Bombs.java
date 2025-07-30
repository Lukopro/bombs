package net.luko.bombs;

import net.luko.bombs.block.ModBlocks;
import net.luko.bombs.block.entity.ModBlockEntities;
import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.data.ModManagers;
import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.item.bomb.BombItem;
import net.luko.bombs.item.ModCreativeModeTabs;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.recipe.ModRecipeSerializers;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.screen.ModMenuTypes;
import net.luko.bombs.util.BombConfigSync;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod(Bombs.MODID)
public class Bombs
{
    public static final String MODID = "bombs";
    public static final Logger LOGGER = LoggerFactory.getLogger("Bombs");

    public Bombs(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.COMMON, BombsConfig.COMMON_CONFIG);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        ModManagers.init();

        modEventBus.addListener(this::commonSetup);
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
                    bomb.throwBomb(source.getLevel(), source, stack);
                    return stack;
                }
            };

            for(Item item : bombItems){
                DispenserBlock.registerBehavior(item, bombBehavior);
            }
        });
    }
}
