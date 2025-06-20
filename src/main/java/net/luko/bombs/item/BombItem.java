package net.luko.bombs.item;

import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.entity.ThrownBombEntity;
import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
            Map.entry("gentle", Style.EMPTY.withColor(TextColor.fromRgb(0x369c7d))),
            Map.entry("evaporate", Style.EMPTY.withColor(TextColor.fromRgb(0x9aa9b5))),
            Map.entry("golden", Style.EMPTY.withColor(TextColor.fromRgb(0xffe017)))
    );

    private static Map<Integer, Float> explosionPowerMap = new HashMap<>();
    static{
        explosionPowerMap.put(1, 1.5F);
        explosionPowerMap.put(2, 2.5F);
        explosionPowerMap.put(3, 4.0F);
        explosionPowerMap.put(4, 5.5F);
        explosionPowerMap.put(5, 7.0F);
    }

    public BombItem(Properties properties){
        super(properties);
    }

    private float calculateExplosionPower(ItemStack stack){
        int tier = 1;
        if (stack.hasTag() && stack.getTag().contains("Tier")){
            tier = stack.getTag().getInt("Tier");
        }
        return explosionPowerMap.getOrDefault(tier, 1.5F);
    }

    public static void setExplosionPowerMapTier(int tier, float power){
        explosionPowerMap.put(tier, power);
    }

    // When bomb is in hand and is right clicked, use() is called.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
        ItemStack stack = player.getItemInHand(hand);

        // Server bomb logic
        if(!level.isClientSide()){
            ThrownBombEntity bombEntity = new ThrownBombEntity(ModEntities.THROWN_BOMB.get(), level, player, calculateExplosionPower(stack));

            // Spawns bomb slightly in front of player.
            Vec3 forward = player.getLookAngle();
            bombEntity.setPos(
                player.getX() + forward.x * 0.6,
                player.getY() + player.getEyeHeight(),
                player.getZ() + forward.z * 0.6
            );

            // bombEntity is set to its particular type (e.g. strong, blaze).
            bombEntity.setItem(stack);

            // Bomb is launched from the player.
            bombEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, getVelocity(stack), 1.0F);

            // Bomb is spawned server-side
            level.addFreshEntity(bombEntity);
        }

        // If in creative mode, player can use infinite bombs.
        if(!player.getAbilities().instabuild){
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack){
        int tier = 1;
        if(stack.hasTag() && stack.getTag().contains("Tier")){
            tier = stack.getTag().getInt("Tier");
        }

        String baseName = this.getDescriptionId(stack);

        if(tier == 1){
            return Component.translatable(baseName);
        }

        MutableComponent tierName = Component.translatable("tier.bombs." + tier).append(Component.literal(" "));

        return tierName.append(Component.translatable(baseName));
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
            tooltip.add(Component.literal("Modifiers:").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xdddddd))).withStyle(ChatFormatting.BOLD));

            for(int i = 0; i < modifiers.size(); i++){
                String mod = modifiers.getString(i);

                if(!modifierColorMap.containsKey(mod)){
                    continue;
                }

                tooltip.add(Component.literal("- ")
                                .append(Component.translatable("modifier.bombs." + mod))
                                .withStyle(modifierColorMap.get(mod)));

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

    private float getVelocity(ItemStack stack){
        if(BombModifierUtil.hasModifier(stack, "light")){
            return 2.5F;
        }
        return 1.5F;
    }
}
