package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.effect.ForestWaveEntity;

import static net.minecraftforge.client.model.data.ModelData.EMPTY;

public class ForestWaveRenderer extends EntityRenderer<ForestWaveEntity> {
    private final BlockRenderDispatcher dispatcher;

    public ForestWaveRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.dispatcher = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(ForestWaveEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        BlockState state = pEntity.getState();

        if (state.getRenderShape() == RenderShape.MODEL) {
            Level level = pEntity.getLevel();

            if (state != level.getBlockState(pEntity.blockPosition()) && state.getRenderShape() != RenderShape.INVISIBLE) {
                pPoseStack.pushPose();
                BlockPos pos = BlockPos.containing(pEntity.getX(), pEntity.getBoundingBox().maxY, pEntity.getZ());
                pPoseStack.translate(-0.5D, 0.0D, -0.5D);

                float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
                float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

                pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw));
                pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch));

                BakedModel model = this.dispatcher.getBlockModel(state);

                for (RenderType tyoe : model.getRenderTypes(state, RandomSource.create(state.getSeed(pEntity.blockPosition())), EMPTY)) {
                    this.dispatcher.getModelRenderer().tesselateBlock(level, model, state, pos, pPoseStack, pBuffer.getBuffer(tyoe), false, RandomSource.create(), state.getSeed(pEntity.blockPosition()), OverlayTexture.NO_OVERLAY, EMPTY, tyoe);
                }
                pPoseStack.popPose();

                super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
            }
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ForestWaveEntity pEntity) {
        return null;
    }
}
