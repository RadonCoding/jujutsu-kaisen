package radon.jujutsu_kaisen.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
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
        Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getEnergy() == 0.0F) return;

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            Vector3f color = cap.isInZone() ? ParticleColors.BLACK_FLASH : cap.getType() == JujutsuType.SORCERER ?
                    ParticleColors.CURSED_ENERGY_SORCERER_COLOR : ParticleColors.CURSED_ENERGY_CURSE_COLOR;
            RenderSystem.setShaderColor(color.x(), color.y(), color.z(), 1.0F);

            graphics.blit(TEXTURE, 20, 32, 0, 0, 93, 9, 93, 16);

            float maxEnergy = cap.getMaxEnergy();
            float energyWidth = (Mth.clamp(cap.getEnergy(), 0.0F, maxEnergy) / maxEnergy) * 94.0F;
            graphics.blit(TEXTURE, 20, 33, 0, 9, (int) energyWidth, 7, 93, 16);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            float scale = 0.6F;

            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, scale);

            graphics.drawString(gui.getFont(), Component.translatable(String.format("gui.%s.cursed_energy_overlay.output", JujutsuKaisen.MOD_ID), Math.round(cap.getOutput() * 100)),
                    Math.round(20 * (1.0F / scale)), Math.round(26 * (1.0F / scale)), 16777215);

            graphics.drawString(gui.getFont(), String.format("%.1f / %.1f", cap.getEnergy(), maxEnergy),
                    Math.round(23 * (1.0F / scale)), Math.round(34 * (1.0F / scale)), 16777215);
            graphics.pose().popPose();

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        });
    };
}
