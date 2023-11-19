package radon.jujutsu_kaisen.client.render.entity.sorcerer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.layer.SukunaMarkingsLayer;
import radon.jujutsu_kaisen.entity.CloneEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;

public class SukunaRenderer extends HumanoidMobRenderer<SukunaEntity, PlayerModel<SukunaEntity>> {
    public SukunaRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, null, 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel<>(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), pContext.getModelManager()));
        this.addLayer(new SukunaMarkingsLayer<>(this));
    }

    @Override
    public void render(SukunaEntity pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        LivingEntity owner = pEntity.getOwner();

        if (owner == null) {
            Minecraft mc = Minecraft.getInstance();
            assert mc.level != null;
            EntityType<?> type = pEntity.getKey();
            LivingEntity entity = (LivingEntity) type.create(mc.level);

            if (entity == null) return;

            var renderer = (LivingEntityRenderer<?, ?>) this.entityRenderDispatcher.getRenderer(entity);
            this.model = (PlayerModel<SukunaEntity>) renderer.getModel();
        } else {
            var renderer = (LivingEntityRenderer<?, ?>) this.entityRenderDispatcher.getRenderer(owner);
            this.model = (PlayerModel<SukunaEntity>) renderer.getModel();
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SukunaEntity pEntity) {
        LivingEntity owner = pEntity.getOwner();

        if (owner == null) {
            Minecraft mc = Minecraft.getInstance();
            assert mc.level != null;
            EntityType<?> type = pEntity.getKey();
            Entity entity = type.create(mc.level);
            assert entity != null;
            return this.entityRenderDispatcher.getRenderer(entity).getTextureLocation(entity);
        } else {
            return this.entityRenderDispatcher.getRenderer(owner).getTextureLocation(owner);
        }
    }
}
