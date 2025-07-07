package net.luko.bombs.data;

import net.luko.bombs.Bombs;
import net.luko.bombs.data.modifiers.ModifierColorManager;
import net.luko.bombs.data.modifiers.ModifierPriorityManager;
import net.luko.bombs.data.themes.ThemeManager;

public class ModManagers {
    public static void init(){
        Bombs.ModReloadListenerRegistry.register(ThemeManager.INSTANCE);
        Bombs.ModReloadListenerRegistry.register(ModifierColorManager.INSTANCE);
        Bombs.ModReloadListenerRegistry.register(ModifierPriorityManager.INSTANCE);
    }
}
