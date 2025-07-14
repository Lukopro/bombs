package net.luko.bombs.client.renderer;

import net.luko.bombs.Bombs;
import net.luko.bombs.client.model.HonseModel;
import net.luko.bombs.entity.HonseEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class HonseRenderer extends MobRenderer<HonseEntity, HonseModel<HonseEntity>> {
    private static final ResourceLocation BASE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/honse.png");
    private static final Map<Integer, ResourceLocation> TEXTURE_MAP = Map.ofEntries(
            Map.entry(0, BASE_TEXTURE),
            Map.entry(1, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/dark_honse.png"))
    );

    public HonseRenderer(EntityRendererProvider.Context context) {
        super(context, new HonseModel<>(context.bakeLayer(HonseModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(HonseEntity pEntity) {
        return TEXTURE_MAP.getOrDefault(pEntity.getColor(), BASE_TEXTURE);
    }
}
