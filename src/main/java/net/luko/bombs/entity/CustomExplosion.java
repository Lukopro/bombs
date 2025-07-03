package net.luko.bombs.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.*;
import javax.annotation.Nullable;

import net.luko.bombs.Bombs;
import net.luko.bombs.data.themes.ThemeData;
import net.luko.bombs.data.themes.ThemeManager;
import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

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
    private final Map<BlockPos, Float> almostBroke = new HashMap<BlockPos, Float>();
    private final Map<Player, Vec3> hitPlayers_ = Maps.newHashMap();
    private final Vec3 position_;
    private final ItemStack stack;
    private final ThemeData themeData;
    private final float themeStrength;
    /* Saved for later!
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
    */
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

        if(stack.hasTag() && stack.getTag().contains("Theme", Tag.TAG_STRING)){
            String themeId = stack.getTag().getString("Theme");
            ResourceLocation themeRL = ResourceLocation.tryParse(themeId.contains(":") ? themeId : Bombs.MODID + ":" + themeId);
            this.themeData = themeRL != null && ThemeManager.hasTheme(themeRL) ? ThemeManager.get(themeRL) : null;
        } else {
            this.themeData = null;
        }
        this.themeStrength = this.themeData == null ? 0.0F : this.themeData.getStrength();
    }

    private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity pEntity) {
        return (ExplosionDamageCalculator)(pEntity == null ? EXPLOSION_DAMAGE_CALCULATOR_ : new EntityBasedExplosionDamageCalculator(pEntity));
    }
    /*
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
        Map<BlockPos, Float> map = new HashMap<>();

        int gridSize = 4 + (int)Math.floor(Math.pow(radius_, 1.4));

        for(int j = 0; j < gridSize; ++j) {
            for(int k = 0; k < gridSize; ++k) {
                for(int l = 0; l < gridSize; ++l) {
                    if (j == 0 || j == gridSize - 1 || k == 0 || k == gridSize - 1 || l == 0 || l == gridSize - 1) {
                        double d0 = (double)((float)j / (float)(gridSize - 1) * 2.0F - 1.0F);
                        double d1 = (double)((float)k / (float)(gridSize - 1) * 2.0F - 1.0F);
                        double d2 = (double)((float)l / (float)(gridSize - 1) * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius_ * (0.7F + this.level_.random.nextFloat() * 0.6F);
                        double d4 = this.x_;
                        double d6 = this.y_;
                        double d8 = this.z_;

                        for(float f1 = 0.3F; f > -this.themeStrength / 3.0F; f -= 0.22500001F) {
                            BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            BlockState blockstate = this.level_.getBlockState(blockpos);
                            FluidState fluidstate = this.level_.getFluidState(blockpos);

                            if (BombModifierUtil.hasModifier(this.stack, "evaporate") && fluidstate.is(FluidTags.WATER)) {
                                blockstate = Blocks.AIR.defaultBlockState();
                            }

                            if (!this.level_.isInWorldBounds(blockpos)) {
                                break;
                            }

                            Optional<Float> optional = this.damageCalculator_.getBlockExplosionResistance(this, this.level_, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.damageCalculator_.shouldBlockExplode(this, this.level_, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            } else if (f > -this.themeStrength){
                                if(!blockstate.isAir()) map.merge(blockpos, f, Math::max);
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
        this.almostBroke.putAll(map);

        almostBroke.keySet().removeIf(toBlow_::contains);

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
                        float damageAmount = (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D));
                        if (BombModifierUtil.hasModifier(this.stack, "lethal")) {
                            damageAmount *= 2.0F;
                        }

                        if((entity instanceof ItemEntity || entity instanceof AbstractMinecart || entity instanceof Boat)){
                            if(!BombModifierUtil.hasModifier(this.stack, "gentle")){
                                entity.hurt(this.getDamageSource(), damageAmount);
                            }
                        } else if(!BombModifierUtil.hasModifier(this.stack, "pacified")) {
                            entity.hurt(this.getDamageSource(), damageAmount);
                        }

                        double d11;
                        if (entity instanceof LivingEntity livingEntity) {
                            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingEntity, d10);

                            if(stack.hasTag() && (stack.getTag().contains("Potion") || stack.getTag().contains("CustomPotionEffects"))){
                                if(BombModifierUtil.hasModifier(stack, "laden")){
                                    for(MobEffectInstance effect : PotionUtils.getMobEffects(stack)){
                                        livingEntity.addEffect(new MobEffectInstance(effect));
                                    }
                                }
                            }

                            if(BombModifierUtil.hasModifier(this.stack, "frost")){
                                livingEntity.setTicksFrozen(livingEntity.getTicksFrozen() + (int)(20.0F * this.radius_));
                            }

                        } else {
                            d11 = d10;
                        }

                        d5 *= d11;
                        d7 *= d11;
                        d9 *= d11;
                        Vec3 vec31 = new Vec3(d5, d7, d9);

                        // Modifier adaptation
                        if(!BombModifierUtil.hasModifier(this.stack, "dampened")) {
                            if (BombModifierUtil.hasModifier(this.stack, "shockwave")) {
                                vec31 = vec31.scale(2.0);
                            }
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

        for (Map.Entry<Player, Vec3> entry : this.hitPlayers_.entrySet()){
            Player player = entry.getKey();
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        }

        if(BombModifierUtil.hasModifier(stack, "imbued")){
            List<MobEffectInstance> effects = PotionUtils.getMobEffects(stack);
            if(!effects.isEmpty()){
                AreaEffectCloud cloud = new AreaEffectCloud(
                        level_, x_, y_, z_);

                cloud.setRadius(radius_ - 0.5F);
                cloud.setRadiusOnUse(-0.2F);
                cloud.setWaitTime(10);
                cloud.setDuration(100 * (int)radius_);
                cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());

                if(stack.hasTag() && stack.getTag().contains("CustomPotionEffects")){
                    for(MobEffectInstance effect : effects){
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
        this.level_.playSound(
                null,
                this.x_, this.y_, this.z_,
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.BLOCKS,
                this.radius_,
                pitch
        );

        boolean flag = this.interactsWithBlocks();

        // Restructured particle spawning
        if (this.level_ instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel) this.level_;
            spawnParticles(serverLevel);
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

            if(this.themeData != null){
                for(Map.Entry<BlockPos, Float> entry : almostBroke.entrySet()){
                    BlockPos pos = entry.getKey();
                    float f = entry.getValue();

                    BlockState replacement = themeData.getReplacementBlock(f);
                    if(replacement == null || replacement == Blocks.AIR.defaultBlockState()) continue;

                    BlockState oldState = level_.getBlockState(pos);
                    BlockEntity blockEntity = level_.getBlockEntity(pos);

                    if (blockEntity != null) {
                        if(blockEntity instanceof Container container){
                            Containers.dropContents(level_, pos, container);
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
                        oldState.onRemove(level_, pos, oldState, false);
                    }

                    level_.setBlockAndUpdate(pos, replacement);
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

    private void spawnParticles(ServerLevel serverLevel){
        int particleCount = (int)(this.radius_ * 4.0F);

        double spread = this.radius_ * 0.3;

        serverLevel.sendParticles(ParticleTypes.EXPLOSION, x_, y_, z_, particleCount, spread, spread, spread, 0.1);

        if(BombModifierUtil.hasModifier(stack, "laden")) {
            int color = PotionUtils.getColor(PotionUtils.getMobEffects(stack));
            float r = (color >> 16 & 255) / 255.0F;
            float g = (color >> 8 & 255) / 255.0F;
            float b = (color & 255) / 255.0F;

            serverLevel.sendParticles(new DustParticleOptions(new Vector3f(r, g, b), 1.0F),
                    x_, y_, z_, particleCount * 5, spread, spread, spread, 1.0);
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
    /* Saved for later!
    public boolean interactsWithBlocks() {
        return this.blockInteraction_ != Explosion.BlockInteraction.KEEP;
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
     *
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
    */
}
