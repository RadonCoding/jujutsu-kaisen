package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.effect.WoodSegmentEntity;

public class WoodSegmentRenderer extends EntityRenderer<WoodSegmentEntity> {
    public WoodSegmentRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull WoodSegmentEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher renderer = mc.getBlockRenderer();

        BlockState state = Blocks.OAK_LOG.defaultBlockState();
        BakedModel model = renderer.getBlockModel(state);
        RandomSource rand = RandomSource.create();
        rand.setSeed(state.getSeed(pEntity.blockPosition()));

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.pushPose();
        pPoseStack.scale(0.4F, 0.8F, 0.4F);
        pPoseStack.mulPose(Axis.YN.rotationDegrees(yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch - 180.0F));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(5.0F * pEntity.getIndex()));

        assert mc.level != null;

        renderer.renderBatched(state,
                pEntity.blockPosition(),
                mc.level,
                pPoseStack,
                pBuffer.getBuffer(RenderType.solid()),
                false,
                rand,
                ModelData.EMPTY,
                RenderType.solid());

        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull WoodSegmentEntity pEntity) {
        return null;
    }
}
