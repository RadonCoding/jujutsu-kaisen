package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.CursedTechnique;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;

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

    private static Ability getAbility(int idx) {
        int count = abilities.size();

        while (idx < 0) {
            idx += count;
        }

        while (idx >= count) {
            idx -= count;
        }
        return abilities.get(idx);
    }

    public static IGuiOverlay ABILITY_OVERLAY = (gui, poseStack, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();
        LocalPlayer player = mc.player;

        assert player != null;

        if (abilities.isEmpty()) {
            player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                CursedTechnique technique = cap.getTechnique();
                abilities.addAll(technique.getAbilities());
            });
        }

        if (!abilities.isEmpty()) {
            int color = 16777215;
            Ability ability = getAbility(selected);

            MutableComponent nameText = Component.empty();
            nameText.append(Component.translatable(String.format("gui.%s.ability_overlay.name", JujutsuKaisen.MOD_ID)));
            nameText.append(ability.getName());

            int x = mc.getWindow().getGuiScaledWidth() - mc.font.width(nameText) - 20
                    + (mc.font.width(nameText) - mc.font.width(nameText)) / 2;
            int y = 20;
            mc.font.drawShadow(poseStack, nameText, x, y, color);

            y += mc.font.lineHeight;

            float cost = ability.getRealCost(player);

            if (cost > 0.0F) {
                MutableComponent costText = Component.empty();
                costText.append(Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MOD_ID)));
                costText.append(String.format("%.2f", cost));
                mc.font.drawShadow(poseStack, costText, x, y, color);
            }
        }
    };
}
