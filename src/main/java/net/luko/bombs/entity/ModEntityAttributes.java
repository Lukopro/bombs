package net.luko.bombs.entity;

import net.luko.bombs.Bombs;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Bombs.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event){
        event.put(ModEntities.PROSPECTOR.get(), ProspectorEntity.createAttributes().build());
        event.put(ModEntities.HONSE.get(), HonseEntity.createAttributes().build());
    }
}
