package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.*;

public class SixEyesOverlay {
    public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();

        assert mc.level != null && mc.player != null;

        IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (!data.hasTrait(Trait.SIX_EYES)) return;

        if (!(RotationUtil.getLookAtHit(mc.player, 64.0D) instanceof EntityHitResult hit)) return;

        if (!(hit.getEntity() instanceof LivingEntity target)) return;

        ClientVisualHandler.ClientData client = ClientVisualHandler.get(target);

        if (client == null) return;

        if (client.traits.contains(Trait.HEAVENLY_RESTRICTION_BODY)) return;

        List<Component> lines = new ArrayList<>();

        if (client.technique != null) {
            Component techniqueText = Component.translatable(String.format("gui.%s.six_eyes_overlay.cursed_techniques", JujutsuKaisen.MOD_ID),
                    String.join(", ", client.techniques.stream().map(technique -> technique.getName().getString()).toList()));
            lines.add(techniqueText);
        }

        Component gradeText = Component.translatable(String.format("gui.%s.six_eyes_overlay.grade", JujutsuKaisen.MOD_ID),
                SorcererUtil.getGrade(client.experience).getName());
        lines.add(gradeText);

        Component experienceText = Component.translatable(String.format("gui.%s.six_eyes_overlay.experience", JujutsuKaisen.MOD_ID), client.experience);
        lines.add(experienceText);

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
