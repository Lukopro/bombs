package net.luko.bombs.entity;

import net.luko.bombs.item.ModItems;
import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;


public class ThrownBombEntity extends ThrowableItemProjectile implements IEntityAdditionalSpawnData {
    private float explosionPower;
    private float randomSideTilt;
    private float initialForwardTilt;
    private float randomSpinSpeed;

    public static final float DEFAULT_POWER = 1.5F;

    public static final float RANDOM_SIDE_TILT_MAX = 20.0F;
    public static final float RANDOM_FORWARD_TILT_MAX = 20.0F;
    public static final float RANDOM_SPIN_SPEED_MIN = 15.0F;
    public static final float RANDOM_SPIN_SPEED_MAX = 25.0F;

    public final int tickLife;

    public ThrownBombEntity(EntityType<? extends ThrownBombEntity> type, Level level) {
        this(type, level, DEFAULT_POWER);
    }

    public ThrownBombEntity(EntityType<? extends ThrownBombEntity> type, Level level, float explosionPower) {
        super(type, level);
        this.explosionPower = explosionPower;

        this.randomSideTilt = RANDOM_SIDE_TILT_MAX * (float)Math.random();
        this.initialForwardTilt = RANDOM_FORWARD_TILT_MAX * (float)Math.random();
        this.randomSpinSpeed = RANDOM_SPIN_SPEED_MIN + (RANDOM_SPIN_SPEED_MAX - RANDOM_SPIN_SPEED_MIN) * (float)Math.random();

        this.tickLife = 1200;
    }

    public ThrownBombEntity(EntityType<? extends ThrownBombEntity> type, Level level, LivingEntity thrower, float explosionPower){
        super(type, thrower, level);

        this.explosionPower = explosionPower;


        this.randomSideTilt = RANDOM_SIDE_TILT_MAX * (float)Math.random();

        float randomForwardTilt = RANDOM_FORWARD_TILT_MAX * (float)Math.random();
        float throwerXRot = thrower.getXRot();
        this.initialForwardTilt = (ThrownBombEntity.RANDOM_FORWARD_TILT_MAX / 2) + randomForwardTilt + throwerXRot - 20.0F;
        this.randomSpinSpeed = RANDOM_SPIN_SPEED_MIN + (RANDOM_SPIN_SPEED_MAX - RANDOM_SPIN_SPEED_MIN) * (float)Math.random();

        this.tickLife = 1200;
    }

    @Override
    protected Item getDefaultItem(){
        return ModItems.DYNAMITE.get();
    }

    public float getRandomSideTilt(){
        return this.randomSideTilt;
    }

    public float getInitialForwardTilt(){
        return this.initialForwardTilt;
    }

    public float getRandomSpinSpeed(){
        return this.randomSpinSpeed;
    }

    @Override
    public float getGravity(){
        float gravity = 0.03F;
        if(BombModifierUtil.hasModifier(getItem(), "float")) gravity /= 3;
        if(BombModifierUtil.hasModifier(getItem(), "sink")) gravity *= 3;
        return gravity;
    }

    @Override
    public void tick(){
        super.tick();
        if(tickCount % 40 == 0 && tickCount >= this.tickLife) discard();
    }

    @Override
    public boolean isPickable(){
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount){
        if(source.getDirectEntity() instanceof ThrownBombEntity) return false;
        if(!level().isClientSide()){
            explode();
        }
        super.hurt(source, amount);
        return false;
    }

    @Override
    protected void onHitEntity(EntityHitResult result){
        if(result.getEntity() instanceof ThrownBombEntity) return;
        if(!level().isClientSide()){
            explode();
        }
        super.onHitEntity(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result){
        if(!level().isClientSide()){
            explode();
        }
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
        explosion.explode();
        explosion.finalizeExplosion(true);
        discard();
    }

    private Explosion.BlockInteraction getBlockInteraction(ItemStack stack){
        if(BombModifierUtil.hasModifier(stack, "contained")){
            return Explosion.BlockInteraction.KEEP;
        }
        return Explosion.BlockInteraction.DESTROY;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buf) {
        buf.writeFloat(this.explosionPower);
        buf.writeFloat(this.randomSideTilt);
        buf.writeFloat(this.initialForwardTilt);
        buf.writeFloat(this.randomSpinSpeed);
        buf.writeItem(this.getItem());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        this.explosionPower = buf.readFloat();
        this.randomSideTilt = buf.readFloat();
        this.initialForwardTilt = buf.readFloat();
        this.randomSpinSpeed = buf.readFloat();
        this.setItem(buf.readItem());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(){
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private class ModifierExplosionDamageCalculator extends ExplosionDamageCalculator{
        private final ItemStack stack;

        public ModifierExplosionDamageCalculator(ExplosionDamageCalculator vanilla, ItemStack stack){
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
