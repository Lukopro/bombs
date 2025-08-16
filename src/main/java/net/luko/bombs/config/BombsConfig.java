package net.luko.bombs.config;

import net.luko.bombs.item.bomb.DynamiteItem;
import net.luko.bombs.item.bomb.GrenadeItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Map;

public class BombsConfig {
    public static final ModConfigSpec COMMON_CONFIG;

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

    public static ModConfigSpec.BooleanValue ENABLE_DYNAMITE;
    public static ModConfigSpec.BooleanValue ENABLE_GRENADES;

    public static ModConfigSpec.DoubleValue BOMB_BASE_POWER;
    public static ModConfigSpec.DoubleValue BOMB_II_BASE_POWER;
    public static ModConfigSpec.DoubleValue BOMB_III_BASE_POWER;

    public static ModConfigSpec.DoubleValue BOMB_DYNAMITE_BASE_POWER;
    public static ModConfigSpec.DoubleValue BOMB_DYNAMITE_II_BASE_POWER;
    public static ModConfigSpec.DoubleValue BOMB_DYNAMITE_III_BASE_POWER;

    public static ModConfigSpec.ConfigValue<List<? extends String>> CRAFTING_DEFAULT_MODIFIERS;
    public static ModConfigSpec.ConfigValue<List<? extends String>> CRAFTING_RESTRICTED_MODIFIERS;

    public static ModConfigSpec.IntValue BOMB_TIMEOUT_TIME;

    public static ModConfigSpec.DoubleValue PROSPECTOR_SPAWN_CHANCE;

    public static final ModConfigSpec.IntValue PROSPECTOR_GROUP_MIN;
    public static final ModConfigSpec.IntValue PROSPECTOR_GROUP_MAX;

    public static ModConfigSpec.ConfigValue<List<? extends String>> PROSPECTOR_DEFAULT_MODIFIERS;
    public static ModConfigSpec.IntValue PROSPECTOR_AGGRESSION;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Enable/Disable Bombs");

        ENABLE_DYNAMITE = builder
                .comment("Enable/Disable Dynamite crafting.")
                .define("enableDynamite", true);

        ENABLE_GRENADES = builder
                .comment("Enable/Disable Grenade crafting.")
                .define("enableGrenades", true);

        builder.pop();

        builder.push("Bomb Base Damage");

        BOMB_BASE_POWER = builder
                .comment("Base explosion power of any basic Bomb (default: 2.0)")
                .defineInRange("bombBasePower",
                        DEFAULT_POWER_VALUES.get(1),
                        POWER_MIN, POWER_MAX);

        BOMB_II_BASE_POWER = builder
                .comment("Base explosion power of any Bomb II (default: 3.0)")
                .defineInRange("bombIIBasePower",
                        DEFAULT_POWER_VALUES.get(2),
                        POWER_MIN, POWER_MAX);

        BOMB_III_BASE_POWER = builder
                .comment("Base explosion power of any Bomb III (default: 4.0)")
                .defineInRange("bombIIIBasePower",
                        DEFAULT_POWER_VALUES.get(3),
                        POWER_MIN, POWER_MAX);

        BOMB_DYNAMITE_BASE_POWER = builder
                .comment("Base explosion power of any Soul Bomb (default: 5.0)")
                .defineInRange("soulBombBasePower",
                        DEFAULT_POWER_VALUES.get(4),
                        POWER_MIN, POWER_MAX);

        BOMB_DYNAMITE_II_BASE_POWER = builder
                .comment("Base explosion power of any Soul Bomb II (default: 6.0)")
                .defineInRange("soulBombIIBasePower",
                        DEFAULT_POWER_VALUES.get(5),
                        POWER_MIN, POWER_MAX);

        BOMB_DYNAMITE_III_BASE_POWER = builder
                .comment("Base explosion power of any Soul Bomb III (default: 7.0)")
                .defineInRange("soulBombIIIBasePower",
                        DEFAULT_POWER_VALUES.get(6),
                        POWER_MIN, POWER_MAX);

        builder.pop();

        builder.push("Modifiers");

        CRAFTING_DEFAULT_MODIFIERS = builder
                .comment("List of modifiers applied to dynamite by default by crafting")
                .defineList("craftingDefaultModifiers",
                        List.of(),
                        obj -> obj instanceof String);

        CRAFTING_RESTRICTED_MODIFIERS = builder
                .comment("List of modifiers that are not allowed to be applied by crafting")
                .defineList("craftingRestrictedModifiers",
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

        builder.push("Mobs");

        PROSPECTOR_SPAWN_CHANCE = builder
                .comment("What is the chance for prospectors and honses to spawn per minute? (default: 0.001)")
                .defineInRange("prospectorSpawnChance",
                        0.001,
                        0.0, 1.0);

        PROSPECTOR_GROUP_MIN = builder
                .comment("Minimum number of groups (1 honse, 2 prospectors) that try to spawn at a time (default: 2)")
                .defineInRange("prospectorGroupMin",
                        2,
                        0, Integer.MAX_VALUE);

        PROSPECTOR_GROUP_MAX = builder
                .comment("Maximum number of groups (1 honse, 2 prospectors) that try to spawn at a time (default: 3)")
                .defineInRange("prospectorGroupMax",
                        3,
                        0, Integer.MAX_VALUE);

        PROSPECTOR_DEFAULT_MODIFIERS = builder
                .comment("List of modifiers applied to prospector's dynamite on spawn (default: ['contained'])")
                .defineList("prospectorDefaultModifiers",
                        List.of("contained"),
                        obj -> obj instanceof String);

        PROSPECTOR_AGGRESSION = builder
                .comment("How aggressive should Prospectors be? (default: false)")
                .comment("0 = no targetting, 1 = targets players, 2 = targets players + villagers + iron golems, 3 = targets almost everything")
                .defineInRange("prospectorAggression", 1, 0, 3);


        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    public static boolean isBombEnabled(ItemStack bomb){
        if(bomb.getItem() instanceof DynamiteItem && !ENABLE_DYNAMITE.get()) return false;
        if(bomb.getItem() instanceof GrenadeItem && !ENABLE_GRENADES.get()) return false;
        return true;
    }
}
