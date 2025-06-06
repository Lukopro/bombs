package net.luko.bombs.config;

import net.luko.bombs.item.ModItems;
import net.minecraftforge.common.ForgeConfigSpec;

public class BombsConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final float POWER_MIN = 0.0F;
    public static final float POWER_MAX = 50.0F;

    public static ForgeConfigSpec.DoubleValue BASIC_DYNAMITE_BASE_POWER;
    public static ForgeConfigSpec.DoubleValue STRONG_DYNAMITE_BASE_POWER;
    public static ForgeConfigSpec.DoubleValue BLAZE_DYNAMITE_BASE_POWER;
    public static ForgeConfigSpec.DoubleValue DRAGON_DYNAMITE_BASE_POWER;
    public static ForgeConfigSpec.DoubleValue CRYSTAL_DYNAMITE_BASE_POWER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Dynamite Base Damage");

        BASIC_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of basic Dynamite (default: 1.5)")
                .defineInRange("basicDynamiteBasePower", ModItems.DEFAULT_POWER_VALUES[0], POWER_MIN, POWER_MAX);

        STRONG_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of Strong Dynamite (default: 2.5)")
                .defineInRange("strongDynamiteBasePower", ModItems.DEFAULT_POWER_VALUES[1], POWER_MIN, POWER_MAX);

        BLAZE_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of Blaze Dynamite (default: 4.0)")
                .defineInRange("blazeDynamiteBasePower", ModItems.DEFAULT_POWER_VALUES[2], POWER_MIN, POWER_MAX);

        DRAGON_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of Dragon Dynamite (default: 5.5)")
                .defineInRange("dragonDynamiteBasePower", ModItems.DEFAULT_POWER_VALUES[3], POWER_MIN, POWER_MAX);

        CRYSTAL_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of Crystal Dynamite (default: 7.0)")
                .defineInRange("crystalDynamiteBasePower", ModItems.DEFAULT_POWER_VALUES[4], POWER_MIN, POWER_MAX);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}
