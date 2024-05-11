package radon.jujutsu_kaisen.client.render.entity.effect;

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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.effect.WoodSegmentEntity;

import static net.neoforged.neoforge.client.model.data.ModelData.EMPTY;

public class WoodSegmentRenderer extends EntityRenderer<WoodSegmentEntity> {
    private final BlockRenderDispatcher dispatcher;

    public WoodSegmentRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.dispatcher = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(@NotNull WoodSegmentEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.scale(0.4F, 0.8F, 0.4F);

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YN.rotationDegrees(yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch - 180.0F));

        pPoseStack.mulPose(Axis.YP.rotationDegrees(5.0F * pEntity.getIndex()));

        BlockPos pos = BlockPos.containing(pEntity.getX(), pEntity.getBoundingBox().maxY, pEntity.getZ());

        BlockState state = Blocks.OAK_WOOD.defaultBlockState();
        BakedModel model = this.dispatcher.getBlockModel(state);

        for (RenderType tyoe : model.getRenderTypes(state, RandomSource.create(state.getSeed(pEntity.blockPosition())), EMPTY)) {
            this.dispatcher.getModelRenderer().tesselateBlock(pEntity.level(), model, state, pos, pPoseStack, pBuffer.getBuffer(tyoe), false,
                    RandomSource.create(), state.getSeed(pEntity.blockPosition()), OverlayTexture.NO_OVERLAY, EMPTY, tyoe);
        }
        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull WoodSegmentEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
