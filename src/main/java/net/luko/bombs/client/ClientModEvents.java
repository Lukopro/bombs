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
import net.luko.bombs.screen.DemolitionTableScreen;
import net.luko.bombs.screen.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Bombs.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event){
        event.register(BombTooltipItemIcons.ModifierTooltipComponent.class, BombTooltipItemIcons.ClientModifierTooltipComponent::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(BombsClient::init);
    }

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event){
        event.register(ModMenuTypes.DEMOLITION_TABLE_MENU.get(), DemolitionTableScreen::new);
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
}
