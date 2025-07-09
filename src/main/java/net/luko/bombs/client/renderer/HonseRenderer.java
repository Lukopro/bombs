package net.luko.bombs.client.renderer;

import net.luko.bombs.Bombs;
import net.luko.bombs.client.model.HonseModel;
import net.luko.bombs.entity.HonseEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HonseRenderer extends MobRenderer<HonseEntity, HonseModel<HonseEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/honse.png");

    public HonseRenderer(EntityRendererProvider.Context context) {
        super(context, new HonseModel<>(context.bakeLayer(HonseModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(HonseEntity pEntity) {
        return TEXTURE;
    }
}
