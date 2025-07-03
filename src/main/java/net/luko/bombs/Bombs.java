package net.luko.bombs;

import net.luko.bombs.block.ModBlocks;
import net.luko.bombs.block.entity.ModBlockEntities;
import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.data.ModManagers;
import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.item.BombItem;
import net.luko.bombs.item.ModCreativeModeTabs;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.recipe.ModRecipeSerializers;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.screen.ModMenuTypes;
import net.luko.bombs.util.BombConfigSync;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Mod(Bombs.MODID)
public class Bombs
{
    public static final String MODID = "bombs";
    public static final Logger LOGGER = LoggerFactory.getLogger("Bombs");

    public Bombs(IEventBus modEventBus, ModContainer modContainer)
    {
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

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(BombConfigSync::syncBombExplosionPowers);
        event.enqueueWork(() -> DispenserBlock.registerBehavior(ModItems.DYNAMITE.get(), new DefaultDispenseItemBehavior(){
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack){
                if(!(stack.getItem() instanceof BombItem bomb)) return stack;

                bomb.throwBomb(source.level(), source, stack);
                return stack;
            }
        }));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @EventBusSubscriber
    public class ModReloadListenerRegistry{
        private static final List<PreparableReloadListener> LISTENERS = new ArrayList<>();

        public static void register(PreparableReloadListener listener){
            LISTENERS.add(listener);
        }

        @SubscribeEvent
        public static void onReload(AddReloadListenerEvent event){
            for(PreparableReloadListener listener : LISTENERS){
                event.addListener(listener);
            }
        }
    }
}
