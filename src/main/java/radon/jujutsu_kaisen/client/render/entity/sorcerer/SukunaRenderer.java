package radon.jujutsu_kaisen.client.render.entity.sorcerer;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.MixinData;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.layer.SukunaMarkingsLayer;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.mixin.client.IPlayerModelAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SukunaRenderer extends LivingEntityRenderer<SukunaEntity, EntityModel<SukunaEntity>> {
    private final EntityRendererProvider.Context ctx;

    private final PlayerModel<SukunaEntity> normal;
    private final PlayerModel<SukunaEntity> slim;

    public SukunaRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, null, 0.5F);

        this.ctx = pContext;

        this.normal = new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER), false);
        this.slim = new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void render(@NotNull SukunaEntity pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.level != null;

        Optional<GameProfile> profile = pEntity.getPlayer();

        if (profile.isPresent()) {
            ClientPacketListener conn = Minecraft.getInstance().getConnection();
            PlayerInfo info = conn == null ? null : conn.getPlayerInfo(profile.get().getId());
            this.model = (info == null ? DefaultPlayerSkin.get(profile.get().getId()) : info.getSkin()).model() == PlayerSkin.Model.WIDE ? this.normal : this.slim;

            if (this.layers.isEmpty()) {
                this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel<>(this.ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                        new HumanoidArmorModel<>(this.ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), this.ctx.getModelManager()));
                this.addLayer(new SukunaMarkingsLayer<>(this));
                this.addLayer(new JJKOverlayLayer(this));
            }
            super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        } else {
            EntityType<?> type = pEntity.getEntity();

            LivingEntity entity = (LivingEntity) type.create(mc.level);
            
            if (entity == null) return;

            MixinData.isCustomWalkAnimation = true;
            MixinData.walkAnimationPosition = pEntity.walkAnimation.position(pPartialTicks);
            MixinData.walkAnimationSpeed = pEntity.walkAnimation.speed(pPartialTicks);

            entity.swinging = pEntity.swinging;
            entity.swingTime = pEntity.swingTime;

            entity.setYRot(pEntity.getYRot());
            entity.yRotO = pEntity.yRotO;

            entity.yHeadRot = pEntity.getYHeadRot();
            entity.yHeadRotO = pEntity.yHeadRotO;

            entity.yBodyRot = pEntity.yBodyRot;
            entity.yBodyRotO = pEntity.yBodyRotO;

            EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<? super Entity> renderer = manager.getRenderer(entity);

            if (renderer instanceof LivingEntityRenderer living && living.getModel() instanceof HeadedModel) {
                living.addLayer(new SukunaMarkingsLayer<>(living));
                living.addLayer(new JJKOverlayLayer<>(living));
            }
            renderer.render(entity, 0.0F, pPartialTicks, pPoseStack, pBuffer, pPackedLight);

            MixinData.isCustomWalkAnimation = false;
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SukunaEntity pEntity) {
        Optional<GameProfile> profile = pEntity.getPlayer();

        if (profile.isPresent()) {
            ClientPacketListener conn = Minecraft.getInstance().getConnection();
            PlayerInfo info = conn == null ? null : conn.getPlayerInfo(profile.get().getId());
            return (info == null ? DefaultPlayerSkin.get(profile.get().getId()) : info.getSkin()).texture();
        } else {
            EntityType<?> type = pEntity.getEntity();

            Minecraft mc = Minecraft.getInstance();
            assert mc.level != null;
            Entity entity = type.create(mc.level);
            assert entity != null;
            return this.entityRenderDispatcher.getRenderer(entity).getTextureLocation(entity);
        }
    }
}
