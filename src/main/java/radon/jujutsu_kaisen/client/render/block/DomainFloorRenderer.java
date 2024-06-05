package radon.jujutsu_kaisen.client.render.block;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.domain.DomainFloorBlock;
import radon.jujutsu_kaisen.block.entity.DomainFloorBlockEntity;

public class DomainFloorRenderer implements BlockEntityRenderer<DomainFloorBlockEntity> {
    @Override
    public void render(@NotNull DomainFloorBlockEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

    }
}
