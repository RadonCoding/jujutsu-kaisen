package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.RequestSixEyesDataC2SPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SixEyesOverlay {
    private static @Nullable AbstractMap.SimpleEntry<UUID, SixEyesData> current;

    public static void setCurrent(UUID identifier, @NotNull SixEyesData data) {
        current = new AbstractMap.SimpleEntry<>(identifier, data);
    }

    public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();

        assert mc.level != null;
        assert mc.player != null;

        mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasTrait(Trait.SIX_EYES) && !mc.player.getItemBySlot(EquipmentSlot.HEAD).is(JJKItems.SATORU_BLINDFOLD.get())) {
                if (HelperMethods.getLookAtHit(mc.player, 64.0D) instanceof EntityHitResult hit) {
                    if (hit.getEntity() instanceof LivingEntity target) {
                        if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent() || target.hasEffect(JJKEffects.UNDETECTABLE.get())) return;

                        if (current == null) {
                            PacketHandler.sendToServer(new RequestSixEyesDataC2SPacket(target.getUUID()));
                            return;
                        } else if (mc.level.getGameTime() % 20 == 0) {
                            PacketHandler.sendToServer(new RequestSixEyesDataC2SPacket(target.getUUID()));
                        }

                        UUID identifier = current.getKey();

                        if (target.getUUID().equals(identifier)) {
                            SixEyesData data = current.getValue();

                            List<Component> lines = new ArrayList<>();

                            if (data.technique != null) {
                                Component techniqueText = Component.translatable(String.format("gui.%s.six_eyes_overlay.cursed_technique", JujutsuKaisen.MOD_ID),
                                        data.technique.getName());
                                lines.add(techniqueText);
                            }

                            Component gradeText = Component.translatable(String.format("gui.%s.six_eyes_overlay.grade", JujutsuKaisen.MOD_ID),
                                    data.grade.getName());
                            lines.add(gradeText);

                            Component energyText = Component.translatable(String.format("gui.%s.six_eyes_overlay.energy", JujutsuKaisen.MOD_ID),
                                    data.energy, data.maxEnergy);
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
                                graphics.drawString(gui.getFont(), line, x, y, 53503);
                                y += mc.font.lineHeight;
                            }
                        } else {
                            PacketHandler.sendToServer(new RequestSixEyesDataC2SPacket(target.getUUID()));
                        }
                    }
                }
            }
        });
    };

    public record SixEyesData(@Nullable CursedTechnique technique, SorcererGrade grade, float energy, float maxEnergy) {
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();

            if (this.technique != null) {
                nbt.putInt("technique", this.technique.ordinal());
            }
            nbt.putInt("grade", this.grade.ordinal());
            nbt.putFloat("energy", this.energy);
            nbt.putFloat("max_energy", this.maxEnergy);
            return nbt;
        }

        public static SixEyesData deserializeNBT(CompoundTag nbt) {
            return new SixEyesData(nbt.contains("technique") ? CursedTechnique.values()[nbt.getInt("technique")] : null,
                    SorcererGrade.values()[nbt.getInt("grade")], nbt.getFloat("energy"), nbt.getFloat("max_energy"));
        }
    }
}
