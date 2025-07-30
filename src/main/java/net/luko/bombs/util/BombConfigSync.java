package net.luko.bombs.util;

import net.luko.bombs.config.BombsConfig;
import net.luko.bombs.item.bomb.BombItem;

public class BombConfigSync {
    public static void syncBombExplosionPowers(){
        BombItem.setExplosionPowerMapTier(1, BombsConfig.BOMB_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(2, BombsConfig.BOMB_II_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(3, BombsConfig.BOMB_III_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(4, BombsConfig.SOUL_BOMB_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(5, BombsConfig.SOUL_BOMB_II_BASE_POWER.get().floatValue());
        BombItem.setExplosionPowerMapTier(6, BombsConfig.SOUL_BOMB_III_BASE_POWER.get().floatValue());
    }
}
