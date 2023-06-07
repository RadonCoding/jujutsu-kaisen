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
                abilities.addAll(Arrays.asList(technique.getAbilities()));
            });
        }

        if (!abilities.isEmpty()) {
            int color = 16777215;
            Ability ability = getAbility(selected);

            MutableComponent name = Component.empty();
            name.append(Component.translatable(String.format("gui.%s.ability_overlay.name", JujutsuKaisen.MODID)));
            name.append(ability.getName());

            int x = mc.getWindow().getGuiScaledWidth() - mc.font.width(name) - 20
                    + (mc.font.width(name) - mc.font.width(name)) / 2;
            int y = 20;
            mc.font.drawShadow(poseStack, name, x, y, color);

            y += mc.font.lineHeight;

            MutableComponent cost = Component.empty();
            cost.append(Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MODID)));
            cost.append(String.format("%.2f", ability.getCost()));
            mc.font.drawShadow(poseStack, cost, x, y, color);
        }
    };
}
