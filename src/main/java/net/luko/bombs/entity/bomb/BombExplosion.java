package net.luko.bombs.entity.bomb;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.*;
import javax.annotation.Nullable;

import net.luko.bombs.Bombs;
import net.luko.bombs.data.themes.ThemeData;
import net.luko.bombs.data.themes.ThemeManager;
import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.joml.Vector3f;

// Class and instance variables have an underscore _ to differentiate from super's variables.

public class BombExplosion extends Explosion {
    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR_ = new ExplosionDamageCalculator();
    private final boolean fire_;
    private static final float FIRE_THRESHOLD = 5.0F;
    private final Explosion.BlockInteraction blockInteraction_;
    private final RandomSource random_ = RandomSource.create();
    private final Level level_;
    private final double x_;
    private final double y_;
    private final double z_;
    @Nullable
    private final Entity source_;
    private final float radius_;
    private final int estimatedCubicSize;
    private final ExplosionDamageCalculator damageCalculator_;
    private final LongOpenHashSet mayIgnite;
    private final Long2FloatOpenHashMap almostBroke;
    Object2ObjectOpenHashMap<ItemMergeKey, ObjectArrayList<Drop>> drops;

    private final Map<Player, Vec3> hitPlayers_ = Maps.newHashMap();
    private final ItemStack stack;
    private final ThemeData themeData;
    private final float themeStrength;
    private final boolean hasTheme;

    private final boolean hasEvaporateModifier;
    private final boolean hasGentleModifier;
    private final boolean hasLethalModifier;
    private final boolean hasPacifiedModifier;
    private final boolean hasLadenModifier;
    private final boolean hasFrostModifier;
    private final boolean hasDampenedModifier;
    private final boolean hasShockwaveModifier;
    private final boolean hasImbuedModifier;
    private final float dropChance;
    private final int maxDropStackSize;

    public BombExplosion(Level pLevel, @Nullable Entity pSource, @Nullable DamageSource pDamageSource, @Nullable ExplosionDamageCalculator pDamageCalculator, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction, ItemStack stack) {
        super(pLevel, pSource, pDamageSource, pDamageCalculator, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
        this.level_ = pLevel;
        this.source_ = pSource;
        this.radius_ = pRadius;
        this.x_ = pToBlowX;
        this.y_ = pToBlowY;
        this.z_ = pToBlowZ;
        this.fire_ = pFire;
        this.blockInteraction_ = pBlockInteraction;
        this.damageCalculator_ = pDamageCalculator == null
                ? (pSource == null ? EXPLOSION_DAMAGE_CALCULATOR_ : new EntityBasedExplosionDamageCalculator(pSource))
                : pDamageCalculator;

        this.stack = stack;

        if(stack.hasTag() && stack.getTag().contains("Theme", Tag.TAG_STRING)){
            String themeId = stack.getTag().getString("Theme");
            ResourceLocation themeRL = ResourceLocation.tryParse(themeId.contains(":") ? themeId : Bombs.MODID + ":" + themeId);
            this.themeData = themeRL != null && ThemeManager.hasTheme(themeRL) ? ThemeManager.get(themeRL) : null;
        } else {
            this.themeData = null;
        }
        this.hasTheme = this.themeData != null;
        this.themeStrength = this.hasTheme ? this.themeData.getStrength() : 0.0F;

        this.hasEvaporateModifier = BombModifierUtil.hasModifier(stack, "evaporate");
        this.hasGentleModifier = BombModifierUtil.hasModifier(stack, "gentle");
        this.hasLethalModifier = BombModifierUtil.hasModifier(stack, "lethal");
        this.hasPacifiedModifier = BombModifierUtil.hasModifier(stack, "pacified");
        this.hasLadenModifier = BombModifierUtil.hasModifier(stack, "laden");
        this.hasFrostModifier = BombModifierUtil.hasModifier(stack, "frost");
        this.hasDampenedModifier = BombModifierUtil.hasModifier(stack, "dampened");
        this.hasShockwaveModifier = BombModifierUtil.hasModifier(stack, "shockwave");
        this.hasImbuedModifier = BombModifierUtil.hasModifier(stack, "imbued");
        this.dropChance = Math.min(1.0F, (float) Math.pow(0.95, (radius_ * 0.9) - 9));
        this.maxDropStackSize = 12 + 2 * Math.min(26, (int) radius_); // min 12, max 64

        estimatedCubicSize = (int)(radius_ * radius_ * radius_ * 2);
        mayIgnite = new LongOpenHashSet((int)(radius_ * radius_ * 2));
        almostBroke = new Long2FloatOpenHashMap(estimatedCubicSize);
        drops = new Object2ObjectOpenHashMap<>((int)(this.radius_));

        almostBroke.defaultReturnValue(Float.NEGATIVE_INFINITY);
    }

    // Find clips directly, avoids overhead from built-in functions
    public static float getSeenPercent(Vec3 pExplosionVector, Entity pEntity) {
        AABB aabb = pEntity.getBoundingBox();
        double d0 = 1.0D / ((aabb.maxX - aabb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((aabb.maxY - aabb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((aabb.maxZ - aabb.minZ) * 2.0D + 1.0D);
        if (d0 < 0D || d1 < 0D || d2 < 0D) return 0.0F;
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
        int i = 0;
        int j = 0;

        for(double d5 = 0.0D; d5 <= 1.0D; d5 += d0) {
            for(double d6 = 0.0D; d6 <= 1.0D; d6 += d1) {
                for(double d7 = 0.0D; d7 <= 1.0D; d7 += d2) {
                    double d8 = Mth.lerp(d5, aabb.minX, aabb.maxX);
                    double d9 = Mth.lerp(d6, aabb.minY, aabb.maxY);
                    double d10 = Mth.lerp(d7, aabb.minZ, aabb.maxZ);
                    Vec3 vec3 = new Vec3(d8 + d3, d9, d10 + d4);
                    if (isUnobstructed(vec3, pExplosionVector, pEntity.level())) {
                        ++i;
                    }

                    ++j;
                }
            }
        }

        return (float)i / (float)j;
    }

    private static boolean isUnobstructed(Vec3 from, Vec3 to, Level level) {
        if (from.equals(to)) return true;

        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;

        int x = Mth.floor(from.x);
        int y = Mth.floor(from.y);
        int z = Mth.floor(from.z);

        int endX = Mth.floor(to.x);
        int endY = Mth.floor(to.y);
        int endZ = Mth.floor(to.z);

        int stepX = endX > x ? 1 : -1;
        int stepY = endY > y ? 1 : -1;
        int stepZ = endZ > z ? 1 : -1;

        double tDeltaX = Math.abs(1D / dx);
        double tDeltaY = Math.abs(1D / dy);
        double tDeltaZ = Math.abs(1D / dz);

        double nextBoundaryX = stepX > 0 ? (x + 1D) : x;
        double nextBoundaryY = stepY > 0 ? (y + 1D) : y;
        double nextBoundaryZ = stepZ > 0 ? (z + 1D) : z;

        double tMaxX = (nextBoundaryX - from.x) / dx;
        double tMaxY = (nextBoundaryY - from.y) / dy;
        double tMaxZ = (nextBoundaryZ - from.z) / dz;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        double currentT = 0D;

        while (currentT <= 1.0) {
            pos.set(x, y, z);

            // step
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    x += stepX;
                    currentT = tMaxX;
                    tMaxX += tDeltaX;
                } else {
                    z += stepZ;
                    currentT = tMaxZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    y += stepY;
                    currentT = tMaxY;
                    tMaxY += tDeltaY;
                } else {
                    z += stepZ;
                    currentT = tMaxZ;
                    tMaxZ += tDeltaZ;
                }
            }

            BlockState state = level.getBlockState(pos);

            if (state.isAir()) continue;

            if (state.isCollisionShapeFullBlock(level, pos)) return false;

            VoxelShape shape = state.getCollisionShape(level, pos);

            BlockHitResult hit = shape.clip(from, to, pos);
            if (hit != null && hit.getType() != HitResult.Type.MISS) return false;
        }

        return true;
    }

    /**
     * Does the first part of the explosion (destroy blocks)
     */
    @Override
    public void explode() {
        this.level_.gameEvent(this.source_, GameEvent.EXPLODE, new Vec3(this.x_, this.y_, this.z_));

        this.affectEntities();
        this.affectBlocks();

        for (Map.Entry<Player, Vec3> entry : this.hitPlayers_.entrySet()){
            Player player = entry.getKey();
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        }
    }

    private void affectEntities() {
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

        for (Entity entity : list) {
            if (entity.ignoreExplosion()) continue;
            double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double) f2;

            if (d12 > 1.0D) continue;
            double d5 = entity.getX() - this.x_;
            double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y_;
            double d9 = entity.getZ() - this.z_;
            double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

            if (d13 == 0.0D) continue;
            d5 /= d13;
            d7 /= d13;
            d9 /= d13;
            double d14 = (double) BombExplosion.getSeenPercent(vec3, entity);
            double d10 = (1.0D - d12) * d14;

            // Modifier adaptation
            float damageAmount = (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D));
            if (!this.hasLethalModifier) {
                damageAmount *= 0.5F;
            }

            if ((entity instanceof ItemEntity || entity instanceof AbstractMinecart || entity instanceof Boat)) {
                if (!this.hasGentleModifier) {
                    entity.hurt(this.getDamageSource(), damageAmount);
                }
            } else if (!this.hasPacifiedModifier) {
                entity.hurt(this.getDamageSource(), damageAmount);
            }

            double d11;
            if (entity instanceof LivingEntity livingEntity) {
                d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingEntity, d10);

                if (this.hasLadenModifier) {
                    for (MobEffectInstance baseEffect : PotionUtils.getMobEffects(this.stack)) {
                        livingEntity.addEffect(new MobEffectInstance(
                                baseEffect.getEffect(),
                                (int)(baseEffect.getDuration() * (d10)),
                                baseEffect.getAmplifier(),
                                baseEffect.isAmbient(),
                                baseEffect.isVisible()
                        ));
                    }
                }

                if (this.hasFrostModifier) {
                    livingEntity.setTicksFrozen(livingEntity.getTicksFrozen() + (int) (30.0F * this.radius_ * d10));
                }

            } else {
                d11 = d10;
            }

            d5 *= d11;
            d7 *= d11;
            d9 *= d11;
            Vec3 vec31 = new Vec3(d5, d7, d9);

            // Modifier adaptation
            if (!this.hasDampenedModifier) {
                if (this.hasShockwaveModifier) {
                    vec31 = vec31.scale(2.0);
                }
                entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
            }

            if (entity instanceof Player player) {
                if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                    this.hitPlayers_.put(player, vec31);
                }
            }
        }
    }

    private void breakBlock(BlockState blockstate, BlockPos blockpos, boolean playerIndirectSourceEntityFlag) {
        if (blockstate.isAir()) return;
        if (blockstate.canDropFromExplosion(this.level_, blockpos, this)) {
            if (this.level_ instanceof ServerLevel serverlevel) {
                BlockEntity blockentity = blockstate.hasBlockEntity() ? this.level_.getBlockEntity(blockpos) : null;

                LootParams.Builder lootparams$builder = (new LootParams.Builder(serverlevel))
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos))
                        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                        .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity)
                        .withOptionalParameter(LootContextParams.THIS_ENTITY, this.source_);

                if (this.blockInteraction_ == BlockInteraction.DESTROY_WITH_DECAY) {
                    lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius_);
                }

                blockstate.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY, playerIndirectSourceEntityFlag);
                blockstate.getDrops(lootparams$builder).forEach((p_46074_) -> {
                    if (random_.nextFloat() < this.dropChance) addBlockDrops(drops, p_46074_, blockpos.immutable(), maxDropStackSize);
                });
            }
        }

        blockstate.onBlockExploded(this.level_, blockpos, this);
    }

    private void affectBlocks() {
        boolean interactsWithBlocksFlag = this.interactsWithBlocks();
        boolean playerIndirectSourceEntityFlag = this.getIndirectSourceEntity() instanceof Player;
        BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos();

        // store temporary voxel data as longs
        // bit 0 = true if the block was originally air
        // bit 1 = true if the block was broken earlier in ray traversal
        // bits 2-33 = float resistance value of block
        Long2LongOpenHashMap voxelCache = new Long2LongOpenHashMap(estimatedCubicSize);
        voxelCache.defaultReturnValue(Long.MIN_VALUE);


        // will likely refactor rays to split during traversal, notes are here for my future reference
        // d(r) = r * sqrt(4pi) * sqrt(1/N)
        // d(r) = 0.5
        // sqrt(1/N) = d(r) / (r * sqrt(4pi))
        // N = 1 / (0.5 / (r * sqrt(4pi)))^2
        int gridSize = 4 + (int)Math.floor(Math.pow(this.radius_, 1.45));
        int rayCount = gridSize * gridSize;
        double goldenAngle = Math.PI * (3.0D - Math.sqrt(5.0D));

        for (int ray = 0; ray < rayCount; ray++) {
            double d1 = 1.0D - (2.0D * ray) / (rayCount - 1);
            double radial = Math.sqrt(1.0D - d1 * d1);
            double theta = goldenAngle * ray;

            double d0 = Math.cos(theta) * radial;
            double d2 = Math.sin(theta) * radial;

            // axis-aligned rays should not step, so step = 0 is not required
            int stepX = d0 > 0 ? 1 : -1;
            int stepY = d1 > 0 ? 1 : -1;
            int stepZ = d2 > 0 ? 1 : -1;

            double tDeltaX = d0 == 0 ? Double.MAX_VALUE : Math.abs(1.0D / d0);
            double tDeltaY = d1 == 0 ? Double.MAX_VALUE : Math.abs(1.0D / d1);
            double tDeltaZ = d2 == 0 ? Double.MAX_VALUE : Math.abs(1.0D / d2);

            float f = this.radius_;// * (0.7F + this.level_.random.nextFloat() * 0.6F);
            int voxelX = Mth.floor(this.x_);
            int voxelY = Mth.floor(this.y_);
            int voxelZ = Mth.floor(this.z_);

            double nextBoundaryX = stepX > 0 ? voxelX + 1.0D : voxelX;
            double nextBoundaryY = stepY > 0 ? voxelY + 1.0D : voxelY;
            double nextBoundaryZ = stepZ > 0 ? voxelZ + 1.0D : voxelZ;

            double tMaxX = d0 == 0 ? Double.MAX_VALUE : (nextBoundaryX - this.x_) / d0;
            double tMaxY = d1 == 0 ? Double.MAX_VALUE : (nextBoundaryY - this.y_) / d1;
            double tMaxZ = d2 == 0 ? Double.MAX_VALUE : (nextBoundaryZ - this.z_) / d2;

            float currentT = 0F;
            float lastStepTraveledDistance;

            for(; f > -this.themeStrength / 3.0F; f -= 0.75F * lastStepTraveledDistance) {
                blockpos.set(voxelX, voxelY, voxelZ);
                long longpos = blockpos.asLong();

                if (!this.level_.isInWorldBounds(blockpos)) break;

                // Advance voxel
                float nextT;
                if (tMaxX < tMaxY) {
                    if (tMaxX < tMaxZ) {
                        nextT = (float) tMaxX;
                        voxelX += stepX;
                        tMaxX += tDeltaX;
                    } else {
                        nextT = (float) tMaxZ;
                        voxelZ += stepZ;
                        tMaxZ += tDeltaZ;
                    }
                } else {
                    if (tMaxY < tMaxZ) {
                        nextT = (float) tMaxY;
                        voxelY += stepY;
                        tMaxY += tDeltaY;
                    } else {
                        nextT = (float) tMaxZ;
                        voxelZ += stepZ;
                        tMaxZ += tDeltaZ;
                    }
                }

                lastStepTraveledDistance = nextT - currentT;
                currentT = nextT;

                long voxelData = voxelCache.get(longpos);

                if (voxelData == Long.MIN_VALUE) {
                    BlockState blockstate = this.level_.getBlockState(blockpos);
                    FluidState fluidstate = blockstate.getFluidState();

                    boolean isWaterToIgnore = this.hasEvaporateModifier && fluidstate.is(FluidTags.WATER);

                    float resistance = isWaterToIgnore
                            ? this.damageCalculator_.getBlockExplosionResistance(
                            this, this.level_, blockpos, Blocks.AIR.defaultBlockState(), fluidstate).orElse(0.0F)
                            : this.damageCalculator_.getBlockExplosionResistance(
                            this, this.level_, blockpos, blockstate, fluidstate).orElse(0.0F);

                    f -= (resistance + 0.3F) * lastStepTraveledDistance;

                    if (f > 0.0F) {
                        if (!interactsWithBlocksFlag) continue;

                        breakBlock(blockstate, blockpos, playerIndirectSourceEntityFlag);


                        voxelCache.put(longpos, packVoxelData(blockstate.isAir() || isWaterToIgnore, true, resistance));
                        if (this.fire_ && f < FIRE_THRESHOLD) mayIgnite.add(longpos);
                        continue;
                    }

                    voxelData = packVoxelData(blockstate.isAir() || isWaterToIgnore, false, resistance);
                    voxelCache.put(longpos, voxelData);
                } else {
                    f -= (resistanceFromLong(voxelData) + 0.3F) * lastStepTraveledDistance;

                    if (brokeFromLong(voxelData)) continue;

                    if (f > 0.0F) {
                        if (!interactsWithBlocksFlag) continue;

                        breakBlock(this.level_.getBlockState(blockpos), blockpos, playerIndirectSourceEntityFlag);


                        voxelCache.put(longpos, setBroke(voxelData));
                        if (this.fire_ && f < FIRE_THRESHOLD) mayIgnite.add(longpos);
                        continue;
                    }
                }

                if (!this.hasTheme || f <= -this.themeStrength || isAirFromLong(voxelData)) continue;
                if (f > almostBroke.get(longpos)) almostBroke.put(longpos, f);
            }
        }

        if(this.hasTheme){
            for(Long2FloatMap.Entry entry : this.almostBroke.long2FloatEntrySet()){
                BlockPos pos = BlockPos.of(entry.getLongKey());
                float f = entry.getFloatValue();

                if(f > 0) continue;

                BlockState replacement = this.themeData.getReplacementBlock(f);
                if(replacement == null || replacement == Blocks.AIR.defaultBlockState()) continue;

                BlockEntity blockEntity = this.level_.getBlockEntity(pos);

                if (blockEntity == null) {
                    this.level_.setBlockAndUpdate(pos, replacement);
                    continue;
                }

                BlockState oldState = this.level_.getBlockState(pos);

                if(blockEntity instanceof Container container){
                    Containers.dropContents(this.level_, pos, container);
                } else {
                    LazyOptional<IItemHandler> capability = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);

                    if(capability.isPresent()){
                        IItemHandler handler = capability.orElseThrow(() -> new IllegalStateException("Expected IItemHandler not found for block at " + pos));
                        for(int i = 0; i < handler.getSlots(); i++){
                            ItemStack stack  = handler.getStackInSlot(i);
                            if (!stack.isEmpty()) Block.popResource(level_, pos, stack);
                        }
                    }
                }

                oldState.onRemove(this.level_, pos, replacement, false);
                this.level_.setBlockAndUpdate(pos, replacement);
            }
        }

        for (ObjectArrayList<Drop> dropList : drops.values()) {
            for(Drop drop : dropList) {
                Block.popResource(this.level_, drop.pos, drop.stack);
            }
        }

        if (this.fire_) {
            LongIterator iterator = this.mayIgnite.iterator();
            while (iterator.hasNext()) {
                long longpos = iterator.nextLong();
                BlockPos pos = BlockPos.of(longpos);

                if (!(this.random_.nextInt(3) == 0)) continue;
                long voxelData = voxelCache.get(longpos);
                if ((longpos != Long.MIN_VALUE && (isAirFromLong(voxelData)) || this.level_.getBlockState(pos).isAir())
                        && this.level_.getBlockState(pos.below()).isSolidRender(this.level_, pos.below())) {
                    this.level_.setBlockAndUpdate(pos, BaseFireBlock.getState(this.level_, pos));
                }
            }
        }
    }

    /**
     * Does the second part of the explosion (sound, particles, potions)
     */
    @Override
    public void finalizeExplosion(boolean pSpawnParticles) {
        // Random pitch between 0.63F and 0.77F
        float pitch = (1.0F + (this.level_.random.nextFloat() - this.level_.random.nextFloat()) * 0.2F) * 0.7F;
        SoundEvent soundEvent = BombModifierUtil.getTier(stack) > 0
                ? SoundEvents.GENERIC_EXPLODE
                : SoundEvents.PUFFER_FISH_FLOP;
        this.level_.playSound(
                null,
                this.x_, this.y_, this.z_,
                soundEvent,
                SoundSource.BLOCKS,
                this.radius_,
                pitch
        );

        if (this.level_ instanceof ServerLevel serverLevel) {
            spawnParticles(serverLevel);
        }

        if(!this.hasImbuedModifier) return;
        List<MobEffectInstance> effects = PotionUtils.getMobEffects(stack);

        if(effects.isEmpty()) return;
        AreaEffectCloud cloud = new AreaEffectCloud(
                this.level_, this.x_, this.y_, this.z_);

        cloud.setRadius(this.radius_ - 0.5F);
        cloud.setRadiusOnUse(-0.2F);
        cloud.setWaitTime(10);
        cloud.setDuration(100 * (int) radius_);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());

        if (this.stack.getTag().contains("CustomPotionEffects")) {
            for (MobEffectInstance effect : effects) {
                cloud.addEffect(new MobEffectInstance(
                        effect.getEffect(),
                        effect.getDuration() / 4,
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.isVisible()
                ));
            }
        } else {
            cloud.setPotion(PotionUtils.getPotion(stack));
        }

        this.level_.addFreshEntity(cloud);
    }

    private void spawnParticles(ServerLevel serverLevel){
        int particleCount = (int)(this.radius_ * 4.0F);

        double spread = this.radius_ * 0.3;

        serverLevel.sendParticles(ParticleTypes.EXPLOSION, x_, y_, z_, particleCount, spread, spread, spread, 0.1);

        if(this.hasLadenModifier) {
            int color = PotionUtils.getColor(PotionUtils.getMobEffects(stack));
            float r = (color >> 16 & 255) / 255.0F;
            float g = (color >> 8 & 255) / 255.0F;
            float b = (color & 255) / 255.0F;

            serverLevel.sendParticles(new DustParticleOptions(new Vector3f(r, g, b), 1.0F),
                    this.x_, this.y_, this.z_, particleCount * 5,
                    spread, spread, spread, 1.0);
        }
    }

    private static void addBlockDrops(Object2ObjectOpenHashMap<ItemMergeKey, ObjectArrayList<Drop>> drops,
                                      ItemStack stack, BlockPos pos, int maxDropStackSize) {
        if (stack.isEmpty()) return;

        ItemMergeKey key = new ItemMergeKey(stack.getItem(), stack.getTag());

        ObjectArrayList<Drop> list = drops.computeIfAbsent(key, k -> new ObjectArrayList<>());

        int maxStackSize = Math.min(stack.getMaxStackSize(), maxDropStackSize);

        for (Drop drop : list) {
            ItemStack existing = drop.stack;

            int transferable = Math.min(
                    maxStackSize - existing.getCount(),
                    stack.getCount()
            );

            if (transferable <= 0) continue;
            existing.grow(transferable);
            stack.shrink(transferable);

            if (stack.isEmpty()) return;
        }

        list.add(new Drop(stack, pos));
    }

    private static long packVoxelData(boolean air, boolean broke, float resistance) {
        int resistanceBits = Float.floatToRawIntBits(resistance);
        return ((long) resistanceBits << 2)
             | (air ? 1L : 0L)
             | (broke ? 2L : 0L);
    }

    private static boolean isAirFromLong(long l) {
        return (l & 1L) != 0;
    }

    private static boolean brokeFromLong(long l) {
        return (l & 2L) != 0;
    }

    private static float resistanceFromLong(long l) {
        return Float.intBitsToFloat((int)(l >>> 2));
    }

    private static long setBroke(long l) {
        return l | 2L;
    }

    private record ItemMergeKey (Item item, CompoundTag tag) {}
    private record Drop (ItemStack stack, BlockPos pos) {}
}
