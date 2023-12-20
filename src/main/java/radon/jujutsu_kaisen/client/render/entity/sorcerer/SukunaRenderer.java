package radon.jujutsu_kaisen.client.render.entity.sorcerer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.layer.SukunaMarkingsLayer;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.mixin.client.IPlayerModelAccessor;

public class SukunaRenderer extends HumanoidMobRenderer<SukunaEntity, PlayerModel<SukunaEntity>> {
    private final PlayerModel<SukunaEntity> normal;
    private final PlayerModel<SukunaEntity> slim;

    @Nullable
    private ResourceLocation texture;

    public SukunaRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, null, 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel<>(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), pContext.getModelManager()));
        this.addLayer(new SukunaMarkingsLayer<>(this));
        this.addLayer(new JJKOverlayLayer<>(this));

        this.normal = new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER), false);
        this.slim = new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    @Override
    public void render(@NotNull SukunaEntity pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.level != null;

        EntityType<?> type = pEntity.getKey();

        if (type == EntityType.PLAYER) {
            GameProfile profile = pEntity.getPlayer();
            ClientPacketListener conn = Minecraft.getInstance().getConnection();
            PlayerInfo info = conn == null ? null : conn.getPlayerInfo(profile.getId());
            this.model = (info == null ? DefaultPlayerSkin.getSkinModelName(profile.getId()) : info.getModelName()).equals("default") ? this.normal : this.slim;
        } else {
            LivingEntity entity = (LivingEntity) type.create(mc.level);
            if (entity == null) return;
            LivingEntityRenderer<?, ?> renderer = (LivingEntityRenderer<?, ?>) this.entityRenderDispatcher.getRenderer(entity);
            if (!(renderer.getModel() instanceof PlayerModel<?> player)) return;
            this.model = ((IPlayerModelAccessor) player).getSlimAccessor() ? this.slim : this.normal;
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SukunaEntity pEntity) {
        EntityType<?> type = pEntity.getKey();

        if (type == EntityType.PLAYER) {
            GameProfile profile = pEntity.getPlayer();
            ClientPacketListener conn = Minecraft.getInstance().getConnection();
            PlayerInfo info = conn == null ? null : conn.getPlayerInfo(profile.getId());
            this.model = (info == null ? DefaultPlayerSkin.getSkinModelName(profile.getId()) : info.getModelName()).equals("default") ? this.normal : this.slim;
            return info == null ? DefaultPlayerSkin.getDefaultSkin(profile.getId()) : info.getSkinLocation();
        } else {
            Minecraft mc = Minecraft.getInstance();
            assert mc.level != null;
            Entity entity = type.create(mc.level);
            assert entity != null;
            System.out.println(type);
            return this.entityRenderDispatcher.getRenderer(entity).getTextureLocation(entity);
        }
    }
}
