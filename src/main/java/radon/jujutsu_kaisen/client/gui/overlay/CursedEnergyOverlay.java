package radon.jujutsu_kaisen.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.client.particle.ParticleColors;

public class CursedEnergyOverlay {
    public static ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/overlay/energy_bar.png");

    public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        LocalPlayer player = gui.getMinecraft().player;

        assert player != null;

        player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getEnergy() == 0.0F) return;

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            Vector3f color = cap.isInZone(player) ? ParticleColors.BLACK_FLASH : cap.getType() == JujutsuType.SORCERER ?
                    ParticleColors.CURSED_ENERGY_SORCERER_COLOR : ParticleColors.CURSED_ENERGY_CURSE_COLOR;
            RenderSystem.setShaderColor(color.x(), color.y(), color.z(), 1.0F);

            graphics.blit(TEXTURE, 20, 20, 0, 0, 93, 9, 93, 16);

            float maxEnergy = cap.getMaxEnergy();
            float energyWidth = (Mth.clamp(cap.getEnergy(), 0.0F, maxEnergy) / maxEnergy) * 94.0F;
            graphics.blit(TEXTURE, 20, 21, 0, 9, (int) energyWidth, 7, 93, 16);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            graphics.pose().pushPose();
            graphics.pose().scale(0.5F, 0.5F, 0.5F);
            graphics.drawString(gui.getFont(), String.format("%.1f / %.1f", cap.getEnergy(), maxEnergy),
                    (20 * 2) + 6, (20 * 2) + 6, 16777215);
            graphics.pose().popPose();

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        });
    };
}
