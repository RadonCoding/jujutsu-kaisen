package radon.jujutsu_kaisen.client.render.entity.effect;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.entity.effect.MeteorEntity;

public class MeteorRenderer extends EntityRenderer<MeteorEntity> {
    public MeteorRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull MeteorEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.level != null;

        int size = pEntity.getSize();
        BlockPos center = pEntity.blockPosition();

        BlockRenderDispatcher renderer = mc.getBlockRenderer();

        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2, 0.0D);

        if (pEntity.getExplosionTime() < 10) {
            float time = (pEntity.getTime() + pPartialTick) * 10.0F;
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(time * 1.5F));
            pPoseStack.mulPose(Axis.YP.rotationDegrees(time * 1.5F));
        }
        pPoseStack.translate(-0.5D, -0.5D, -0.5D);

        ModelBlockRenderer.enableCaching();

        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance < size && distance >= size - 1) {
                        BlockPos pos = center.offset(x, y, z);

                        BlockState state = JJKBlocks.METEOR.get().defaultBlockState();
                        BakedModel model = renderer.getBlockModel(state);
                        RandomSource rand = RandomSource.create();
                        rand.setSeed(state.getSeed(pos));

                        pPoseStack.pushPose();
                        pPoseStack.translate(x, y, z);

                        for (RenderType type : model.getRenderTypes(state, rand, ModelData.EMPTY)) {
                            renderer.renderSingleBlock(state,
                                    pPoseStack,
                                    bufferSource,
                                    LightTexture.FULL_BRIGHT,
                                    OverlayTexture.NO_OVERLAY,
                                    ModelData.EMPTY,
                                    type);
                        }
                        pPoseStack.popPose();
                    }
                }
            }
        }

        ModelBlockRenderer.clearCache();

        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MeteorEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
