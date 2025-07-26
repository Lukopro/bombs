package net.luko.bombs.entity.bomb;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.*;
import javax.annotation.Nullable;

import net.luko.bombs.Bombs;
import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.data.themes.ThemeData;
import net.luko.bombs.data.themes.ThemeManager;
import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.joml.Vector3f;

// Class and instance variables have an underscore _ to differentiate from super's variables.

public class BombExplosion extends Explosion {
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
    private final LongOpenHashSet toBlow_ = new LongOpenHashSet();
    private final Long2FloatOpenHashMap almostBroke = new Long2FloatOpenHashMap();
    private final Map<Player, Vec3> hitPlayers_ = Maps.newHashMap();
    private final Vec3 position_;
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
    private float dropChance;

    public BombExplosion(Level pLevel, @Nullable Entity pSource, @Nullable DamageSource pDamageSource, @Nullable ExplosionDamageCalculator pDamageCalculator, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction, ItemStack stack) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
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

        if(stack.has(ModDataComponents.THEME)){
            String themeId = stack.get(ModDataComponents.THEME);
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
        this.dropChance = Math.min(1.0F, 10.0F / radius_);
    }

    private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity pEntity) {
        return (ExplosionDamageCalculator)(pEntity == null ? EXPLOSION_DAMAGE_CALCULATOR_ : new EntityBasedExplosionDamageCalculator(pEntity));
    }

    /**
     * Does the first part of the explosion (destroy blocks)
     */
    @Override
    public void explode() {
        this.level_.gameEvent(this.source_, GameEvent.EXPLODE, new Vec3(this.x_, this.y_, this.z_));
        Long2FloatOpenHashMap map = new Long2FloatOpenHashMap();
        map.defaultReturnValue(Float.NEGATIVE_INFINITY);

        BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos();

        int gridSize = 4 + (int)Math.floor(Math.pow(this.radius_, 1.4));

        for(int j = 0; j < gridSize; ++j) {
            for(int k = 0; k < gridSize; ++k) {
                for(int l = 0; l < gridSize; ++l) {
                    if (j == 0 || j == gridSize - 1 || k == 0 || k == gridSize - 1 || l == 0 || l == gridSize - 1) {
                        double d0 = (float)j / (float)(gridSize - 1) * 2.0F - 1.0F;
                        double d1 = (float)k / (float)(gridSize - 1) * 2.0F - 1.0F;
                        double d2 = (float)l / (float)(gridSize - 1) * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;

                        float f = this.radius_ * (0.7F + this.level_.random.nextFloat() * 0.6F);
                        double d4 = this.x_;
                        double d6 = this.y_;
                        double d8 = this.z_;

                        for(; f > -this.themeStrength / 3.0F; f -= 0.22500001F) {
                            blockpos.set(d4, d6, d8);

                            d4 += d0 * (double)0.3F;
                            d6 += d1 * (double)0.3F;
                            d8 += d2 * (double)0.3F;

                            if (!this.level_.isInWorldBounds(blockpos)) break;

                            BlockState blockstate = this.level_.getBlockState(blockpos);
                            FluidState fluidstate = this.level_.getFluidState(blockpos);

                            if (this.hasEvaporateModifier && fluidstate.is(FluidTags.WATER)) {
                                blockstate = Blocks.AIR.defaultBlockState();
                            }

                            Optional<Float> optional = this.damageCalculator_.getBlockExplosionResistance(this, this.level_, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.damageCalculator_.shouldBlockExplode(this, this.level_, blockpos, blockstate, f)) {
                                toBlow_.add(blockpos.asLong());
                            } else if (this.hasTheme && f > -this.themeStrength){
                                if(!blockstate.isAir()) map.merge(blockpos.asLong(), f, Math::max);
                            }
                        }
                    }
                }
            }
        }

        if(hasTheme) {
            this.almostBroke.putAll(map);
        }

        float f2 = this.radius_ * 2.0F;
        int k1 = Mth.floor(this.x_ - (double)f2 - 1.0D);
        int l1 = Mth.floor(this.x_ + (double)f2 + 1.0D);
        int i2 = Mth.floor(this.y_ - (double)f2 - 1.0D);
        int i1 = Mth.floor(this.y_ + (double)f2 + 1.0D);
        int j2 = Mth.floor(this.z_ - (double)f2 - 1.0D);
        int j1 = Mth.floor(this.z_ + (double)f2 + 1.0D);
        List<Entity> list = this.level_.getEntities(this.source_, new AABB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        EventHooks.onExplosionDetonate(this.level_, this, list, f2);
        Vec3 vec3 = new Vec3(this.x_, this.y_, this.z_);

        for (Entity entity : list) {
            if (!entity.ignoreExplosion(this)) {
                double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double) f2;
                if (d12 <= 1.0D) {
                    double d5 = entity.getX() - this.x_;
                    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y_;
                    double d9 = entity.getZ() - this.z_;
                    double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d13 != 0.0D) {
                        d5 /= d13;
                        d7 /= d13;
                        d9 /= d13;
                        double d14 = (double) getSeenPercent(vec3, entity);
                        double d10 = (1.0D - d12) * d14;

                        // Modifier adaptation
                        float damageAmount = (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D));
                        if (!this.hasLethalModifier) {
                            damageAmount *= 0.5F;
                        }

                        if ((entity instanceof ItemEntity || entity instanceof AbstractMinecart || entity instanceof Boat)) {
                            if (!this.hasGentleModifier) {
                                entity.hurt(this.damageSource_, damageAmount);
                            }
                        } else if (!this.hasPacifiedModifier) {
                            entity.hurt(this.damageSource_, damageAmount);
                        }

                        double d11;
                        if (entity instanceof LivingEntity livingEntity) {
                            d11 = 1.0 - livingEntity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE);

                            if(BombModifierUtil.hasModifier(stack, "laden") && stack.has(DataComponents.POTION_CONTENTS)){
                                for(MobEffectInstance baseEffect : stack.get(DataComponents.POTION_CONTENTS).getAllEffects()){
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
            }
        }

        for (Map.Entry<Player, Vec3> entry : this.hitPlayers_.entrySet()){
            Player player = entry.getKey();
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        }

        if(BombModifierUtil.hasModifier(stack, "imbued") && stack.has(DataComponents.POTION_CONTENTS)){
            Iterable<MobEffectInstance> effects = stack.get(DataComponents.POTION_CONTENTS).getAllEffects();
            if(effects.iterator().hasNext()){
                AreaEffectCloud cloud = new AreaEffectCloud(
                        level_, x_, y_, z_);

                cloud.setRadius(radius_ - 0.5F);
                cloud.setRadiusOnUse(-0.2F);
                cloud.setWaitTime(10);
                cloud.setDuration(100 * (int)radius_);
                cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());


                for(MobEffectInstance effect : effects){
                    cloud.addEffect(new MobEffectInstance(
                            effect.getEffect(),
                            effect.getDuration() / 4,
                            effect.getAmplifier(),
                            effect.isAmbient(),
                            effect.isVisible()
                    ));
                }

                level_.addFreshEntity(cloud);
            }
        }
    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
    @Override
    public void finalizeExplosion(boolean pSpawnParticles) {
        // Replaced the sound emitter to be server-side.
        // Random pitch between 0.63F and 0.77F
        float pitch = (1.0F + (this.level_.random.nextFloat() - this.level_.random.nextFloat()) * 0.2F) * 0.7F;
        SoundEvent soundEvent = stack.getOrDefault(ModDataComponents.TIER.get(), 1) > 0
                ? SoundEvents.GENERIC_EXPLODE.value()
                : SoundEvents.PUFFER_FISH_FLOP;
        this.level_.playSound(
                null,
                this.x_, this.y_, this.z_,
                soundEvent,
                SoundSource.BLOCKS,
                this.radius_,
                pitch
        );

        boolean flag = this.interactsWithBlocks();

        // Restructured particle spawning
        if (this.level_ instanceof ServerLevel serverLevel) {
            spawnParticles(serverLevel);
        }

        if (flag) {
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
            boolean flag1 = this.getIndirectSourceEntity() instanceof Player;

            for(BlockPos blockpos : this.toBlow_.longStream().mapToObj(BlockPos::of).toList()) {
                BlockState blockstate = this.level_.getBlockState(blockpos);
                if (!blockstate.isAir()) {
                    BlockPos blockpos1 = blockpos.immutable();
                    this.level_.getProfiler().push("explosion_blocks");
                    if (blockstate.canDropFromExplosion(this.level_, blockpos, this)) {
                        if (this.level_ instanceof ServerLevel serverlevel) {
                            BlockEntity blockentity = blockstate.hasBlockEntity() ? this.level_.getBlockEntity(blockpos) : null;

                            LootParams.Builder lootparams$builder = (new LootParams.Builder(serverlevel))
                                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos))
                                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity)
                                    .withOptionalParameter(LootContextParams.THIS_ENTITY, this.source_);

                            if (this.blockInteraction_ == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                                lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius_);
                            }

                            blockstate.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY, flag1);
                            blockstate.getDrops(lootparams$builder).forEach((p_46074_) -> {
                                if (random_.nextFloat() < this.dropChance) addBlockDrops(objectarraylist, p_46074_, blockpos1);
                            });
                        }
                    }

                    blockstate.onBlockExploded(this.level_, blockpos, this);
                    this.level_.getProfiler().pop();
                }
            }

            if(this.hasTheme){
                for(Long2FloatMap.Entry entry : this.almostBroke.long2FloatEntrySet()){
                    BlockPos pos = BlockPos.of(entry.getLongKey());
                    if(this.toBlow_.contains(pos.asLong())) continue;
                    float f = entry.getFloatValue();

                    BlockState replacement = this.themeData.getReplacementBlock(f);
                    if(replacement == null || replacement == Blocks.AIR.defaultBlockState()) continue;

                    BlockState oldState = this.level_.getBlockState(pos);
                    BlockEntity blockEntity = this.level_.getBlockEntity(pos);

                    if (blockEntity != null) {
                        if(blockEntity instanceof Container container){
                            Containers.dropContents(level_, pos, container);
                        } else if (blockEntity instanceof Clearable clearable){
                            clearable.clearContent();
                        } else {
                            Bombs.LOGGER.warn("BlockEntity at {} does not expose items to drop", pos);
                        }

                        oldState.onRemove(level_, pos, oldState, false);
                    }

                    this.level_.setBlockAndUpdate(pos, replacement);
                }
            }


            for(Pair<ItemStack, BlockPos> pair : objectarraylist) {
                Block.popResource(this.level_, pair.getSecond(), pair.getFirst());
            }
        }

        if (this.fire_) {
            for(BlockPos blockpos2 : this.toBlow_.longStream().mapToObj(BlockPos::of).toList()) {
                if (this.random_.nextInt(3) == 0 && this.level_.getBlockState(blockpos2).isAir() && this.level_.getBlockState(blockpos2.below()).isSolidRender(this.level_, blockpos2.below())) {
                    this.level_.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level_, blockpos2));
                }
            }
        }
    }

    private void spawnParticles(ServerLevel serverLevel){
        int particleCount = (int)(this.radius_ * 4.0F);

        double spread = this.radius_ * 0.3;

        serverLevel.sendParticles(ParticleTypes.EXPLOSION, x_, y_, z_, particleCount, spread, spread, spread, 0.1);

        if(this.hasLadenModifier && stack.has(DataComponents.POTION_CONTENTS)) {
            int color = stack.get(DataComponents.POTION_CONTENTS).getColor();
            float r = (color >> 16 & 255) / 255.0F;
            float g = (color >> 8 & 255) / 255.0F;
            float b = (color & 255) / 255.0F;

            serverLevel.sendParticles(new DustParticleOptions(new Vector3f(r, g, b), 1.0F),
                    this.x_, this.y_, this.z_, particleCount * 5,
                    spread, spread, spread, 1.0);
        }
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
}
