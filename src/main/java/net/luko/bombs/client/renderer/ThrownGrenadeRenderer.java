package net.luko.bombs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.luko.bombs.Bombs;
import net.luko.bombs.client.model.GrenadeModel;
import net.luko.bombs.entity.bomb.ThrownGrenadeEntity;
import net.luko.bombs.util.BombTextureUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public class ThrownGrenadeRenderer extends EntityRenderer<ThrownGrenadeEntity> {
    private final GrenadeModel<ThrownGrenadeEntity> model;

    private static final Map<Float, ResourceLocation> TEXTURES = Map.of(
            1.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/grenade.png"),
            2.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/grenade_mid.png"),
            3.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/grenade_max.png"),
            4.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/soul_grenade.png"),
            5.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/soul_grenade_mid.png"),
            6.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/soul_grenade_max.png")
    );

    public ThrownGrenadeRenderer(EntityRendererProvider.Context context){
        super(context);
        this.model = new GrenadeModel<>(context.bakeLayer(GrenadeModel.LAYER_LOCATION));
    }

    @Override
    public void render(ThrownGrenadeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        poseStack.pushPose();

        poseStack.translate(0.0F, 0.2F, 0.0F);

        float speedSqr = (float)entity.getDeltaMovement().lengthSqr();

        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90F));

        float spinScale = Math.min(speedSqr * 100 >= 0.3F ? speedSqr * 100 : 0.0F, 1F);
        float tick = entity.tickCount + partialTicks;
        float spin = entity.lastSpin + (tick - entity.lastTick) * entity.getRandomSpinSpeed() * spinScale;
        entity.lastTick = tick;
        entity.lastSpin = spin;

        poseStack.mulPose(Axis.ZP.rotationDegrees(spin + entity.getInitialForwardTilt()));

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
    public ResourceLocation getTextureLocation(ThrownGrenadeEntity entity){
        return TEXTURES.getOrDefault(
                BombTextureUtil.getTextureIndex(entity.getItem()),
                ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/grenade.png"));
    }
}
