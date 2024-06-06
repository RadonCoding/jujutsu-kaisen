package radon.jujutsu_kaisen.client.model.entity.effect;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.effect.CursedEnergyBlastEntity;
import radon.jujutsu_kaisen.entity.effect.FireBeamEntity;

public class FireBeamModel extends EntityModel<FireBeamEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "fire_beam"), "main");

    private final ModelPart body1;
    private final ModelPart body2;
    private final ModelPart body3;
    private final ModelPart body4;

    public FireBeamModel(ModelPart root) {
        this.body1 = root.getChild("body1");
        this.body2 = root.getChild("body2");
        this.body3 = root.getChild("body3");
        this.body4 = root.getChild("body4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("body1", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, CubeDeformation.NONE),
                PartPose.offset(0.0F, 2.0F, 0.0F));
        part.addOrReplaceChild("body2", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, CubeDeformation.NONE),
                PartPose.offset(0.0F, 2.0F, 0.0F));
        part.addOrReplaceChild("body3", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, -4.0F, -4.0F, 8,8, 8, CubeDeformation.NONE),
                PartPose.offset(0.0F, 2.0F, 0.0F));
        part.addOrReplaceChild("body4", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, CubeDeformation.NONE),
                PartPose.offset(0.0F, 2.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack pPoseStack, @NotNull VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.body4.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.body3.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.body2.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.body1.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }

    private void setScale(ModelPart part, float xScale, float yScale, float zScale) {
        part.xScale = xScale;
        part.yScale = yScale;
        part.zScale = zScale;
    }

    @Override
    public void setupAnim(@NotNull FireBeamEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        float fraction = Math.min(1.0F, pAgeInTicks / pEntity.getCharge());
        float scale = (float) (Math.pow(fraction, 0.5F) * 3 + 0.05F * Math.cos(pAgeInTicks * 3));

        this.setScale(this.body4, scale * 0.4F, scale * 0.4F, scale * 0.4F);
        this.setScale(this.body3, scale * 0.6F, scale * 0.6F, scale * 0.6F);
        this.setScale(this.body2, scale * 0.8F, scale * 0.8F, scale * 0.8F);
        this.setScale(this.body1, scale, scale, scale);
    }
}
