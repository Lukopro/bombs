package net.luko.bombs.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class ProspectorEntity extends PathfinderMob {
    protected ProspectorEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals(){

    }

    @Override
    protected SoundEvent getAmbientSound(){
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source){
        return SoundEvents.PILLAGER_HURT;
    }


}
