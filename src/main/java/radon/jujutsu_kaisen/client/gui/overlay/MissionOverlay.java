package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.entity.IMissionEntityData;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.ArrayList;
import java.util.List;

public class MissionOverlay {
    public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();

        if (mc.level == null || mc.player == null) return;

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

        int x = (width - offset) / 2;
        int y = 20;

        for (Component line : lines) {
            graphics.drawString(gui.getFont(), line, x, y, 16777215);
            y += mc.font.lineHeight;
        }
    };
}
