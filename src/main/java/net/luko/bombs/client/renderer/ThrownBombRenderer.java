package net.luko.bombs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.luko.bombs.Bombs;
import net.luko.bombs.item.ModItems;
import net.luko.bombs.client.model.DynamiteModel;
import net.luko.bombs.entity.ThrownBombEntity;
import net.luko.bombs.util.BombTextureUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;

public class ThrownBombRenderer extends EntityRenderer<ThrownBombEntity> {
    private final DynamiteModel<ThrownBombEntity> model;

    private static final Map<Float, ResourceLocation> TEXTURES = Map.of(
        1.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/dynamite.png"),
            2.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/strong_dynamite.png"),
            3.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/blaze_dynamite.png"),
            4.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/dragon_dynamite.png"),
            5.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/crystal_dynamite.png"),
            6.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/dynamite_modified.png"),
            7.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/strong_dynamite_modified.png"),
            8.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/blaze_dynamite_modified.png"),
            9.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/dragon_dynamite_modified.png"),
            10.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/crystal_dynamite_modified.png")
    );
    public ThrownBombRenderer(EntityRendererProvider.Context context){
        super(context);
        this.model = new DynamiteModel<>(context.bakeLayer(DynamiteModel.LAYER_LOCATION));
    }

    @Override
    public void render(ThrownBombEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        poseStack.pushPose();

        poseStack.translate(0.0F, 0.0F, 0.0F);

        float spin = (entity.tickCount + partialTicks) * entity.getRandomSpinSpeed();
        float YRotOffset = 90.0F - (ThrownBombEntity.RANDOM_SIDE_TILT_MAX / 2) + entity.getRandomSideTilt();
        poseStack.mulPose(Axis.YP.rotationDegrees(YRotOffset + entity.getYRot()));

        poseStack.mulPose(Axis.ZP.rotationDegrees(spin - (ThrownBombEntity.RANDOM_FORWARD_TILT_MAX / 2) + entity.getRandomForwardTilt()));

        model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(model.renderType(getTextureLocation(entity))),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                0xFFFFFF);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownBombEntity entity){
        return TEXTURES.getOrDefault(
                        BombTextureUtil.getTextureIndex(entity.getItem()),
                        ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/dynamite.png"));
    }
}
