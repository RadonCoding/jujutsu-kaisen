package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbilityOverlay {
    private static int selected;
    private static final List<Ability> abilities = new ArrayList<>();

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
        if (abilities.size() > selected) {
            return abilities.get(selected);
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

    public static IGuiOverlay ABILITY_OVERLAY = (gui, poseStack, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();
        LocalPlayer player = mc.player;

        assert player != null;

        player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            CursedTechnique technique = cap.getTechnique();
            abilities.clear();
            abilities.addAll(Arrays.asList(technique.getAbilities(player)));
        });

        if (!abilities.isEmpty()) {
            int index = getIndex();
            Ability ability = abilities.get(index);
            float cost = ability.getRealCost(player);

            List<Component> lines = new ArrayList<>();

            MutableComponent nameText = Component.empty();
            nameText.append(Component.translatable(String.format("gui.%s.ability_overlay.name", JujutsuKaisen.MOD_ID)));
            nameText.append(ability.getName());
            lines.add(nameText);

            if (cost > 0.0F) {
                MutableComponent costText = Component.empty();
                costText.append(Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MOD_ID)));
                costText.append(String.format("%.2f", cost));
                lines.add(costText);
            }

            int offset = 0;

            for (Component line : lines) {
                if (mc.font.width(line) > offset) {
                    offset = mc.font.width(line);
                }
            }

            int x = width - offset - 20;
            int y = 20;

            for (Component line : lines) {
                mc.font.drawShadow(poseStack, line, x, y, 16777215);
                y += mc.font.lineHeight;
            }
        }
    };
}
