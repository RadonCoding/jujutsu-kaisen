package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.*;

public class SixEyesOverlay {
    public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();

        assert mc.level != null && mc.player != null;

        if (!mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

        ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!cap.hasTrait(Trait.SIX_EYES)) return;
        if (!(RotationUtil.getLookAtHit(mc.player, 64.0D) instanceof EntityHitResult hit)) return;
        if (!(hit.getEntity() instanceof LivingEntity target)) return;

        ClientVisualHandler.ClientData data = ClientVisualHandler.get(target);

        if (data == null) return;

        if (data.traits.contains(Trait.HEAVENLY_RESTRICTION)) return;

        List<Component> lines = new ArrayList<>();

        if (data.technique != null) {
            Component techniqueText = Component.translatable(String.format("gui.%s.six_eyes_overlay.cursed_technique", JujutsuKaisen.MOD_ID),
                    data.technique.getName());
            lines.add(techniqueText);
        }

        Component gradeText = Component.translatable(String.format("gui.%s.six_eyes_overlay.grade", JujutsuKaisen.MOD_ID),
                SorcererUtil.getGrade(data.experience).getName());
        lines.add(gradeText);

        int offset = 0;

        for (Component line : lines) {
            if (mc.font.width(line) > offset) {
                offset = mc.font.width(line);
            }
        }

        int x = (width - offset) / 2;
        int y = (height - ((lines.size() - 1) * mc.font.lineHeight + 8)) / 2;

        for (Component line : lines) {
            graphics.drawString(gui.getFont(), line, x, y, 53503);
            y += mc.font.lineHeight;
        }
    };
}
