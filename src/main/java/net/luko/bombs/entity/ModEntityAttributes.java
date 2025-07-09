package net.luko.bombs.entity;

import net.luko.bombs.Bombs;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Bombs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event){
        event.put(ModEntities.PROSPECTOR.get(), ProspectorEntity.createAttributes().build());
        event.put(ModEntities.HONSE.get(), HonseEntity.createAttributes().build());
    }
}
