package net.luko.bombs.data.themes;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class ThemeData {
    public float strength;

    @SerializedName("block1")
    public ResourceLocation block1Id;

    @SerializedName("block2")
    public ResourceLocation block2Id;

    @SerializedName("block3")
    public ResourceLocation block3Id;

    public static ThemeData fromJson(JsonObject json){
        ThemeData theme = new ThemeData();

        theme.strength = GsonHelper.getAsFloat(json, "strength");

        theme.block1Id = ResourceLocation.parse(GsonHelper.getAsString(json, "block1"));
        theme.block2Id = ResourceLocation.parse(GsonHelper.getAsString(json, "block2"));
        theme.block3Id = ResourceLocation.parse(GsonHelper.getAsString(json, "block3"));

        return theme;
    }

    public BlockState getReplacementBlock(float f){
        if(f > -strength / 6.0F && f < 0){
            return getBlock(block1Id);
        } else if(f > -strength / 2.0F && f <= -strength / 6.0F){
            return getBlock(block2Id);
        } else if(f >= -strength && f <= -strength / 2.0F){
            return getBlock(block3Id);
        }
        return null;
    }

    private BlockState getBlock(ResourceLocation id){
        Block block = ForgeRegistries.BLOCKS.getValue(id);
        return block != null ? block.defaultBlockState() : null;
    }

    public float getStrength(){
        return strength;
    }

    public String toString(){
        return ("strength: " + strength + ", blockId1: " + block1Id + ", blockId2: " + block2Id + ", blockId3: " + block3Id);
    }
}
