package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

public class ExperienceOverlay {
    public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            float scale = 0.6F;

            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, scale);

            graphics.drawString(gui.getFont(), Component.translatable(String.format("gui.%s.cursed_energy_overlay.experience", JujutsuKaisen.MOD_ID), (int) cap.getExperience()),
                    Math.round(20 * (1.0F / scale)), Math.round(20 * (1.0F / scale)), 16777215);

            graphics.pose().popPose();
        });
    };
}
