package radon.jujutsu_kaisen.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.BurntEntity;

import java.util.HashMap;
import java.util.Map;

public class BurntRenderer extends EntityRenderer<BurntEntity> {
    private static final ResourceLocation CRACK = new ResourceLocation("textures/block/destroy_stage_9.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/block/basalt_top.png");

    private final Map<String, EntityModel<?>> modelMap = new HashMap<>();
    private final Map<String, Entity> hollowEntityMap = new HashMap<>();
    private final EntityRendererProvider.Context context;

    public BurntRenderer(EntityRendererProvider.Context context) {
        super(context);

        this.context = context;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BurntEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    protected void preRenderCallback(BurntEntity entity, PoseStack poseStack) {
        float scale = entity.getScale() < 0.01F ? 1F : entity.getScale();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public void render(BurntEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        EntityModel model = null;

        if (this.modelMap.get(pEntity.getTrappedEntityTypeString()) != null) {
            model = this.modelMap.get(pEntity.getTrappedEntityTypeString());
        } else {
            EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(pEntity.getTrappedEntityType());

            if (renderer instanceof RenderLayerParent) {
                model = ((RenderLayerParent<?, ?>) renderer).getModel();
            } else if (pEntity.getTrappedEntityType() == EntityType.PLAYER) {
                model = new HumanoidModel<>(this.context.bakeLayer(ModelLayers.PLAYER));
            }
            this.modelMap.put(pEntity.getTrappedEntityTypeString(), model);
        }

        if (model == null) return;

        Entity fake = null;

        if (this.hollowEntityMap.get(pEntity.getTrappedEntityTypeString()) == null) {
            Entity build = pEntity.getTrappedEntityType().create(Minecraft.getInstance().level);

            if (build != null) {
                build.load(pEntity.getTrappedTag());
                fake = this.hollowEntityMap.putIfAbsent(pEntity.getTrappedEntityTypeString(), build);
            }
        } else {
            fake = this.hollowEntityMap.get(pEntity.getTrappedEntityTypeString());
        }

        VertexConsumer burnt = pBuffer.getBuffer(JJKRenderTypes.burnt(TEXTURE));

        pPoseStack.pushPose();
        float yaw = pEntity.yRotO + (pEntity.getYRot() - pEntity.yRotO) * pPartialTick;
        boolean shouldSit = pEntity.isPassenger() && (pEntity.getVehicle() != null && pEntity.getVehicle().shouldRiderSit());
        model.young = pEntity.isBaby();
        model.riding = shouldSit;
        model.attackTime = pEntity.getAttackAnim(pPartialTick);

        if (fake != null) {
            model.setupAnim(fake, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
        }
        this.preRenderCallback(pEntity, pPoseStack);
        pPoseStack.translate(0.0F, 1.5F, 0.0F);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        model.renderToBuffer(pPoseStack, burnt, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();

        VertexConsumer crack = pBuffer.getBuffer( JJKRenderTypes.crack(CRACK));
        pPoseStack.pushPose();
        pPoseStack.pushPose();
        preRenderCallback(pEntity, pPoseStack);
        pPoseStack.translate(0, 1.5F, 0);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        model.renderToBuffer(pPoseStack, crack, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();
        pPoseStack.popPose();
    }
}