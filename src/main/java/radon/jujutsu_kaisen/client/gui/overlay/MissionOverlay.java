package radon.jujutsu_kaisen.client.gui.overlay;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.entity.IMissionEntityData;

import java.util.ArrayList;
import java.util.List;

public class MissionOverlay {
    public static LayeredDraw.Layer OVERLAY = (pGuiGraphics, pPartialTick) -> {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IMissionEntityData data = cap.getMissionData();

        Mission mission = data.getMission();

        if (mission == null) return;

        List<Component> lines = new ArrayList<>();

        Component experienceText = Component.translatable(String.format("gui.%s.mission_overlay.remaining", JujutsuKaisen.MOD_ID),
                mission.getCurses().size(), mission.getTotal());
        lines.add(experienceText);

        int offset = 0;

        for (Component line : lines) {
            if (mc.font.width(line) > offset) {
                offset = mc.font.width(line);
            }
        }

        int width = mc.getWindow().getGuiScaledWidth();

        int x = (width - offset) / 2;
        int y = 20;

        for (Component line : lines) {
            pGuiGraphics.drawString(mc.font, line, x, y, 16777215);
            y += mc.font.lineHeight;
        }
    };
}
