package net.luko.bombs.data.modifiers;

import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record Modifier (
        Ingredient bomb,
        Ingredient modifierItem,
        @Nullable String specialTag,
        Set<String> incompatibleWith,
        int color
){}
