package radon.jujutsu_kaisen.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.CharredEntity;

import java.util.HashMap;
import java.util.Map;

public class CharredRenderer extends EntityRenderer<CharredEntity> {
    private static final ResourceLocation CHARRED = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/charred.png");
    private static final ResourceLocation EXPLODING = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/exploding.png");

    private final EntityRendererProvider.Context ctx;

    public CharredRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.ctx = pContext;
    }

    @Override
    public void render(@NotNull CharredEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        EntityType<?> type = pEntity.getEntity();

        if (!(type.create(pEntity.level()) instanceof LivingEntity entity)) return;

        EntityModel<LivingEntity> model = null;

        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(type);

        if (renderer instanceof RenderLayerParent) {
            model = (EntityModel<LivingEntity>) ((RenderLayerParent<?, ?>) renderer).getModel();
        } else if (pEntity.getEntity() == EntityType.PLAYER) {
            model = new PlayerModel<>(this.ctx.bakeLayer(ModelLayers.PLAYER), true);
        }

        if (model == null) return;

        model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, 0.5F, 0.0F);
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YN.rotationDegrees(yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch - 180.0F));

        VertexConsumer consumer = pBuffer.getBuffer(RenderType.dragonExplosionAlpha(EXPLODING));
        model.renderToBuffer(pPoseStack, consumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                0.5F + (((float) pEntity.getTime() / CharredEntity.DURATION) / 2.0F));

        VertexConsumer decal = pBuffer.getBuffer(RenderType.entityDecal(CHARRED));
        model.renderToBuffer(pPoseStack, decal, pPackedLight, OverlayTexture.pack(0, false), 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CharredEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
