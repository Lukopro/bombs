package net.luko.bombs.item;

import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.entity.ThrownBombEntity;
import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BombItem extends Item {
    // Each instance of BombItem has a hard coded explosionPower.
    private float explosionPower;
    private static final Map<String, ChatFormatting> modifierMap = Map.of(
        "flame", ChatFormatting.RED,
        "light", ChatFormatting.WHITE,
        "contained", ChatFormatting.GREEN,
        "pacified", ChatFormatting.GREEN
    );
    public BombItem(Properties properties, float explosionPower){
        super(properties);
        this.explosionPower = explosionPower;
    }

    public void setExplosionPower(float power){
        this.explosionPower = power;
    }
    // When bomb is in hand and is right clicked, use() is called.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
        ItemStack stack = player.getItemInHand(hand);

        // Server bomb logic
        if(!level.isClientSide()){
            ThrownBombEntity bombEntity = new ThrownBombEntity(ModEntities.THROWN_BOMB.get(), level, player, explosionPower);

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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag){
        super.appendHoverText(stack, level, tooltip, flag);

        if(stack.hasTag() && stack.getTag().contains("Modifiers")){
            ListTag modifiers = stack.getTag().getList("Modifiers", Tag.TAG_STRING);

            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Modifiers:").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GRAY));

            for(int i = 0; i < modifiers.size(); i++){
                String mod = modifiers.getString(i);
                tooltip.add(Component.literal("- ").append(Component.translatable("modifier.bombs." + mod)).withStyle(modifierMap.get(mod)));
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
