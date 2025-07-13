package net.luko.bombs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class ProspectorModel<T extends LivingEntity> extends HumanoidModel<T> implements ArmedModel {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("modid", "prospector"), "main");
    //private final ModelPart root;

    private final ModelPart lip_right;
    private final ModelPart lip_left;
    private final ModelPart nose;


    public ProspectorModel(ModelPart root) {
        super(root);
        //this.root = root.getChild("root");

        this.lip_right = this.hat.getChild("lip_right");
        this.lip_left = this.hat.getChild("lip_left");
        this.nose = this.head.getChild("nose");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();

        PartDefinition root = meshdefinition.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 23).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-6.0F, -2.0F, -4.0F, 12.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition hat = root.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(28, 8).addBox(-4.5F, -11.0F, -4.5F, 9.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 43).addBox(-6.5F, -6.04F, -7.0F, 13.0F, 0.04F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.5F, 0.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition lip_right = hat.addOrReplaceChild("lip_right", CubeListBuilder.create().texOffs(13, 44).addBox(0.79F, -8.96F, -7.0F, 0.04F, 3.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

        PartDefinition lip_left = hat.addOrReplaceChild("lip_left", CubeListBuilder.create().texOffs(13, 41).addBox(-0.825F, -8.96F, -7.0F, 0.04F, 3.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(44, 25).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, -10.0F, 0.0F));

        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 25).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 25).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(12, 25).addBox(0.0F, 10.0F, 2.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -12.0F, 0.0F));

        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 25).addBox(0.0F, 10.0F, 2.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
