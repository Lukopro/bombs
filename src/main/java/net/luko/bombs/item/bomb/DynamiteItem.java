package net.luko.bombs.item.bomb;

import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.entity.ModEntities;
import net.luko.bombs.entity.bomb.ThrownBombEntity;
import net.luko.bombs.entity.bomb.ThrownDynamiteEntity;
import net.luko.bombs.util.BombModifierUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DynamiteItem extends BombItem{
    public DynamiteItem(Properties properties) {
        super(properties);
    }

    @Override
    public ThrownDynamiteEntity createBombEntity(Level level, float explosionPower) {
        return new ThrownDynamiteEntity(ModEntities.THROWN_DYNAMITE.get(), level, explosionPower);
    }

    @Override
    public ThrownDynamiteEntity createBombEntity(Level level, LivingEntity thrower, float explosionPower) {
        return new ThrownDynamiteEntity(ModEntities.THROWN_DYNAMITE.get(), level, thrower, explosionPower);
    }

    @Override
    public SoundEvent getThrowSound(ItemStack stack){
        int tier = stack.getOrDefault(ModDataComponents.TIER.get(), 1);
        if(tier >= 4) return SoundEvents.WITHER_SHOOT;
        else if (tier <= 0) return SoundEvents.EGG_THROW;
        return SoundEvents.FIRECHARGE_USE;
    }

    @Override
    public Component getBaseName(ItemStack stack){
        return Component.translatable("item.bombs.dynamite");
    }
}
