package net.luko.bombs.item;

import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.entity.ThrownBombEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BombItem extends Item {
    private float explosionPower;

    public BombItem(Properties properties, float explosionPower){
        super(properties);
        this.explosionPower = explosionPower;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide()){
            ThrownBombEntity bombEntity = new ThrownBombEntity(ModEntities.THROWN_BOMB.get(), level, player, explosionPower);
            bombEntity.setItem(stack);
            bombEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(bombEntity);
        }

        if(!player.getAbilities().instabuild){
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
