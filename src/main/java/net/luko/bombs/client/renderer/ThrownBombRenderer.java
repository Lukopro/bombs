package net.luko.bombs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.luko.bombs.Bombs;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.client.model.DynamiteModel;
import net.luko.bombs.entity.ThrownBombEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;

public class ThrownBombRenderer extends EntityRenderer<ThrownBombEntity> {
    private final DynamiteModel<ThrownBombEntity> model;

    private static final Map<Item, ResourceLocation> TEXTURES = Map.of(
        ModItems.BASIC_DYNAMITE.get(), new ResourceLocation(Bombs.MODID, "textures/entity/basic_dynamite.png"),
        ModItems.STRONG_DYNAMITE.get(), new ResourceLocation(Bombs.MODID, "textures/entity/strong_dynamite.png"),
        ModItems.REDSTONE_DYNAMITE.get(), new ResourceLocation(Bombs.MODID, "textures/entity/redstone_dynamite.png"),
        ModItems.CHORAL_DYNAMITE.get(), new ResourceLocation(Bombs.MODID, "textures/entity/choral_dynamite.png"),
        ModItems.CRYSTAL_DYNAMITE.get(), new ResourceLocation(Bombs.MODID, "textures/entity/crystal_dynamite.png")
    );
    public ThrownBombRenderer(EntityRendererProvider.Context context){
        super(context);
        Bombs.LOGGER.warn("ThrownBombRenderer initialized");
        this.model = new DynamiteModel<>(context.bakeLayer(DynamiteModel.LAYER_LOCATION));
    }

    @Override
    public void render(ThrownBombEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        poseStack.pushPose();
        poseStack.translate(0.0F, -0.5F, 0.0F);
        model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(getTextureLocation(entity))),
                packedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownBombEntity entity){
        return TEXTURES.getOrDefault(entity.getItem().getItem(),
            new ResourceLocation(Bombs.MODID, "textures/entity/basic_dynamite.png"));
    }
}
