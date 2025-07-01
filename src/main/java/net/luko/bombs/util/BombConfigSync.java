package net.luko.bombs.util;

import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.item.BombItem;
import net.luko.bombs.item.ModItems;

public class BombConfigSync {
    public static void syncBombExplosionPowers(){
        BombItem.setExplosionPowerMapTier(1, BombsConfig.DYNAMITE_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(2, BombsConfig.DYNAMITE_II_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(3, BombsConfig.DYNAMITE_III_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(4, BombsConfig.SOUL_DYNAMITE_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(5, BombsConfig.SOUL_DYNAMITE_II_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(6, BombsConfig.SOUL_DYNAMITE_III_BASE_POWER.get().floatValue());
    }
}
