package net.luko.bombs.entity;

import net.luko.bombs.Bombs;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Optional;


public class ThrownBombEntity extends ThrowableItemProjectile {
    private float explosionPower;
    private float randomSideTilt;
    private float randomForwardTilt;
    private float randomSpinSpeed;

    public static final float DEFAULT_POWER = 1.5F;

    public static final float RANDOM_SIDE_TILT_MAX = 20.0F;
    public static final float RANDOM_FORWARD_TILT_MAX = 20.0F;
    public static final float RANDOM_SPIN_SPEED_MIN = 15.0F;
    public static final float RANDOM_SPIN_SPEED_MAX = 25.0F;

    public ThrownBombEntity(EntityType<? extends ThrownBombEntity> type, Level level) {
        super(type, level);
        this.explosionPower = DEFAULT_POWER;

        this.randomSideTilt = RANDOM_SIDE_TILT_MAX * (float)Math.random();
        this.randomForwardTilt = RANDOM_FORWARD_TILT_MAX * (float)Math.random();
        this.randomSpinSpeed = RANDOM_SPIN_SPEED_MIN + (RANDOM_SPIN_SPEED_MAX - RANDOM_SPIN_SPEED_MIN) * (float)Math.random();
    }

    public ThrownBombEntity(EntityType<? extends ThrownBombEntity> type, Level level, LivingEntity thrower, float explosionPower){
        super(type, thrower, level);

        this.explosionPower = explosionPower;
        if(BombModifierUtil.hasModifier(getItem(), "golden")){
            this.explosionPower += 0.5F;
        }

        this.randomSideTilt = RANDOM_SIDE_TILT_MAX * (float)Math.random();
        this.randomForwardTilt = RANDOM_FORWARD_TILT_MAX * (float)Math.random();
        this.randomSpinSpeed = RANDOM_SPIN_SPEED_MIN + (RANDOM_SPIN_SPEED_MAX - RANDOM_SPIN_SPEED_MIN) * (float)Math.random();
    }

    @Override
    protected Item getDefaultItem(){
        return ModItems.DYNAMITE.get();
    }

    public float getRandomSideTilt(){
        return this.randomSideTilt;
    }

    public float getRandomForwardTilt(){
        return this.randomForwardTilt;
    }

    public float getRandomSpinSpeed(){
        return this.randomSpinSpeed;
    }

    @Override
    protected void onHitEntity(EntityHitResult result){
        explode();
        super.onHitEntity(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result){
        explode();
        super.onHitBlock(result);
    }

    private void explode(){
        CustomExplosion explosion = new CustomExplosion(
                level(),
                this,
                null,
                new ModifierExplosionDamageCalculator(new ExplosionDamageCalculator(), getItem()),
                this.getX(),
                this.getY(),
                this.getZ(),
                explosionPower,
                BombModifierUtil.hasModifier(getItem(), "flame"),
                getBlockInteraction(getItem()),
                getItem());
        if(!level().isClientSide()) {
            explosion.explode();
        }
        explosion.finalizeExplosion(true);
        discard();
    }

    private Explosion.BlockInteraction getBlockInteraction(ItemStack stack){
        if(BombModifierUtil.hasModifier(stack, "contained")){
            return Explosion.BlockInteraction.KEEP;
        }
        return Explosion.BlockInteraction.DESTROY;
    }

    private class ModifierExplosionDamageCalculator extends ExplosionDamageCalculator{
        private final ExplosionDamageCalculator vanilla;
        private final ItemStack stack;

        public ModifierExplosionDamageCalculator(ExplosionDamageCalculator vanilla, ItemStack stack){
            this.vanilla = vanilla;
            this.stack = stack;
        }

        @Override
        public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid) {
            float blockResistance = state.getExplosionResistance(reader, pos, explosion);
            float fluidResistance = fluid.getExplosionResistance(reader, pos, explosion);

            if(BombModifierUtil.hasModifier(this.stack, "shatter")){
                blockResistance *= 0.4F;
            }

            if(BombModifierUtil.hasModifier(this.stack, "evaporate")){
                fluidResistance *= 0.0F;
            }
            return Optional.of(Math.max(blockResistance, fluidResistance));
        }
    }
}
