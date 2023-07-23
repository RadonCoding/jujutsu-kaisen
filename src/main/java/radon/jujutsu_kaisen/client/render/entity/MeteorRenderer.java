package radon.jujutsu_kaisen.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.MeteorEntity;

public class MeteorRenderer extends EntityRenderer<MeteorEntity> {
    public MeteorRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull MeteorEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.level != null;

        int radius = MeteorEntity.SIZE;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockRenderDispatcher renderer = mc.getBlockRenderer();

        poseStack.pushPose();
        poseStack.translate(-0.5D, (entity.getBbHeight() / 2.0) - 0.5D, -0.5D);

        ModelBlockRenderer.enableCaching();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < radius && distance >= radius - 1) {
                        pos.set(entity.blockPosition().offset(x, y, z));

                        BlockState state = Blocks.MAGMA_BLOCK.defaultBlockState();
                        BakedModel model = renderer.getBlockModel(state);
                        RandomSource rand = RandomSource.create();
                        rand.setSeed(state.getSeed(pos));

                        poseStack.pushPose();
                        poseStack.translate(x, y, z);

                        for (RenderType type : model.getRenderTypes(state, rand, ModelData.EMPTY)) {
                            renderer.renderSingleBlock(state,
                                    poseStack,
                                    bufferSource,
                                    (mc.level.getMaxLocalRawBrightness(pos) << 20) | (state.getLightEmission(mc.level, pos) << 4),
                                    OverlayTexture.NO_OVERLAY,
                                    ModelData.EMPTY,
                                    type);
                        }
                        poseStack.popPose();
                    }
                }
            }
        }

        ModelBlockRenderer.clearCache();

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MeteorEntity pEntity) {
        return null;
    }
}
