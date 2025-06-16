package net.luko.bombs.config;

import net.luko.bombs.item.ModItems;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Map;

public class BombsConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;

    private static final float POWER_MIN = 0.0F;
    private static final float POWER_MAX = 50.0F;

    private static final Map<String, Float> DEFAULT_POWER_VALUES = Map.of(
            "basic", 1.5F,
            "strong", 2.5F,
            "blaze", 4.0F,
            "dragon", 5.5F,
            "crystal", 7.0F
    );

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
                .defineInRange("basicDynamiteBasePower",
                        DEFAULT_POWER_VALUES.get("basic"),
                        POWER_MIN, POWER_MAX);

        STRONG_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of Strong Dynamite (default: 2.5)")
                .defineInRange("strongDynamiteBasePower",
                        DEFAULT_POWER_VALUES.get("strong"),
                        POWER_MIN, POWER_MAX);

        BLAZE_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of Blaze Dynamite (default: 4.0)")
                .defineInRange("blazeDynamiteBasePower",
                        DEFAULT_POWER_VALUES.get("blaze"),
                        POWER_MIN, POWER_MAX);

        DRAGON_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of Dragon Dynamite (default: 5.5)")
                .defineInRange("dragonDynamiteBasePower",
                        DEFAULT_POWER_VALUES.get("dragon"),
                        POWER_MIN, POWER_MAX);

        CRYSTAL_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of Crystal Dynamite (default: 7.0)")
                .defineInRange("crystalDynamiteBasePower",
                        DEFAULT_POWER_VALUES.get("crystal"),
                        POWER_MIN, POWER_MAX);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}
