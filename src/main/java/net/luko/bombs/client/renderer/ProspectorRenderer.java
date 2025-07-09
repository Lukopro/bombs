package net.luko.bombs.client.renderer;

import net.luko.bombs.Bombs;
import net.luko.bombs.client.model.ProspectorModel;
import net.luko.bombs.entity.ProspectorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class ProspectorRenderer extends MobRenderer<ProspectorEntity, ProspectorModel<ProspectorEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/prospector.png");

    public ProspectorRenderer(EntityRendererProvider.Context context) {
        super(context, new ProspectorModel<>(context.bakeLayer(ProspectorModel.LAYER_LOCATION)), 0.5F);

        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(ProspectorEntity pEntity) {
        return TEXTURE;
    }
}
