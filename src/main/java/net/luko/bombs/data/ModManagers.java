package net.luko.bombs.data;

import net.luko.bombs.Bombs;
import net.luko.bombs.data.modifiers.PriorityManager;
import net.luko.bombs.data.themes.ThemeManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

public class ModManagers {
    public static void init(){
        ModReloadListenerRegistry.register(ThemeManager.INSTANCE);
        ModReloadListenerRegistry.register(PriorityManager.INSTANCE);
    }

    @Mod.EventBusSubscriber(modid = Bombs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModReloadListenerRegistry{
        private static final List<PreparableReloadListener> LISTENERS = new ArrayList<>();

        public static void register(PreparableReloadListener listener){
            LISTENERS.add(listener);
        }

        @SubscribeEvent
        public static void onReload(AddReloadListenerEvent event){
            for(var listener : LISTENERS) event.addListener(listener);
        }
    }
}
