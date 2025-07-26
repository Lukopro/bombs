package net.luko.bombs.client;

import net.luko.bombs.Bombs;
import net.luko.bombs.client.model.DynamiteModel;
import net.luko.bombs.client.model.GrenadeModel;
import net.luko.bombs.client.model.HonseModel;
import net.luko.bombs.client.model.ProspectorModel;
import net.luko.bombs.client.renderer.HonseRenderer;
import net.luko.bombs.client.renderer.ProspectorRenderer;
import net.luko.bombs.client.renderer.ThrownDynamiteRenderer;
import net.luko.bombs.client.renderer.ThrownGrenadeRenderer;
import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.screen.DemolitionTableScreen;
import net.luko.bombs.screen.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Bombs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        MenuScreens.register(ModMenuTypes.DEMOLITION_TABLE_MENU.get(), DemolitionTableScreen::new);
        event.enqueueWork(BombsClient::init);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(ModEntities.THROWN_DYNAMITE.get(), ThrownDynamiteRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWN_GRENADE.get(), ThrownGrenadeRenderer::new);
        event.registerEntityRenderer(ModEntities.PROSPECTOR.get(), ProspectorRenderer::new);
        event.registerEntityRenderer(ModEntities.HONSE.get(), HonseRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(DynamiteModel.LAYER_LOCATION, DynamiteModel::createBodyLayer);
        event.registerLayerDefinition(GrenadeModel.LAYER_LOCATION, GrenadeModel::createBodyLayer);
        event.registerLayerDefinition(ProspectorModel.LAYER_LOCATION, ProspectorModel::createBodyLayer);
        event.registerLayerDefinition(HonseModel.LAYER_LOCATION, HonseModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event){
        event.register(
                (stack, layer) -> ((ForgeSpawnEggItem) stack.getItem()).getColor(layer),
                ModItems.PROSPECTOR_SPAWN_EGG.get(),
                ModItems.HONSE_SPAWN_EGG.get()
        );
    }
}
