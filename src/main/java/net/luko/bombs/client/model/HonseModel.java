package net.luko.bombs.client.model;

// Made with Blockbench 4.12.5

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class HonseModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("modid", "honse"), "main");
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart saddle;
    private final ModelPart tail;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart mouth;
    private final ModelPart left_ear;
    private final ModelPart right_ear;
    private final ModelPart mane;
    private final ModelPart front_left_leg;
    private final ModelPart front_right_leg;
    private final ModelPart back_right_leg;
    private final ModelPart back_left_leg;

    public HonseModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.saddle = this.body.getChild("saddle");
        this.tail = this.body.getChild("tail");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.mouth = this.head.getChild("mouth");
        this.left_ear = this.head.getChild("left_ear");
        this.right_ear = this.head.getChild("right_ear");
        this.mane = this.head.getChild("mane");
        this.front_left_leg = this.root.getChild("front_left_leg");
        this.front_right_leg = this.root.getChild("front_right_leg");
        this.back_right_leg = this.root.getChild("back_right_leg");
        this.back_left_leg = this.root.getChild("back_left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -10.0F, 0.0F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 25).addBox(-31.0F, -14.0F, -1.0F, 32.0F, 14.0F, 14.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-6.0F, 0.0F, -15.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition saddle = body.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(0, 53).addBox(-7.0F, 0.0F, -5.0F, 14.0F, 9.0F, 18.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -14.0F, 1.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(62, 0).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 18.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -17.0F, 0.0F, 8.0F, 17.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, -16.0F));

        PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(40, 0).addBox(-3.0F, -2.0F, -5.0F, 6.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, 0.0F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(2, 4).addBox(0.55F, -23.0F, 2.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 3.99F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(2, 4).addBox(-2.55F, -23.0F, 2.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 3.99F));

        PartDefinition mane = head.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -21.0F, 4.01F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 3.99F));

        PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(92, 33).addBox(-3.0F, 6.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -16.0F, -13.0F));

        PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(92, 33).addBox(-3.0F, 6.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -16.0F, -13.0F));

        PartDefinition back_right_leg = root.addOrReplaceChild("back_right_leg", CubeListBuilder.create().texOffs(92, 33).addBox(-3.0F, 6.0F, 1.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -16.0F, 9.0F));

        PartDefinition back_left_leg = root.addOrReplaceChild("back_left_leg", CubeListBuilder.create().texOffs(92, 33).addBox(-3.0F, 6.0F, 1.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -16.0F, 9.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}