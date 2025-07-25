package net.luko.bombs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.luko.bombs.Bombs;
import net.luko.bombs.client.model.DynamiteModel;
import net.luko.bombs.entity.bomb.ThrownBombEntity;
import net.luko.bombs.entity.bomb.ThrownDynamiteEntity;
import net.luko.bombs.util.BombTextureUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class ThrownDynamiteRenderer extends EntityRenderer<ThrownDynamiteEntity> {
    private final DynamiteModel<ThrownDynamiteEntity> model;

    private static final Map<Float, ResourceLocation> TEXTURES = Map.of(
            0.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/weiner_dynamite.png"),
            1.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/dynamite.png"),
            2.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/dynamite_mid.png"),
            3.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/dynamite_max.png"),
            4.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/soul_dynamite.png"),
            5.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/soul_dynamite_mid.png"),
            6.0F, ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/soul_dynamite_max.png")
    );

    public ThrownDynamiteRenderer(EntityRendererProvider.Context context){
        super(context);
        this.model = new DynamiteModel<>(context.bakeLayer(DynamiteModel.LAYER_LOCATION));
    }

    @Override
    public void render(ThrownDynamiteEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        poseStack.pushPose();

        poseStack.translate(0.0F, 0.0F, 0.0F);

        float spin = (entity.tickCount + partialTicks) * entity.getRandomSpinSpeed();
        float YRotOffset = 90.0F - (ThrownBombEntity.RANDOM_SIDE_TILT_MAX / 2) + entity.getRandomSideTilt();
        poseStack.mulPose(Axis.YP.rotationDegrees(YRotOffset + entity.getYRot()));

        poseStack.mulPose(Axis.ZP.rotationDegrees(spin + entity.getInitialForwardTilt()));

        spawnFlameParticles(entity, partialTicks);

        model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(model.renderType(getTextureLocation(entity))),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1F, 1F, 1F, 1F);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void spawnFlameParticles(ThrownBombEntity entity, float partialTicks){
        int tier = (entity.getItem().hasTag() && entity.getItem().getTag().contains("Tier")) ? entity.getItem().getTag().getInt("Tier") : 1;

        float currentParticleTick = entity.tickCount + partialTicks;
        float elapsedTicks = currentParticleTick - entity.lastParticleTick;


        float particlesPerTick = (float)(Math.abs((tier - 1) % 3) + 1);

        entity.particlesToSpawn += elapsedTicks * particlesPerTick;
        int particlesSpawned = (int) Math.floor(entity.particlesToSpawn);

        if(particlesSpawned <= 0) return;

        entity.lastParticleTick = currentParticleTick;
        entity.particlesToSpawn -= particlesSpawned;

        Vec3 fusePosLast = getFuseWorldPos(entity, partialTicks - elapsedTicks);
        Vec3 fusePosNow = getFuseWorldPos(entity, partialTicks);

        SimpleParticleType type = (tier >= 4 && tier <= 6) ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;

        for(int i = 0; i < particlesSpawned; i++){
            float t = (float) i / particlesSpawned;
            double px = Mth.lerp(t, fusePosLast.x, fusePosNow.x);
            double py = Mth.lerp(t, fusePosLast.y, fusePosNow.y);
            double pz = Mth.lerp(t, fusePosLast.z, fusePosNow.z);
            Minecraft.getInstance().level.addParticle(
                    type,
                    px, py, pz,
                    Minecraft.getInstance().level.random.nextGaussian() * 0.002,
                    Minecraft.getInstance().level.random.nextGaussian() * 0.002,
                    Minecraft.getInstance().level.random.nextGaussian() * 0.002);
        }
    }

    private static Vec3 rotate(Vec3 vec, float xRot, float yRot, float zRot){
        double x = Math.toRadians(xRot), y = Math.toRadians(yRot), z = Math.toRadians(zRot);

        //Apply Z rotation
        double cosZ = Math.cos(z), sinZ = Math.sin(z);
        double x1 = vec.x * cosZ - vec.y * sinZ;
        double y1 = vec.x * sinZ + vec.y * cosZ;
        double z1 = vec.z;

        //Apply X rotation
        double cosX = Math.cos(x), sinX = Math.sin(x);
        double y2 = y1 * cosX - z1 * sinX;
        double z2 = y1 * sinX + z1 * cosX;
        double x2 = x1;

        //Apply Y rotation
        double cosY = Math.cos(y), sinY = Math.sin(y);
        double x3 = x2 * cosY - z2 * sinY;
        double z3 = -x2 * sinY + z2 * cosY;
        double y3 = y2;

        return new Vec3(x3, y3, z3);
    }

    private Vec3 getFuseWorldPos(ThrownBombEntity entity, float partialTicks){
        double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());

        Vec3 fuseOffset = new Vec3(0, -0.5, 0);

        float spin = (entity.tickCount + partialTicks) * entity.getRandomSpinSpeed();
        float totalXRot = 0;
        float totalYRot = entity.getYRot() + (90.0F - (ThrownBombEntity.RANDOM_SIDE_TILT_MAX / 2) + entity.getRandomSideTilt());
        float totalZRot = spin + entity.getInitialForwardTilt();

        Vec3 rotatedOffset = rotate(fuseOffset, totalXRot, totalYRot, totalZRot);

        return new Vec3(x + rotatedOffset.x, y + rotatedOffset.y, z + rotatedOffset.z);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownDynamiteEntity entity){
        return TEXTURES.getOrDefault(
                BombTextureUtil.getTextureIndex(entity.getItem()),
                ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/entity/dynamite.png"));
    }
}
