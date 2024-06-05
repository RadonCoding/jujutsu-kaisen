package radon.jujutsu_kaisen.client.render.entity.projectile;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.projectile.LavaRockProjectile;

import static net.neoforged.neoforge.client.model.data.ModelData.EMPTY;

public class LavaRockRenderer extends EntityRenderer<LavaRockProjectile> {
    private final BlockRenderDispatcher dispatcher;

    public LavaRockRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.dispatcher = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(@NotNull LavaRockProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        BlockState state = Blocks.MAGMA_BLOCK.defaultBlockState();

        if (state.getRenderShape() == RenderShape.MODEL) {
            Level level = pEntity.level();

            if (state != level.getBlockState(pEntity.blockPosition()) && state.getRenderShape() != RenderShape.INVISIBLE) {
                pPoseStack.pushPose();
                pPoseStack.translate(0.0D, 0.5D, 0.0D);
                float time = (pEntity.getTime() + pPartialTick) * 30.0F;
                pPoseStack.mulPose(Axis.YP.rotationDegrees(time));
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(time));

                pPoseStack.pushPose();
                BlockPos pos = BlockPos.containing(pEntity.getX(), pEntity.getBoundingBox().maxY, pEntity.getZ());
                pPoseStack.translate(-0.5D, -0.5D, -0.5D);

                BakedModel model = this.dispatcher.getBlockModel(state);

                for (RenderType tyoe : model.getRenderTypes(state, RandomSource.create(state.getSeed(pEntity.blockPosition())), EMPTY)) {
                    this.dispatcher.getModelRenderer().tesselateBlock(level, model, state, pos, pPoseStack, pBuffer.getBuffer(tyoe), false, RandomSource.create(), state.getSeed(pEntity.blockPosition()), OverlayTexture.NO_OVERLAY, EMPTY, tyoe);
                }
                pPoseStack.popPose();

                pPoseStack.popPose();

                super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
            }
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LavaRockProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
