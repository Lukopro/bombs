package net.luko.bombs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.luko.bombs.Bombs;
import net.luko.bombs.entity.ThrownBombEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings

public class DynamiteModel<T extends ThrownBombEntity> extends EntityModel<T> {

        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        public static final ModelLayerLocation LAYER_LOCATION =
                new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "dynamite"), "main");
        private final ModelPart bb_main;

        public DynamiteModel(ModelPart root) {
            this.bb_main = root.getChild("bb_main");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition bb_main = partdefinition.addOrReplaceChild(
                    "bb_main",
                    CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                    new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, 6.0F, 0.0F));

            PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(16, -4).addBox(0.0F, -5.0F, -1.5F, 0.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

            return LayerDefinition.create(meshdefinition, 32, 32);
        }

        @Override
        public void setupAnim(ThrownBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
            bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
    }
