package net.luko.bombs.util;

import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.item.BombItem;
import net.luko.bombs.item.ModItems;

public class BombConfigSync {
    public static void syncBombExplosionPowers(){
        ((BombItem) ModItems.DYNAMITE.get()).setExplosionPower(BombsConfig.BASIC_DYNAMITE_BASE_POWER.get().floatValue());
        ((BombItem) ModItems.STRONG_DYNAMITE.get()).setExplosionPower(BombsConfig.STRONG_DYNAMITE_BASE_POWER.get().floatValue());
        ((BombItem) ModItems.BLAZE_DYNAMITE.get()).setExplosionPower(BombsConfig.BLAZE_DYNAMITE_BASE_POWER.get().floatValue());
        ((BombItem) ModItems.DRAGON_DYNAMITE.get()).setExplosionPower(BombsConfig.DRAGON_DYNAMITE_BASE_POWER.get().floatValue());
        ((BombItem) ModItems.CRYSTAL_DYNAMITE.get()).setExplosionPower(BombsConfig.CRYSTAL_DYNAMITE_BASE_POWER.get().floatValue());
    }
}
