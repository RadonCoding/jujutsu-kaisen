package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.RequestSorcererDataC2SPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SixEyesOverlay {
    public static final double RANGE = 100.0D;

    private static @Nullable AbstractMap.SimpleEntry<UUID, ISorcererData> current;

    public static void setCurrent(UUID identifier, @NotNull ISorcererData data) {
        current = new AbstractMap.SimpleEntry<>(identifier, data);
    }

    public static IGuiOverlay OVERLAY = (gui, poseStack, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();

        assert mc.level != null;
        assert mc.player != null;

        mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasTrait(Trait.SIX_EYES) && !mc.player.getItemBySlot(EquipmentSlot.HEAD).is(JJKItems.GOJO_BLINDFOLD.get())) {
                if (HelperMethods.getLookAtHit(mc.player, RANGE) instanceof EntityHitResult hit) {
                    Entity target = hit.getEntity();

                    if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

                    if (current == null) {
                        PacketHandler.sendToServer(new RequestSorcererDataC2SPacket(target.getUUID()));
                        return;
                    } else if (mc.level.getGameTime() % 20 == 0) {
                        PacketHandler.sendToServer(new RequestSorcererDataC2SPacket(target.getUUID()));
                    }

                    UUID identifier = current.getKey();

                    if (target.getUUID().equals(identifier)) {
                        ISorcererData data = current.getValue();

                        if (data.hasTrait(Trait.HEAVENLY_RESTRICTION)) return;

                        List<Component> lines = new ArrayList<>();

                        CursedTechnique technique = data.getTechnique();

                        if (technique != null) {
                            Component cursedTechniqueText = Component.translatable(String.format("gui.%s.six_eyes_overlay.cursed_technique", JujutsuKaisen.MOD_ID),
                                    technique.getName());
                            lines.add(cursedTechniqueText);
                        }

                        Component gradeText = Component.translatable(String.format("gui.%s.six_eyes_overlay.grade", JujutsuKaisen.MOD_ID),
                                data.getGrade().getName());
                        lines.add(gradeText);

                        Component energyText = Component.translatable(String.format("gui.%s.six_eyes_overlay.energy", JujutsuKaisen.MOD_ID),
                                data.getEnergy(), data.getMaxEnergy());
                        lines.add(energyText);

                        int offset = 0;

                        for (Component line : lines) {
                            if (mc.font.width(line) > offset) {
                                offset = mc.font.width(line);
                            }
                        }

                        int x = width - offset - 20;
                        int y = height - 20 - ((lines.size() - 1) * mc.font.lineHeight + 2);

                        for (Component line : lines) {
                            mc.font.drawShadow(poseStack, line, x, y, 53503);
                            y += mc.font.lineHeight;
                        }
                    } else {
                        PacketHandler.sendToServer(new RequestSorcererDataC2SPacket(target.getUUID()));
                    }
                }
            }
        });
    };
}
