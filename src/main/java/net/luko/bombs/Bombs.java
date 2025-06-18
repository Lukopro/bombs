package net.luko.bombs;

import net.luko.bombs.block.ModBlocks;
import net.luko.bombs.block.entity.ModBlockEntities;
import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.item.ModCreativeModeTabs;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.recipe.ModRecipeSerializers;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.screen.ModMenuTypes;
import net.luko.bombs.util.BombConfigSync;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Bombs.MODID)
public class Bombs
{
    public static final String MODID = "bombs";
    public static final Logger LOGGER = LoggerFactory.getLogger("Bombs");

    public Bombs(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BombsConfig.COMMON_CONFIG);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(BombConfigSync::syncBombExplosionPowers);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }
}
