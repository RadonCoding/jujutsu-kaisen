package radon.jujutsu_kaisen.client.render.entity.curse;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.curse.RainbowDragonEntity;
import radon.jujutsu_kaisen.entity.curse.RainbowDragonSegmentEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.HashMap;
import java.util.Map;

public class RainbowDragonSegmentRenderer extends EntityRenderer<RainbowDragonSegmentEntity> {
    private final Map<Integer, GeoEntityRenderer<RainbowDragonSegmentEntity>> renderers = new HashMap<>();
    private final RainbowDragonBodyRenderer body;

    public RainbowDragonSegmentRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.renderers.put(RainbowDragonEntity.ARMS, new RainbowDragonArmsRenderer(pContext));
        this.renderers.put(RainbowDragonEntity.LEGS, new RainbowDragonLegsRenderer(pContext));
        this.renderers.put(RainbowDragonEntity.TAIL, new RainbowDragonTailRenderer(pContext));

        this.body = new RainbowDragonBodyRenderer(pContext);
    }

    @Override
    public void render(@NotNull RainbowDragonSegmentEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        this.renderers.getOrDefault(pEntity.getIndex(), this.body).render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(RainbowDragonSegmentEntity pEntity) {
        return this.renderers.get(pEntity.getIndex()).getTextureLocation(pEntity);
    }
}
