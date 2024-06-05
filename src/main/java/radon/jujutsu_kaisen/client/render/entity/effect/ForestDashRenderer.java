package radon.jujutsu_kaisen.client.render.entity.effect;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.effect.ForestDashEntity;
import radon.jujutsu_kaisen.entity.effect.ForestWaveEntity;

import static net.neoforged.neoforge.client.model.data.ModelData.EMPTY;

public class ForestDashRenderer extends EntityRenderer<ForestDashEntity> {
    private final BlockRenderDispatcher dispatcher;

    public ForestDashRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.dispatcher = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(@NotNull ForestDashEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        BlockState state = Blocks.OAK_WOOD.defaultBlockState();

        Level level = pEntity.level();

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);
        pPoseStack.scale(ForestDashEntity.SIZE, ForestDashEntity.SIZE, ForestDashEntity.SIZE);

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch));

        pPoseStack.pushPose();
        pPoseStack.translate(-0.5D, -0.5D, -0.5D);
        BakedModel model = this.dispatcher.getBlockModel(state);

        BlockPos pos = BlockPos.containing(pEntity.getX(), pEntity.getBoundingBox().maxY, pEntity.getZ());

        for (RenderType type : model.getRenderTypes(state, RandomSource.create(state.getSeed(pEntity.blockPosition())), ModelData.EMPTY)) {
            this.dispatcher.getModelRenderer().tesselateBlock(level, model, state, pos, pPoseStack, pBuffer.getBuffer(type), false, RandomSource.create(),
                    state.getSeed(pEntity.blockPosition()), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, type);
        }
        pPoseStack.popPose();

        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ForestDashEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
