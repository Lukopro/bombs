package net.luko.bombs.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

// Vanilla Explosion class was not adaptable enough, so I copied the entire thing and adapted it as I saw fit.
// Class and instance variables have an underscore _ to differentiate from super's variables.

public class CustomExplosion extends Explosion {
    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR_ = new ExplosionDamageCalculator();
    private final boolean fire_;
    private final Explosion.BlockInteraction blockInteraction_;
    private final RandomSource random_ = RandomSource.create();
    private final Level level_;
    private final double x_;
    private final double y_;
    private final double z_;
    @Nullable
    private final Entity source_;
    private final float radius_;
    private final DamageSource damageSource_;
    private final ExplosionDamageCalculator damageCalculator_;
    private final ObjectArrayList<BlockPos> toBlow_ = new ObjectArrayList<>();
    private final Map<Player, Vec3> hitPlayers_ = Maps.newHashMap();
    private final Vec3 position_;
    private final ItemStack stack;

    public CustomExplosion(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, List<BlockPos> pPositions, ItemStack stack) {
        this(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY, pPositions, stack);
    }

    public CustomExplosion(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction, List<BlockPos> pPositions, ItemStack stack) {
        this(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction, stack);
        this.toBlow_.addAll(pPositions);
    }

    public CustomExplosion(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction, ItemStack stack) {
        this(pLevel, pSource, (DamageSource)null, (ExplosionDamageCalculator)null, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction, stack);
    }

    public CustomExplosion(Level pLevel, @Nullable Entity pSource, @Nullable DamageSource pDamageSource, @Nullable ExplosionDamageCalculator pDamageCalculator, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction, ItemStack stack) {
        super(pLevel, pSource, pDamageSource, pDamageCalculator, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
        this.level_ = pLevel;
        this.source_ = pSource;
        this.radius_ = pRadius;
        this.x_ = pToBlowX;
        this.y_ = pToBlowY;
        this.z_ = pToBlowZ;
        this.fire_ = pFire;
        this.blockInteraction_ = pBlockInteraction;
        this.damageSource_ = pDamageSource == null ? pLevel.damageSources().explosion(this) : pDamageSource;
        this.damageCalculator_ = pDamageCalculator == null ? this.makeDamageCalculator(pSource) : pDamageCalculator;
        this.position_ = new Vec3(this.x_, this.y_, this.z_);
        this.stack = stack;
    }

    private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity pEntity) {
        return (ExplosionDamageCalculator)(pEntity == null ? EXPLOSION_DAMAGE_CALCULATOR_ : new EntityBasedExplosionDamageCalculator(pEntity));
    }

    public static float getSeenPercent(Vec3 pExplosionVector, Entity pEntity) {
        AABB aabb = pEntity.getBoundingBox();
        double d0 = 1.0D / ((aabb.maxX - aabb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((aabb.maxY - aabb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((aabb.maxZ - aabb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
        if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
            int i = 0;
            int j = 0;

            for(double d5 = 0.0D; d5 <= 1.0D; d5 += d0) {
                for(double d6 = 0.0D; d6 <= 1.0D; d6 += d1) {
                    for(double d7 = 0.0D; d7 <= 1.0D; d7 += d2) {
                        double d8 = Mth.lerp(d5, aabb.minX, aabb.maxX);
                        double d9 = Mth.lerp(d6, aabb.minY, aabb.maxY);
                        double d10 = Mth.lerp(d7, aabb.minZ, aabb.maxZ);
                        Vec3 vec3 = new Vec3(d8 + d3, d9, d10 + d4);
                        if (pEntity.level().clip(new ClipContext(vec3, pExplosionVector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, pEntity)).getType() == HitResult.Type.MISS) {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float)i / (float)j;
        } else {
            return 0.0F;
        }
    }

    /**
     * Does the first part of the explosion (destroy blocks)
     */
    @Override
    public void explode() {
        this.level_.gameEvent(this.source_, GameEvent.EXPLODE, new Vec3(this.x_, this.y_, this.z_));
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
                for(int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius_ * (0.7F + this.level_.random.nextFloat() * 0.6F);
                        double d4 = this.x_;
                        double d6 = this.y_;
                        double d8 = this.z_;

                        for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            BlockState blockstate = this.level_.getBlockState(blockpos);
                            FluidState fluidstate = this.level_.getFluidState(blockpos);
                            if (!this.level_.isInWorldBounds(blockpos)) {
                                break;
                            }

                            Optional<Float> optional = this.damageCalculator_.getBlockExplosionResistance(this, this.level_, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.damageCalculator_.shouldBlockExplode(this, this.level_, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            }

                            d4 += d0 * (double)0.3F;
                            d6 += d1 * (double)0.3F;
                            d8 += d2 * (double)0.3F;
                        }
                    }
                }
            }
        }

        this.toBlow_.addAll(set);
        float f2 = this.radius_ * 2.0F;
        int k1 = Mth.floor(this.x_ - (double)f2 - 1.0D);
        int l1 = Mth.floor(this.x_ + (double)f2 + 1.0D);
        int i2 = Mth.floor(this.y_ - (double)f2 - 1.0D);
        int i1 = Mth.floor(this.y_ + (double)f2 + 1.0D);
        int j2 = Mth.floor(this.z_ - (double)f2 - 1.0D);
        int j1 = Mth.floor(this.z_ + (double)f2 + 1.0D);
        List<Entity> list = this.level_.getEntities(this.source_, new AABB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.level_, this, list, f2);
        Vec3 vec3 = new Vec3(this.x_, this.y_, this.z_);

        for(int k2 = 0; k2 < list.size(); ++k2) {
            Entity entity = list.get(k2);
            if (!entity.ignoreExplosion()) {
                double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double)f2;
                if (d12 <= 1.0D) {
                    double d5 = entity.getX() - this.x_;
                    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y_;
                    double d9 = entity.getZ() - this.z_;
                    double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d13 != 0.0D) {
                        d5 /= d13;
                        d7 /= d13;
                        d9 /= d13;
                        double d14 = (double)getSeenPercent(vec3, entity);
                        double d10 = (1.0D - d12) * d14;

                        // Modifier adaptation
                        if(!BombModifierUtil.hasModifier(this.stack, "pacified")) {
                            entity.hurt(this.getDamageSource(), (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D)));
                        }

                        double d11;
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity)entity;
                            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, d10);
                        } else {
                            d11 = d10;
                        }

                        d5 *= d11;
                        d7 *= d11;
                        d9 *= d11;
                        Vec3 vec31 = new Vec3(d5, d7, d9);

                        // Modifier adaptation
                        if(!BombModifierUtil.hasModifier(this.stack, "dampened")) {
                            entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
                        }

                        if (entity instanceof Player) {
                            Player player = (Player)entity;
                            if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                this.hitPlayers_.put(player, vec31);
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
    public void finalizeExplosion(boolean pSpawnParticles) {
        if (this.level_.isClientSide) {
            this.level_.playLocalSound(this.x_, this.y_, this.z_, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level_.random.nextFloat() - this.level_.random.nextFloat()) * 0.2F) * 0.7F, false);
        }

        boolean flag = this.interactsWithBlocks();
        if (pSpawnParticles) {
            if (!(this.radius_ < 2.0F) && flag) {
                this.level_.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x_, this.y_, this.z_, 1.0D, 0.0D, 0.0D);
            } else {
                this.level_.addParticle(ParticleTypes.EXPLOSION, this.x_, this.y_, this.z_, 1.0D, 0.0D, 0.0D);
            }
        }

        if (flag) {
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
            boolean flag1 = this.getIndirectSourceEntity() instanceof Player;
            Util.shuffle(this.toBlow_, this.level_.random);

            for(BlockPos blockpos : this.toBlow_) {
                BlockState blockstate = this.level_.getBlockState(blockpos);
                Block block = blockstate.getBlock();
                if (!blockstate.isAir()) {
                    BlockPos blockpos1 = blockpos.immutable();
                    this.level_.getProfiler().push("explosion_blocks");
                    if (blockstate.canDropFromExplosion(this.level_, blockpos, this)) {
                        Level $$9 = this.level_;
                        if ($$9 instanceof ServerLevel) {
                            ServerLevel serverlevel = (ServerLevel)$$9;
                            BlockEntity blockentity = blockstate.hasBlockEntity() ? this.level_.getBlockEntity(blockpos) : null;
                            LootParams.Builder lootparams$builder = (new LootParams.Builder(serverlevel)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source_);
                            if (this.blockInteraction_ == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                                lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius_);
                            }

                            blockstate.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY, flag1);
                            blockstate.getDrops(lootparams$builder).forEach((p_46074_) -> {
                                addBlockDrops(objectarraylist, p_46074_, blockpos1);
                            });
                        }
                    }

                    blockstate.onBlockExploded(this.level_, blockpos, this);
                    this.level_.getProfiler().pop();
                }
            }

            for(Pair<ItemStack, BlockPos> pair : objectarraylist) {
                Block.popResource(this.level_, pair.getSecond(), pair.getFirst());
            }
        }

        if (this.fire_) {
            for(BlockPos blockpos2 : this.toBlow_) {
                if (this.random_.nextInt(3) == 0 && this.level_.getBlockState(blockpos2).isAir() && this.level_.getBlockState(blockpos2.below()).isSolidRender(this.level_, blockpos2.below())) {
                    this.level_.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level_, blockpos2));
                }
            }
        }

    }

    public boolean interactsWithBlocks() {
        return this.blockInteraction_ != Explosion.BlockInteraction.KEEP;
    }

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> pDropPositionArray, ItemStack pStack, BlockPos pPos) {
        int i = pDropPositionArray.size();

        for(int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = pDropPositionArray.get(j);
            ItemStack itemstack = pair.getFirst();
            if (ItemEntity.areMergable(itemstack, pStack)) {
                ItemStack itemstack1 = ItemEntity.merge(itemstack, pStack, 16);
                pDropPositionArray.set(j, Pair.of(itemstack1, pair.getSecond()));
                if (pStack.isEmpty()) {
                    return;
                }
            }
        }

        pDropPositionArray.add(Pair.of(pStack, pPos));
    }

    public DamageSource getDamageSource() {
        return this.damageSource_;
    }

    public Map<Player, Vec3> getHitPlayers() {
        return this.hitPlayers_;
    }

    @Nullable
    public LivingEntity getIndirectSourceEntity() {
        if (this.source_ == null) {
            return null;
        } else {
            Entity entity = this.source_;
            if (entity instanceof PrimedTnt) {
                PrimedTnt primedtnt = (PrimedTnt)entity;
                return primedtnt.getOwner();
            } else {
                entity = this.source_;
                if (entity instanceof LivingEntity) {
                    LivingEntity livingentity = (LivingEntity)entity;
                    return livingentity;
                } else {
                    entity = this.source_;
                    if (entity instanceof Projectile) {
                        Projectile projectile = (Projectile)entity;
                        entity = projectile.getOwner();
                        if (entity instanceof LivingEntity) {
                            return (LivingEntity)entity;
                        }
                    }

                    return null;
                }
            }
        }
    }

    /**
     * Returns either the entity that placed the explosive block, the entity that caused the explosion or null.
     */
    @Nullable
    public Entity getDirectSourceEntity() {
        return this.source_;
    }

    public void clearToBlow() {
        this.toBlow_.clear();
    }

    public List<BlockPos> getToBlow() {
        return this.toBlow_;
    }

    public Vec3 getPosition() {
        return this.position_;
    }

    @Nullable
    public Entity getExploder() {
        return this.source_;
    }

    public static enum BlockInteraction {
        KEEP,
        DESTROY,
        DESTROY_WITH_DECAY;
    }
}
