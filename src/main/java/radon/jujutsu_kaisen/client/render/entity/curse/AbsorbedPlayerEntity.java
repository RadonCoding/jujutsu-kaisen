package radon.jujutsu_kaisen.client.render.entity.curse;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;

public class AbsorbedPlayerEntity extends HumanoidMobRenderer<radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity, PlayerModel<radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity>> {
    private final PlayerModel<radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity> normal;
    private final PlayerModel<radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity> slim;

    public AbsorbedPlayerEntity(EntityRendererProvider.Context pContext) {
        super(pContext, null, 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel<>(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));

        this.normal = new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER), false);
        this.slim = new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    @Override
    public void render(@NotNull radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.level != null;

        GameProfile profile = pEntity.getPlayer();
        ClientPacketListener conn = Minecraft.getInstance().getConnection();
        PlayerInfo info = conn == null ? null : conn.getPlayerInfo(profile.getId());
        this.model = (info == null ? DefaultPlayerSkin.get(profile.getId()) : info.getSkin()).model() == PlayerSkin.Model.WIDE ? this.normal : this.slim;

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity pEntity) {
        GameProfile profile = pEntity.getPlayer();
        ClientPacketListener conn = Minecraft.getInstance().getConnection();
        PlayerInfo info = conn == null ? null : conn.getPlayerInfo(profile.getId());
        this.model = (info == null ? DefaultPlayerSkin.get(profile.getId()) : info.getSkin()).model() == PlayerSkin.Model.WIDE ? this.normal : this.slim;
        return (info == null ? DefaultPlayerSkin.get(profile.getId()) : info.getSkin()).texture();
    }
}
