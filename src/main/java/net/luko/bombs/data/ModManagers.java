package net.luko.bombs.data;


import net.luko.bombs.Bombs;
import net.luko.bombs.data.modifiers.PriorityManager;
import net.luko.bombs.data.themes.ThemeManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.ArrayList;
import java.util.List;

public class ModManagers {
    public static void init(){
        ModReloadListenerRegistry.register(ThemeManager.INSTANCE);
        ModReloadListenerRegistry.register(PriorityManager.INSTANCE);
    }

    @EventBusSubscriber(modid = Bombs.MODID, bus = EventBusSubscriber.Bus.GAME)
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

