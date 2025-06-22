package net.luko.bombs.util;

import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.item.BombItem;

public class BombConfigSync {
    public static void syncBombExplosionPowers(){
        BombItem.setExplosionPowerMapTier(1, BombsConfig.BASIC_DYNAMITE_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(2, BombsConfig.STRONG_DYNAMITE_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(3, BombsConfig.BLAZE_DYNAMITE_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(4, BombsConfig.DRAGON_DYNAMITE_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(5, BombsConfig.CRYSTAL_DYNAMITE_BASE_POWER.get().floatValue());
    }
}
