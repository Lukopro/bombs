package net.luko.bombs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
        ModItems.DYNAMITE.get(), new ResourceLocation(Bombs.MODID, "textures/entity/dynamite.png"),
        ModItems.STRONG_DYNAMITE.get(), new ResourceLocation(Bombs.MODID, "textures/entity/strong_dynamite.png"),
        ModItems.BLAZE_DYNAMITE.get(), new ResourceLocation(Bombs.MODID, "textures/entity/blaze_dynamite.png"),
        ModItems.DRAGON_DYNAMITE.get(), new ResourceLocation(Bombs.MODID, "textures/entity/dragon_dynamite.png"),
        ModItems.CRYSTAL_DYNAMITE.get(), new ResourceLocation(Bombs.MODID, "textures/entity/crystal_dynamite.png")
    );
    public ThrownBombRenderer(EntityRendererProvider.Context context){
        super(context);
        this.model = new DynamiteModel<>(context.bakeLayer(DynamiteModel.LAYER_LOCATION));
    }

    @Override
    public void render(ThrownBombEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        poseStack.pushPose();

        poseStack.translate(0.0F, 0.0F, 0.0F);

        float spin = (entity.tickCount + partialTicks) * 20;
        float YRotOffset = 90.0F - (ThrownBombEntity.RANDOM_TILT_MAX / 2) + entity.getRandomTilt();
        poseStack.mulPose(Axis.YP.rotationDegrees(YRotOffset + entity.getYRot()));

        poseStack.mulPose(Axis.ZP.rotationDegrees(spin));

        model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(model.renderType(getTextureLocation(entity))),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1F, 1F, 1F, 1F);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownBombEntity entity){
        return TEXTURES.getOrDefault(entity.getItem().getItem(),
            new ResourceLocation(Bombs.MODID, "textures/entity/dynamite.png"));
    }
}
