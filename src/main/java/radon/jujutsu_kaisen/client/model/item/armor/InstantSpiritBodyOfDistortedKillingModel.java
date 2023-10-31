package radon.jujutsu_kaisen.client.model.item.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class InstantSpiritBodyOfDistortedKillingModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/item/armor/instant_spirit_body_of_distorted_killing.png");
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "instant_spirit_body_of_distorted_killing"), "main");

    public InstantSpiritBodyOfDistortedKillingModel(ModelPart pRoot) {
        super(pRoot);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bipedHead = partdefinition.addOrReplaceChild("bipedHead", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition armorHead = bipedHead.addOrReplaceChild("armorHead", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.01F))
                .texOffs(0, 0).addBox(-1.0F, -4.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = armorHead.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(26, 10).addBox(-4.5F, -7.0F, -4.1F, 9.0F, 7.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.0175F, 0.0F, 0.0F));

        PartDefinition cloth_1 = armorHead.addOrReplaceChild("cloth_1", CubeListBuilder.create().texOffs(44, 37).addBox(0.0F, -2.5F, 0.0F, 0.0F, 4.0F, 11.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.6F, -2.5F, 3.8F, 0.22F, 0.1278F, 0.0285F));

        PartDefinition cloth_end_1 = cloth_1.addOrReplaceChild("cloth_end_1", CubeListBuilder.create().texOffs(40, 32).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -0.5F, 11.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition cloth_2 = armorHead.addOrReplaceChild("cloth_2", CubeListBuilder.create().texOffs(44, 37).addBox(0.0F, -2.5F, 0.0F, 0.0F, 4.0F, 11.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.1F, -2.5F, 3.8F, -0.1745F, 0.0F, 0.0F));

        PartDefinition cloth_end_2 = cloth_2.addOrReplaceChild("cloth_end_2", CubeListBuilder.create().texOffs(32, 11).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -0.5F, 11.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition cloth_3 = armorHead.addOrReplaceChild("cloth_3", CubeListBuilder.create().texOffs(44, 37).addBox(0.0F, -2.5F, 0.0F, 0.0F, 4.0F, 11.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.6F, -2.5F, 3.8F, 0.2233F, -0.2129F, -0.0479F));

        PartDefinition cloth_end_3 = cloth_3.addOrReplaceChild("cloth_end_3", CubeListBuilder.create().texOffs(32, 11).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -0.5F, 11.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition bipedBody = partdefinition.addOrReplaceChild("bipedBody", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition armorBody = bipedBody.addOrReplaceChild("armorBody", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
                .texOffs(0, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition tail_0 = armorBody.addOrReplaceChild("tail_0", CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -2.1F, -0.5F, 3.0F, 4.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-0.5F, 10.0F, 1.8F, -0.3054F, 0.0F, 0.0F));

        PartDefinition tail_1 = tail_0.addOrReplaceChild("tail_1", CubeListBuilder.create().texOffs(32, 0).addBox(-0.5F, -1.5F, 0.0F, 3.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, 4.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition tail_2 = tail_1.addOrReplaceChild("tail_2", CubeListBuilder.create().texOffs(32, 0).addBox(-0.5F, -1.5F, -0.8F, 3.0F, 4.0F, 5.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.8F, 0.1745F, 0.0F, 0.0F));

        PartDefinition tail_3 = tail_2.addOrReplaceChild("tail_3", CubeListBuilder.create().texOffs(32, 0).addBox(-0.5F, -1.5F, -0.6F, 3.0F, 4.0F, 5.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.8F, 0.2182F, 0.0F, 0.0F));

        PartDefinition tail_4 = tail_3.addOrReplaceChild("tail_4", CubeListBuilder.create().texOffs(61, 32).addBox(-0.5F, -2.1126F, -0.9826F, 3.0F, 4.0F, 7.0F, new CubeDeformation(-0.6F)), PartPose.offsetAndRotation(0.0F, 0.7F, 3.8F, 0.3054F, 0.0F, 0.0F));

        PartDefinition bipedLeftArm = partdefinition.addOrReplaceChild("bipedLeftArm", CubeListBuilder.create(), PartPose.offset(-4.0F, 2.0F, 0.0F));

        PartDefinition armorLeftArm = bipedLeftArm.addOrReplaceChild("armorLeftArm", CubeListBuilder.create().texOffs(44, 52).addBox(-12.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.01F))
                .texOffs(66, 43).addBox(-12.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(8.0F, 0.0F, 0.0F));

        PartDefinition cube_r2 = armorLeftArm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 30).addBox(-10.0F, -5.0F, 3.0F, 0.0F, 10.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -2.6F, 1.2F, -0.6109F, 0.0F, 0.0F));

        PartDefinition bipedRightArm = partdefinition.addOrReplaceChild("bipedRightArm", CubeListBuilder.create(), PartPose.offset(4.0F, 2.0F, 0.0F));

        PartDefinition armorRightArm = bipedRightArm.addOrReplaceChild("armorRightArm", CubeListBuilder.create().texOffs(52, 23).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.01F))
                .texOffs(64, 16).addBox(1.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r3 = armorRightArm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(24, 30).addBox(10.0F, -5.0F, 3.0F, 0.0F, 10.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-8.0F, -2.6F, 1.2F, -0.6109F, 0.0F, 0.0F));

        PartDefinition bipedLeftLeg = partdefinition.addOrReplaceChild("bipedLeftLeg", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));

        PartDefinition armorLeftLeg = bipedLeftLeg.addOrReplaceChild("armorLeftLeg", CubeListBuilder.create().texOffs(50, 0).addBox(6.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(-4.0F, 0.0F, 0.0F));

        PartDefinition armorLeftBoot = bipedLeftLeg.addOrReplaceChild("armorLeftBoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bipedRightLeg = partdefinition.addOrReplaceChild("bipedRightLeg", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition armorRightLeg = bipedRightLeg.addOrReplaceChild("armorRightLeg", CubeListBuilder.create().texOffs(0, 48).addBox(-6.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition armorRightBoot = bipedRightLeg.addOrReplaceChild("armorRightBoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
}