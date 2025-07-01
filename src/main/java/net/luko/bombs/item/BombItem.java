package net.luko.bombs.item;

import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.data.ModDataComponents;
import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.entity.ThrownBombEntity;
import net.luko.bombs.util.BombModifierUtil;
import net.luko.bombs.util.BombTextureUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BombItem extends Item {

    private static final Map<String, Style> modifierColorMap = Map.ofEntries(
            Map.entry("flame", Style.EMPTY.withColor(TextColor.fromRgb(0xff5e00))),
            Map.entry("light", Style.EMPTY.withColor(TextColor.fromRgb(0xffffe0))),
            Map.entry("contained", Style.EMPTY.withColor(TextColor.fromRgb(0x369c7d))),
            Map.entry("pacified", Style.EMPTY.withColor(TextColor.fromRgb(0x369c7d))),
            Map.entry("dampened", Style.EMPTY.withColor(TextColor.fromRgb(0x369c7d))),
            Map.entry("shatter", Style.EMPTY.withColor(TextColor.fromRgb(0x5f9c36))),
            Map.entry("lethal", Style.EMPTY.withColor(TextColor.fromRgb(0x5f9c36))),
            Map.entry("shockwave", Style.EMPTY.withColor(TextColor.fromRgb(0x5f9c36))),
            Map.entry("gentle", Style.EMPTY.withColor(TextColor.fromRgb(0x369c98))),
            Map.entry("evaporate", Style.EMPTY.withColor(TextColor.fromRgb(0x9aa9b5))),
            Map.entry("quickdraw", Style.EMPTY.withColor(TextColor.fromRgb(0x7a5c3c))),
            Map.entry("golden", Style.EMPTY.withColor(TextColor.fromRgb(0xffe017))),
            Map.entry("float", Style.EMPTY.withColor(TextColor.fromRgb(0xcbcf9b))),
            Map.entry("sink", Style.EMPTY.withColor(TextColor.fromRgb(0x3d372e)))
    );

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
        return explosionPowerMap.getOrDefault(tier, 2.0F);
    }

    public static void setExplosionPowerMapTier(int tier, float power){
        explosionPowerMap.put(tier, power);
    }

    // When bomb is in hand and is right clicked, use() is called.
    // When bomb is in hand and is right clicked, use() is called.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
        ItemStack stack = player.getItemInHand(hand);

        if(BombsConfig.QUICKDRAW_BY_DEFAULT.get() || BombModifierUtil.hasModifier(stack, "quickdraw")) {
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

    private float getChargeFactor(int ticks){
        float f = ticks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Mth.clamp(f, 0.1F, 1.0F);
    }

    public void throwBomb(Level level, Player player, ItemStack stack, float velocity){
        ThrownBombEntity bombEntity = new ThrownBombEntity(ModEntities.THROWN_BOMB.get(), level, player, calculateExplosionPower(stack));

        // Spawns bomb slightly in front of player.
        Vec3 forward = player.getLookAngle();
        bombEntity.setPos(
                player.getX() + forward.x * 0.6,
                player.getY() + player.getEyeHeight() + forward.y * 0.6,
                player.getZ() + forward.z * 0.6
        );

        // bombEntity is given an ItemStack with an NBT tag.
        bombEntity.setItem(stack);

        // Bomb is launched from the player.
        bombEntity.shootFromRotation(player,
                player.getXRot(), player.getYRot(), 0.0F,
                velocity, 1.0F);

        // Play sound
        SoundEvent soundEvent =
                (stack.getOrDefault(ModDataComponents.TIER.get(), 1) >= 4)
                        ? SoundEvents.WITHER_SHOOT
                        : SoundEvents.FIRECHARGE_USE;

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
        Direction direction = source.state().getValue(DispenserBlock.FACING);

        ThrownBombEntity bombEntity = new ThrownBombEntity(ModEntities.THROWN_BOMB.get(), level, calculateExplosionPower(stack));

        bombEntity.setPos(source.pos().getX() + direction.getStepX() * 0.5,
                source.pos().getY() + direction.getStepY() * 0.5,
                source.pos().getZ() + direction.getStepZ() * 0.5);

        // bombEntity is given an ItemStack with an NBT tag.
        bombEntity.setItem(stack);

        // Bomb is launched from the player.
        bombEntity.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(),
                getBaseVelocity(stack), 1.0F);

        // Play sound
        SoundEvent soundEvent =
                (stack.getOrDefault(ModDataComponents.TIER.get(), 1)) >= 4
                        ? SoundEvents.WITHER_SHOOT
                        : SoundEvents.FIRECHARGE_USE;

        level.playSound(null, source.pos(),
                soundEvent, SoundSource.PLAYERS, 0.5F, 1.0F);

        // Bomb is spawned server-side
        level.addFreshEntity(bombEntity);

        stack.shrink(1);
    }

    @Override
    public Component getName(ItemStack stack){
        int tier = (int) BombTextureUtil.getTextureIndex(stack);
        return Component.translatable("tier.bombs." + tier);
    }

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

                if(mod.equals("imbued") && stack.has(DataComponents.POTION_CONTENTS)){
                    ItemStack tempPotionStack = new ItemStack(Items.POTION);
                    tempPotionStack.set(DataComponents.POTION_CONTENTS, stack.get(DataComponents.POTION_CONTENTS));
                    int potionColor = stack.get(DataComponents.POTION_CONTENTS).getColor();
                    String potionDescriptionId = ((PotionItem) Items.POTION).getDescriptionId(tempPotionStack);

                    modifierComponent.append(Component.literal(" ("))
                            .append(Component.translatable(potionDescriptionId))
                            .append(Component.literal(")"))
                            .withStyle(Style.EMPTY.withColor(potionColor));
                } else {
                    modifierComponent.withStyle(modifierColorMap.getOrDefault(mod, Style.EMPTY.withColor(TextColor.fromRgb(0x3d372e))));
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

    private float getBaseVelocity(ItemStack stack){
        if(BombModifierUtil.hasModifier(stack, "light")){
            return 2.5F;
        }
        return 1.5F;
    }
}
