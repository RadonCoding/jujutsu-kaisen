package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

public class DataOverlay {
    public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        // DO NOT REMOVE
        if (!mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
        ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        float scale = 0.6F;

        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, scale);

        graphics.drawString(gui.getFont(), Component.translatable(String.format("gui.%s.data_overlay.experience", JujutsuKaisen.MOD_ID), (int) cap.getExperience()),
                Math.round(20 * (1.0F / scale)), Math.round(20 * (1.0F / scale)), 16777215);

        if (cap.hasTechnique(CursedTechnique.IDLE_TRANSFIGURATION)) {
            graphics.drawString(gui.getFont(), Component.translatable(String.format("gui.%s.data_overlay.transfigured_souls", JujutsuKaisen.MOD_ID), cap.getTransfiguredSouls()),
                    Math.round(20 * (1.0F / scale)), Math.round(28 * (1.0F / scale)), 16777215);
        }
        graphics.pose().popPose();
    };
}
