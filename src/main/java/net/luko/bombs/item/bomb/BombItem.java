package net.luko.bombs.item.bomb;

import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.data.modifiers.ModifierColorManager;
import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.entity.bomb.ThrownBombEntity;
import net.luko.bombs.util.BombModifierUtil;
import net.luko.bombs.util.BombPotionUtil;
import net.luko.bombs.util.BombTextureUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public abstract class BombItem extends Item {

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
        if (stack.has(ModDataComponents.TIER.get())){
            tier = stack.getOrDefault(ModDataComponents.TIER.get(), 1);
        }
        return explosionPowerMap.getOrDefault(tier, 0.5F);
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
    public int getUseDuration(ItemStack stack, LivingEntity entity){
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft){
        if(level.isClientSide()) return;

        int usedTicks = this.getUseDuration(stack, user) - timeLeft;
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
        Direction direction = source.state().getValue(DispenserBlock.FACING);

        ThrownBombEntity bombEntity = createBombEntity(level, calculateExplosionPower(stack));

        bombEntity.setPos(source.pos().getX() + direction.getStepX() * 0.5,
                source.pos().getY() + direction.getStepY() * 0.5,
                source.pos().getZ() + direction.getStepZ() * 0.5);

        // bombEntity is given an ItemStack with an NBT tag.
        bombEntity.setItem(stack);

        // Bomb is launched from the player.
        bombEntity.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(),
                getBaseVelocity(stack), 1.0F);

        // Play sound
        level.playSound(null, source.pos(),
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag){
        super.appendHoverText(stack, context, tooltip, flag);

        if(stack.has(ModDataComponents.MODIFIERS.get()) && !stack.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of()).isEmpty()){
            List<String> modifiers = stack.get(ModDataComponents.MODIFIERS.get());

            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Modifiers:")
                    .withStyle(Style.EMPTY
                            .withColor(TextColor.fromRgb(0xdddddd)))
                    .withStyle(ChatFormatting.BOLD));

            for(String mod : modifiers){
                MutableComponent modifierComponent = Component.literal("- ")
                        .append(Component.translatable("modifier.bombs." + mod));

                if(mod.equals("laden") || mod.equals("imbued")){
                    int potionColor = stack.get(DataComponents.POTION_CONTENTS) == null
                            ? 0x385dc6
                            : stack.get(DataComponents.POTION_CONTENTS).getColor();

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
