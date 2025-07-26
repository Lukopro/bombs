package net.luko.bombs.entity.bomb;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import java.util.Optional;


public abstract class ThrownBombEntity extends ThrowableItemProjectile implements IEntityWithComplexSpawn {
    protected float explosionPower;
    protected float randomSideTilt;
    protected float initialForwardTilt;
    protected float randomSpinSpeed;

    public static final float DEFAULT_POWER = 1.5F;

    public static final float RANDOM_SIDE_TILT_MAX = 20.0F;
    public static final float RANDOM_FORWARD_TILT_MAX = 20.0F;
    public static final float RANDOM_SPIN_SPEED_MIN = 15.0F;
    public static final float RANDOM_SPIN_SPEED_MAX = 25.0F;

    public float lastParticleTick;
    public float particlesToSpawn;

    protected Boolean hasHydrosensitiveModifier = null;

    public ThrownBombEntity(EntityType<? extends ThrownBombEntity> type, Level level) {
        this(type, level, DEFAULT_POWER);
    }

    public ThrownBombEntity(EntityType<? extends ThrownBombEntity> type, Level level, float explosionPower) {
        super(type, level);
        this.explosionPower = explosionPower;

        this.randomSideTilt = RANDOM_SIDE_TILT_MAX * (float)Math.random();
        this.initialForwardTilt = RANDOM_FORWARD_TILT_MAX * (float)Math.random();
        this.randomSpinSpeed = RANDOM_SPIN_SPEED_MIN + (RANDOM_SPIN_SPEED_MAX - RANDOM_SPIN_SPEED_MIN) * (float)Math.random();

        this.lastParticleTick = this.tickCount;
        this.particlesToSpawn = 0.0F;
    }

    public ThrownBombEntity(EntityType<? extends ThrownBombEntity> type, Level level, LivingEntity thrower, float explosionPower){
        super(type, thrower, level);

        this.explosionPower = explosionPower;
        if(BombModifierUtil.hasModifier(getItem(), "golden")){
            this.explosionPower += 0.5F;
        }

        this.randomSideTilt = RANDOM_SIDE_TILT_MAX * (float)Math.random();
        float randomForwardTilt = RANDOM_FORWARD_TILT_MAX * (float)Math.random();
        float throwerXRot = thrower.getXRot();

        this.initialForwardTilt = (ThrownBombEntity.RANDOM_FORWARD_TILT_MAX / 2) + randomForwardTilt + throwerXRot - 20.0F;
        this.randomSpinSpeed = RANDOM_SPIN_SPEED_MIN + (RANDOM_SPIN_SPEED_MAX - RANDOM_SPIN_SPEED_MIN) * (float)Math.random();
    }

    protected static int getTickLife(){
        return BombsConfig.BOMB_TIMEOUT_TIME.get();
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
    public double getDefaultGravity(){
        float gravity = getBaseGravity();
        if(BombModifierUtil.hasModifier(getItem(), "float")) gravity /= 3;
        if(BombModifierUtil.hasModifier(getItem(), "sink")) gravity *= 3;
        return gravity;
    }

    public abstract float getBaseGravity();

    protected boolean hasHydrosensitiveModifier(){
        if(hasHydrosensitiveModifier == null){
            hasHydrosensitiveModifier = BombModifierUtil.hasModifier(getItem(), "hydrosensitive");
        }
        return hasHydrosensitiveModifier;
    }

    @Override
    public void tick(){
        super.tick();
        if(tickCount % 40 == 0 && tickCount >= getTickLife()) discard();
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

    protected void explode(){
        BombExplosion explosion = new BombExplosion(
                level(),
                this,
                null,
                new ModifierExplosionDamageCalculator(getItem()),
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

    protected Explosion.BlockInteraction getBlockInteraction(ItemStack stack){
        if(BombModifierUtil.hasModifier(stack, "contained")){
            return Explosion.BlockInteraction.KEEP;
        }
        return Explosion.BlockInteraction.DESTROY;
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {
        buf.writeFloat(this.explosionPower);
        buf.writeFloat(this.randomSideTilt);
        buf.writeFloat(this.initialForwardTilt);
        buf.writeFloat(this.randomSpinSpeed);
        buf.writeWithCodec(NbtOps.INSTANCE, ItemStack.OPTIONAL_CODEC, this.getItem());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {
        this.explosionPower = buf.readFloat();
        this.randomSideTilt = buf.readFloat();
        this.initialForwardTilt = buf.readFloat();
        this.randomSpinSpeed = buf.readFloat();
        this.setItem(buf.readWithCodec(NbtOps.INSTANCE, ItemStack.OPTIONAL_CODEC, NbtAccounter.unlimitedHeap()));
    }

    protected class ModifierExplosionDamageCalculator extends ExplosionDamageCalculator{
        protected final boolean shatter;
        protected final boolean evaporate;

        protected Object2FloatOpenHashMap<Block> cachedBlockResistanceValues;
        protected Object2FloatOpenHashMap<Fluid> cachedFluidResistanceValues;

        public ModifierExplosionDamageCalculator(ItemStack stack){
            this.shatter = BombModifierUtil.hasModifier(stack, "shatter");
            this.evaporate = BombModifierUtil.hasModifier(stack, "evaporate");
            this.cachedBlockResistanceValues = new Object2FloatOpenHashMap<>();
            this.cachedFluidResistanceValues = new Object2FloatOpenHashMap<>();
        }

        @Override
        public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid) {

            float blockResistance = hasStaticExplosionResistance(state.getBlock())
                    ? cachedBlockResistanceValues.computeIfAbsent(state.getBlock(),
                    b -> state.getExplosionResistance(reader, pos, explosion))
                    : state.getExplosionResistance(reader, pos, explosion);

            float fluidResistance = evaporate || fluid.getType() == Fluids.EMPTY
                    ? 0.0F
                    : cachedFluidResistanceValues.computeIfAbsent(fluid.getType(),
                    f -> fluid.getExplosionResistance(reader, pos, explosion));

            if(shatter) blockResistance *= 0.4F;

            return Optional.of(Math.max(blockResistance, fluidResistance));
        }

        protected static boolean hasStaticExplosionResistance(Block block){
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            return id.getNamespace().equals("minecraft");
        }
    }
}
