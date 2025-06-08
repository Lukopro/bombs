package net.luko.bombs.client;

import net.luko.bombs.Bombs;
import net.luko.bombs.client.model.DynamiteModel;
import net.luko.bombs.client.renderer.ThrownBombRenderer;
import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.screen.DemolitionTableScreen;
import net.luko.bombs.screen.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Bombs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        MenuScreens.register(ModMenuTypes.DEMOLITION_TABLE_MENU.get(), DemolitionTableScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(ModEntities.THROWN_BOMB.get(), ThrownBombRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(DynamiteModel.LAYER_LOCATION, DynamiteModel::createBodyLayer);
    }
}
