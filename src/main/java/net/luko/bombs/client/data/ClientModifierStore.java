package net.luko.bombs.client.data;

import net.luko.bombs.data.modifiers.Modifier;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class ClientModifierStore {
    private static Map<String, Modifier> modifiers = new HashMap<>();

    public static void setModifiers(Map<String, Modifier> newData) {
        modifiers = newData;
    }

    public static Modifier get(String id) {
        return modifiers.get(id);
    }

    public static Ingredient getModifierItem(String mod){
        Modifier modifier = modifiers.get(mod);
        assert modifier != null;
        return modifier.modifierItem();
    }

    public static TextColor getColor(String mod){
        Modifier modifier = modifiers.get(mod);
        return modifier == null ? TextColor.fromRgb(0x3d372e) : TextColor.fromRgb(modifier.color());
    }
}
