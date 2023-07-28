package radon.jujutsu_kaisen.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

public class CursedEnergyOverlay {
    public static ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/overlay/energy_bar.png");

    public static IGuiOverlay OVERLAY = (gui, poseStack, partialTicks, width, height) -> {
        LocalPlayer player = gui.getMinecraft().player;

        assert player != null;

        player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getEnergy() == 0.0F) return;

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, TEXTURE);

            GuiComponent.blit(poseStack, 20, 20, 0, 0, 93, 9, 93, 16);

            float maxEnergy = cap.getMaxEnergy();
            float energyWidth = (Mth.clamp(cap.getEnergy(), 0.0F, maxEnergy) / maxEnergy) * 94.0F;
            GuiComponent.blit(poseStack, 20, 21, 0, 9, (int) energyWidth, 7, 93, 16);
            poseStack.pushPose();
            poseStack.scale(0.5F, 0.5F, 0.5F);
            gui.getFont().drawShadow(poseStack, String.format("%.1f / %.1f", cap.getEnergy(), maxEnergy),
                    (20.0F * 2.0F) + 5.0F, (20.0F * 2.0F) + 5.5F, 16777215);
            poseStack.popPose();

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        });
    };
}
