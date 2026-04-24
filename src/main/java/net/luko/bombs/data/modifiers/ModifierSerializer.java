package net.luko.bombs.data.modifiers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashSet;
import java.util.Set;

public class ModifierSerializer {
    public static void write(FriendlyByteBuf buf, Modifier modifier) {
        modifier.bomb().toNetwork(buf);
        modifier.modifierItem().toNetwork(buf);

        buf.writeBoolean(modifier.specialTag() != null);
        if (modifier.specialTag() != null) {
            buf.writeUtf(modifier.specialTag());
        }

        buf.writeVarInt(modifier.incompatibleWith().size());
        for (String s : modifier.incompatibleWith()) {
            buf.writeUtf(s);
        }

        buf.writeInt(modifier.color());
    }

    public static Modifier read(FriendlyByteBuf buf) {
        Ingredient bomb = Ingredient.fromNetwork(buf);
        Ingredient modifierItem = Ingredient.fromNetwork(buf);

        String specialTag = null;
        if (buf.readBoolean()) {
            specialTag = buf.readUtf(32767);
        }

        int size = buf.readVarInt();
        Set<String> incompatibleWith = new HashSet<>();
        for (int i = 0; i < size; i++) {
            incompatibleWith.add(buf.readUtf());
        }

        int color = buf.readInt();

        return new Modifier(bomb, modifierItem, specialTag, incompatibleWith, color);
    }
}
