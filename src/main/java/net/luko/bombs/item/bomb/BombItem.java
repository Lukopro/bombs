package net.luko.bombs.item.bomb;

import net.luko.bombs.data.modifiers.ModifierManager;
import net.luko.bombs.entity.bomb.ThrownBombEntity;
import net.luko.bombs.util.BombModifierUtil;
import net.luko.bombs.util.BombPotionUtil;
import net.luko.bombs.util.BombTextureUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class BombItem extends Item {

    protected static Map<Integer, Float> explosionPowerMap = new HashMap<>();
    static{
        explosionPowerMap.put(1, 2.0F);
        explosionPowerMap.put(2, 3.0F);
        explosionPowerMap.put(3, 4.0F);
        explosionPowerMap.put(4, 5.0F);
        explosionPowerMap.put(5, 6.0F);
        explosionPowerMap.put(6, 7.0F);
    }

    public BombItem(Properties properties){
        super(properties);
    }

    protected float calculateExplosionPower(ItemStack stack){
        int tier = 1;
        if (stack.hasTag() && stack.getTag().contains("Tier")){
            tier = stack.getTag().getInt("Tier");
        }
        float power = explosionPowerMap.getOrDefault(tier, 0.5F);
        if(BombModifierUtil.hasModifier(stack, "golden")){
            power += 0.5F;
        }
        return power;
    }

    public static void setExplosionPowerMapTier(int tier, float power){
        explosionPowerMap.put(tier, power);
    }

    // When bomb is in hand and is right clicked, use() is called.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
        ItemStack stack = player.getItemInHand(hand);

        if(BombModifierUtil.hasModifier(stack, "quickdraw")) {
            if (!level.isClientSide()) {
                throwBomb(level, player, stack, getBaseVelocity(stack));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack){
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft){
        if(level.isClientSide()) return;

        int usedTicks = this.getUseDuration(stack) - timeLeft;
        if(usedTicks <= 2) return;

        float charge = getChargeFactor(usedTicks);
        float velocity = getBaseVelocity(stack) * charge;

        throwBomb(level, (Player) user, stack, velocity);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack){
        return UseAnim.BOW;
    }

    protected float getChargeFactor(int ticks){
        float f = ticks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Mth.clamp(f, 0.1F, 1.0F);
    }

    public abstract ThrownBombEntity createBombEntity(Level level, float explosionPower);
    public abstract ThrownBombEntity createBombEntity(Level level, LivingEntity thrower, float explosionPower);

    public void throwBomb(Level level, Player player, ItemStack stack, float velocity){
        ThrownBombEntity bombEntity = createBombEntity(level, player, calculateExplosionPower(stack));

        // Spawns bomb slightly in front of thrower.
        Vec3 lookAngle = player.getLookAngle();
        bombEntity.setPos(
                player.getX() + lookAngle.x * 0.6,
                player.getY() + player.getEyeHeight() + lookAngle.y * 0.6,
                player.getZ() + lookAngle.z * 0.6
        );

        // bombEntity is given an ItemStack with an NBT tag.
        bombEntity.setItem(stack);

        // Bomb is launched from the player.
        bombEntity.shootFromRotation(player,
                player.getXRot(), player.getYRot(), 0.0F,
                velocity, 1.0F);

        // Play sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                this.getThrowSound(stack), SoundSource.PLAYERS, 0.5F, 1.0F);

        // Bomb is spawned server-side
        level.addFreshEntity(bombEntity);

        // If in creative mode, player can use infinite bombs.
        if(!player.getAbilities().instabuild){
            stack.shrink(1);
        }
    }

    public void throwBomb(Level level, LivingEntity thrower, float pitch, float yaw, ItemStack stack, float velocity){
        ThrownBombEntity bombEntity = createBombEntity(level, thrower, calculateExplosionPower(stack));

        // Spawns bomb slightly in front of thrower.
        Vec3 lookAngle = Vec3.directionFromRotation(pitch, yaw);
        bombEntity.setPos(
                thrower.getX() + lookAngle.x * 0.6,
                thrower.getY() + thrower.getEyeHeight() + lookAngle.y * 0.6,
                thrower.getZ() + lookAngle.z * 0.6
        );

        // bombEntity is given an ItemStack with an NBT tag.
        bombEntity.setItem(stack);

        // Bomb is launched from the player.
        bombEntity.shootFromRotation(thrower,
                pitch, yaw, 0.0F,
                velocity, 1.0F);

        // Play sound
        level.playSound(null, thrower.getX(), thrower.getY(), thrower.getZ(),
                this.getThrowSound(stack), SoundSource.HOSTILE, 0.5F, 1.0F);

        // Bomb is spawned server-side
        level.addFreshEntity(bombEntity);
    }

    public void throwBomb(Level level, BlockSource source, ItemStack stack) {
        Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);

        ThrownBombEntity bombEntity = createBombEntity(level, calculateExplosionPower(stack));

        bombEntity.setPos(source.x() + direction.getStepX() * 0.5,
                          source.y() + direction.getStepY() * 0.5,
                          source.z() + direction.getStepZ() * 0.5);

        // bombEntity is given an ItemStack with an NBT tag.
        bombEntity.setItem(stack);

        // Bomb is launched from the player.
        bombEntity.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(),
                getBaseVelocity(stack), 1.0F);

        // Play sound
        level.playSound(null, source.x(), source.y(), source.z(),
                this.getThrowSound(stack), SoundSource.BLOCKS, 0.5F, 1.0F);

        // Bomb is spawned server-side
        level.addFreshEntity(bombEntity);

        stack.shrink(1);
    }

    public abstract SoundEvent getThrowSound(ItemStack stack);

    @Override
    public Component getName(ItemStack stack){
        int tier = (int)BombTextureUtil.getTextureIndex(stack);
        return Component.translatable("tier.bombs." + tier, getBaseName(stack));
    }

    public abstract Component getBaseName(ItemStack stack);

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag){
        super.appendHoverText(stack, level, tooltip, flag);


    }

    public float getBaseVelocity(ItemStack stack){
        if(BombModifierUtil.hasModifier(stack, "light")){
            return 2.5F;
        }
        return 1.5F;
    }
}
