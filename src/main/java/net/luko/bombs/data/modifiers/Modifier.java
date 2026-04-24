package net.luko.bombs.data.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record Modifier (
        Ingredient bomb,
        Ingredient modifierItem,
        @Nullable String specialTag,
        Set<String> incompatibleWith,
        int color
){
    public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Ingredient.CODEC.fieldOf("bomb").forGetter(Modifier::bomb),
                Ingredient.CODEC.fieldOf("modifier_item").forGetter(Modifier::modifierItem),
                Codec.STRING.optionalFieldOf("special_tag").forGetter(m -> Optional.ofNullable(m.specialTag)),
                Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf)
                        .fieldOf("incompatible_with").forGetter(Modifier::incompatibleWith),
                Codec.INT.fieldOf("color").forGetter(Modifier::color)
            ).apply(instance, (bomb, item, tag, incompatible, color) ->
                new Modifier(bomb, item, tag.orElse(null), incompatible, color)
            )
    );
}
