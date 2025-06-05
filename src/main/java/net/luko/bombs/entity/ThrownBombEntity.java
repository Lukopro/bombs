package net.luko.bombs.entity;

import net.luko.bombs.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;


public class ThrownBombEntity extends ThrowableItemProjectile {
    private float explosionPower;
    private float randomTilt;

    public static final float DEFAULT_POWER = 1.5F;
    public static final float RANDOM_TILT_MAX = 20.0F;

    public ThrownBombEntity(EntityType<? extends ThrownBombEntity> type, Level level) {
        super(type, level);
        this.explosionPower = DEFAULT_POWER;
        this.randomTilt = RANDOM_TILT_MAX * (float)Math.random();
    }

    public ThrownBombEntity(EntityType<? extends ThrownBombEntity> type, Level level, LivingEntity thrower, float explosionPower){
        super(type, thrower, level);
        this.explosionPower = explosionPower;
        this.randomTilt = RANDOM_TILT_MAX * (float)Math.random();
    }

    @Override
    protected Item getDefaultItem(){
        return ModItems.DYNAMITE.get();
    }

    public float getRandomTilt(){
        return this.randomTilt;
    }

    @Override
    protected void onHitEntity(EntityHitResult result){
        if(!level().isClientSide()){
            explode();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result){
        if(!level().isClientSide()){
            explode();
        }
        super.onHitBlock(result);
    }

    private void explode(){
        level().explode(this, this.getX(), this.getY(), this.getZ(), explosionPower, Level.ExplosionInteraction.TNT);
        discard();
    }
}
