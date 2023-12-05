package radon.jujutsu_kaisen.client.render.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.layer.SukunaMarkingsLayer;
import radon.jujutsu_kaisen.entity.CloneEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.mixin.client.IPlayerModelAccessor;
import radon.jujutsu_kaisen.mixin.client.ISkinManagerAccessor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CloneRenderer extends HumanoidMobRenderer<CloneEntity, PlayerModel<CloneEntity>> {
    private final PlayerModel<CloneEntity> normal;
    private final PlayerModel<CloneEntity> slim;

    @Nullable
    private ResourceLocation texture;

    public CloneRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, null, 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel<>(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), pContext.getModelManager()));
        this.addLayer(new JJKOverlayLayer<>(this));

        this.normal = new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER), false);
        this.slim = new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER), true);
    }

    @Override
    public void render(@NotNull CloneEntity pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        if (this.texture == null) {
            GameProfile profile = pEntity.getPlayer();

            AtomicReference<ResourceLocation> texture = new AtomicReference<>(DefaultPlayerSkin.getDefaultSkin(profile.getId()));
            AtomicReference<String> model = new AtomicReference<>(DefaultPlayerSkin.getSkinModelName(profile.getId()));

            try {
                SkullBlockEntity.updateGameprofile(profile, updated -> {
                    if (!updated.isComplete()) return;

                    Minecraft mc = Minecraft.getInstance();
                    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = mc.getSkinManager().getInsecureSkinInformation(updated);

                    if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                        ((ISkinManagerAccessor) mc.getSkinManager()).invokeRegisterTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN, (pTextureType, pLocation, pProfileTexture) -> {
                            texture.set(pLocation);

                            String metadata = pProfileTexture.getMetadata("model");

                            if (metadata == null) return;

                            model.set(metadata);
                        });
                    }
                });
            } catch (Exception ignored) {
            }

            this.texture = texture.get();
            this.model = model.get().equals("default") ? this.normal : this.slim;
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CloneEntity pEntity) {
        return this.texture;
    }
}
