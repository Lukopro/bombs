package net.luko.bombs.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Map;

public class BombsConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;

    private static final float POWER_MIN = 0.0F;
    private static final float POWER_MAX = 100.0F;

    private static final Map<Integer, Float> DEFAULT_POWER_VALUES = Map.of(
            1, 2.0F,
            2, 3.0F,
            3, 4.0F,
            4, 5.0F,
            5, 6.0F,
            6, 7.0F
    );

    public static ForgeConfigSpec.DoubleValue DYNAMITE_BASE_POWER;
    public static ForgeConfigSpec.DoubleValue DYNAMITE_II_BASE_POWER;
    public static ForgeConfigSpec.DoubleValue DYNAMITE_III_BASE_POWER;

    public static ForgeConfigSpec.DoubleValue SOUL_DYNAMITE_BASE_POWER;
    public static ForgeConfigSpec.DoubleValue SOUL_DYNAMITE_II_BASE_POWER;
    public static ForgeConfigSpec.DoubleValue SOUL_DYNAMITE_III_BASE_POWER;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DEFAULT_MODIFIERS;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> RESTRICTED_MODIFIERS;

    public static ForgeConfigSpec.IntValue BOMB_TIMEOUT_TIME;

    public static ForgeConfigSpec.DoubleValue PROSPECTOR_SPAWN_CHANCE;
    public static ForgeConfigSpec.IntValue PROSPECTOR_GROUP_MIN;
    public static ForgeConfigSpec.IntValue PROSPECTOR_GROUP_MAX;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Dynamite Base Damage");

        DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of basic Dynamite (default: 2.0)")
                .defineInRange("dynamiteBasePower",
                        DEFAULT_POWER_VALUES.get(1),
                        POWER_MIN, POWER_MAX);

        DYNAMITE_II_BASE_POWER = builder
                .comment("Base explosion power of Dynamite II (default: 3.0)")
                .defineInRange("dynamiteIIBasePower",
                        DEFAULT_POWER_VALUES.get(2),
                        POWER_MIN, POWER_MAX);

        DYNAMITE_III_BASE_POWER = builder
                .comment("Base explosion power of Dynamite III (default: 4.0)")
                .defineInRange("dynamiteIIIBasePower",
                        DEFAULT_POWER_VALUES.get(3),
                        POWER_MIN, POWER_MAX);

        SOUL_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of Soul Dynamite (default: 5.0)")
                .defineInRange("soulDynamiteBasePower",
                        DEFAULT_POWER_VALUES.get(4),
                        POWER_MIN, POWER_MAX);

        SOUL_DYNAMITE_II_BASE_POWER = builder
                .comment("Base explosion power of Soul Dynamite II (default: 6.0)")
                .defineInRange("soulDynamiteIIBasePower",
                        DEFAULT_POWER_VALUES.get(5),
                        POWER_MIN, POWER_MAX);

        SOUL_DYNAMITE_III_BASE_POWER = builder
                .comment("Base explosion power of Soul Dynamite III (default: 7.0)")
                .defineInRange("soulDynamiteIIIBasePower",
                        DEFAULT_POWER_VALUES.get(6),
                        POWER_MIN, POWER_MAX);

        builder.pop();

        builder.push("Modifiers");

        DEFAULT_MODIFIERS = builder
                .comment("List of modifiers applied to dynamite by default")
                .defineList("defaultModifiers",
                        List.of(),
                        obj -> obj instanceof String);

        RESTRICTED_MODIFIERS = builder
                .comment("List of modifiers that are not allowed to be applied")
                .defineList("restrictedModifiers",
                        List.of(),
                        obj -> obj instanceof String);

        builder.pop();

        builder.push("Performance");

        BOMB_TIMEOUT_TIME = builder
                .comment("How many ticks should a bomb exist before it despawns? (default: 1200)")
                .defineInRange("bombTimeoutTime",
                        1200,
                        40, Integer.MAX_VALUE);

        builder.pop();

        builder.push("Mob Spawning");

        PROSPECTOR_SPAWN_CHANCE = builder
                .comment("What is the chance for prospectors and honses to spawn per minute? (default: 0.001)")
                .defineInRange("prospectorSpawnChance",
                        0.001,
                        0.0, 1.0);

        PROSPECTOR_GROUP_MIN = builder
                .comment("Minimum number of groups (1 honse, 2 prospectors) to spawn at a time (default: 2)")
                .defineInRange("prospectorGroupMin",
                        2,
                        0, Integer.MAX_VALUE);

        PROSPECTOR_GROUP_MAX = builder
                .comment("Maximum number of groups (1 honse, 2 prospectors) to spawn at a time (default: 3)")
                .defineInRange("prospectorGroupMax",
                        3,
                        0, Integer.MAX_VALUE);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}
