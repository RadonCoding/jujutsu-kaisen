package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;

import java.util.ArrayList;
import java.util.List;

public class MeleeAbilityOverlay {
    private static int selected;
    private static List<Ability> abilities = new ArrayList<>();

    public static boolean scroll(int direction) {
        int i = -(int) Math.signum(direction);
        int count = abilities.size();

        if (count == 0) {
            return false;
        }

        selected += i;

        while (selected < 0) {
            selected += count;
        }

        while (selected >= count) {
            selected -= count;
        }
        return true;
    }

    public static Ability getSelected() {
        if (abilities.isEmpty()) return null;

        int index = getIndex();

        if (abilities.size() > index) {
            return abilities.get(index);
        }
        return null;
    }

    private static int getIndex() {
        int count = abilities.size();

        int index = selected;

        while (index < 0) {
            index += count;
        }

        while (index >= count) {
            index -= count;
        }
        return index;
    }

    public static IGuiOverlay OVERLAY = (gui, poseStack, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();
        LocalPlayer player = mc.player;

        assert player != null;

        abilities = JJKAbilities.getAbilities(player);
        abilities.removeIf(ability -> ability.getDisplayType() != DisplayType.SCROLL);

        if (!abilities.isEmpty()) {
            int index = getIndex();
            Ability ability = abilities.get(index);

            List<Component> lines = new ArrayList<>();

            Component nameText = Component.translatable(String.format("gui.%s.ability_overlay.name", JujutsuKaisen.MOD_ID), ability.getName());
            lines.add(nameText);

            float cost = ability.getRealCost(player);

            if (cost > 0.0F) {
                Component costText = Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MOD_ID), cost);
                lines.add(costText);
            }

            if (ability instanceof Ability.IDurationable durationable) {
                int duration = durationable.getRealDuration(player);

                if (duration > 0) {
                    Component durationText = Component.translatable(String.format("gui.%s.ability_overlay.duration", JujutsuKaisen.MOD_ID), duration / 20);
                    lines.add(durationText);
                }
            }

            int offset = 0;

            for (Component line : lines) {
                if (mc.font.width(line) > offset) {
                    offset = mc.font.width(line);
                }
            }

            int x = 20;
            int y = height - (20 + 22 + 24) - ((lines.size() - 1) * mc.font.lineHeight + 2);

            for (Component line : lines) {
                mc.font.drawShadow(poseStack, line, x, y, 16777215);
                y += mc.font.lineHeight;
            }
        }
    };
}
