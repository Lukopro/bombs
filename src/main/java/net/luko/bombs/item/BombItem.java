package net.luko.bombs.item;

import net.luko.bombs.data.modifiers.ModifierColorManager;
import net.luko.bombs.entity.ModEntities;
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
import net.minecraft.sounds.SoundEvents;
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

public class BombItem extends Item {

    private static Map<Integer, Float> explosionPowerMap = new HashMap<>();
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

    private float calculateExplosionPower(ItemStack stack){
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

    private float getChargeFactor(int ticks){
        float f = ticks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Mth.clamp(f, 0.1F, 1.0F);
    }

    public void throwBomb(Level level, Player player, ItemStack stack, float velocity){
        ThrownBombEntity bombEntity = new ThrownBombEntity(ModEntities.THROWN_BOMB.get(), level, player, calculateExplosionPower(stack));

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
        SoundEvent soundEvent = SoundEvents.FIRECHARGE_USE;
        int tier = BombModifierUtil.getTier(stack);
        if(tier >= 4) soundEvent = SoundEvents.WITHER_SHOOT;
        else if (tier <= 0) soundEvent = SoundEvents.EGG_THROW;

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                soundEvent, SoundSource.PLAYERS, 0.5F, 1.0F);

        // Bomb is spawned server-side
        level.addFreshEntity(bombEntity);

        // If in creative mode, player can use infinite bombs.
        if(!player.getAbilities().instabuild){
            stack.shrink(1);
        }
    }

    public void throwBomb(Level level, BlockSource source, ItemStack stack) {
        Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);

        ThrownBombEntity bombEntity = new ThrownBombEntity(ModEntities.THROWN_BOMB.get(), level, calculateExplosionPower(stack));

        bombEntity.setPos(source.x() + direction.getStepX() * 0.5,
                          source.y() + direction.getStepY() * 0.5,
                          source.z() + direction.getStepZ() * 0.5);

        // bombEntity is given an ItemStack with an NBT tag.
        bombEntity.setItem(stack);

        // Bomb is launched from the player.
        bombEntity.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(),
                getBaseVelocity(stack), 1.0F);

        // Play sound
        SoundEvent soundEvent = SoundEvents.FIRECHARGE_USE;
        int tier = BombModifierUtil.getTier(stack);
        if(tier >= 4) soundEvent = SoundEvents.WITHER_SHOOT;
        else if (tier <= 0) soundEvent = SoundEvents.EGG_THROW;

        level.playSound(null, source.x(), source.y(), source.z(),
                soundEvent, SoundSource.BLOCKS, 0.5F, 1.0F);

        // Bomb is spawned server-side
        level.addFreshEntity(bombEntity);

        stack.shrink(1);
    }

    public void throwBomb(Level level, LivingEntity thrower, float pitch, float yaw, ItemStack stack, float velocity){
        ThrownBombEntity bombEntity = new ThrownBombEntity(ModEntities.THROWN_BOMB.get(), level, thrower, calculateExplosionPower(stack));

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
        SoundEvent soundEvent = SoundEvents.FIRECHARGE_USE;
        int tier = BombModifierUtil.getTier(stack);
        if(tier >= 4) soundEvent = SoundEvents.WITHER_SHOOT;
        else if (tier <= 0) soundEvent = SoundEvents.EGG_THROW;

        level.playSound(null, thrower.getX(), thrower.getY(), thrower.getZ(),
                soundEvent, SoundSource.HOSTILE, 0.5F, 1.0F);

        // Bomb is spawned server-side
        level.addFreshEntity(bombEntity);
    }

    @Override
    public Component getName(ItemStack stack){
        int tier = (int)BombTextureUtil.getTextureIndex(stack);
        return Component.translatable("tier.bombs." + tier);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag){
        super.appendHoverText(stack, level, tooltip, flag);

        if(!stack.hasTag()){
            tooltip.add(Component.literal("No modifiers"));
            return;
        }

        if(stack.getTag().contains("Modifiers")){
            ListTag modifiers = stack.getTag().getList("Modifiers", Tag.TAG_STRING);

            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Modifiers:")
                    .withStyle(Style.EMPTY
                            .withColor(TextColor.fromRgb(0xdddddd)))
                    .withStyle(ChatFormatting.BOLD));

            for(int i = 0; i < modifiers.size(); i++){
                String mod = modifiers.getString(i);

                MutableComponent modifierComponent = Component.literal("- ")
                        .append(Component.translatable("modifier.bombs." + mod));

                if(mod.equals("laden") || mod.equals("imbued")){
                    int potionColor = PotionUtils.getColor(
                            PotionUtils.getMobEffects(stack));

                    String potionDescriptionId = BombPotionUtil.getDescriptionId(stack);

                    modifierComponent.append(Component.literal(" ("))
                            .append(Component.translatable(potionDescriptionId))
                            .append(Component.literal(")"))
                            .withStyle(Style.EMPTY.withColor(potionColor));
                } else {
                    modifierComponent.withStyle(Style.EMPTY.withColor(ModifierColorManager.INSTANCE.getColor(mod)));
                }

                tooltip.add(modifierComponent);

                if(Screen.hasShiftDown()){
                    tooltip.add(Component.translatable("modifier.bombs." + mod + ".info")
                            .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));

                    tooltip.add(Component.empty());
                }
            }

            if(!Screen.hasShiftDown()){
                tooltip.add(Component.empty());
                tooltip.add(Component.literal("SHIFT for descriptions"));
            }

        } else {
            tooltip.add(Component.literal("No modifiers"));
        }
    }

    public float getBaseVelocity(ItemStack stack){
        if(BombModifierUtil.hasModifier(stack, "light")){
            return 2.5F;
        }
        return 1.5F;
    }
}
