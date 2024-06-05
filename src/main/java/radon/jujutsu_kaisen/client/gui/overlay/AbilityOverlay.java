package radon.jujutsu_kaisen.client.gui.overlay;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.client.gui.MeleeMenuType;
import radon.jujutsu_kaisen.client.gui.screen.radial.MeleeScreen;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;

import java.util.ArrayList;
import java.util.List;

public class AbilityOverlay {
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

    @Nullable
    public static Ability getSelected() {
        if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.SCROLL) {
            if (abilities.isEmpty()) return null;

            int index = getIndex();

            if (abilities.size() > index) {
                return abilities.get(index);
            }
        } else if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.TOGGLE) {
            return MeleeScreen.getSelected();
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

    private static void renderScroll(GuiGraphics graphics, int index) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        List<Component> lines = new ArrayList<>();

        Ability middle = abilities.get(index);

        int aboveStart = lines.size();

        int aboveIndex = (index == 0) ? abilities.size() - 1 : index - 1;
        Ability above = abilities.get(aboveIndex);
        lines.add(above.getName());

        int aboveEnd = lines.size();

        lines.add(Component.empty());

        renderAbilityInfo(lines, middle);

        lines.add(Component.empty());

        int belowStart = lines.size();

        if (abilities.size() > 2) {
            int belowIndex = (index == abilities.size() - 1) ? 0 : index + 1;
            Ability below = abilities.get(belowIndex);
            lines.add(below.getName());
        }

        int belowEnd = lines.size();

        int offset = 0;

        for (Component line : lines) {
            if (mc.font.width(line) > offset) {
                offset = mc.font.width(line);
            }
        }

        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        int x = width - 20 - offset;
        int y = height - 20 - (lines.size() * mc.font.lineHeight + 2);

        for (int i = 0; i < lines.size(); i++) {
            int color = 0xFFFFFF;

            if (i >= aboveStart && i <= aboveEnd || i >= belowStart && i <= belowEnd) {
                color = (0x80 << 24) | color;
            }
            graphics.drawString(mc.font, lines.get(i), x, y, color);
            y += mc.font.lineHeight;
        }
    }

    private static void renderAbilityInfo(List<Component> lines, Ability ability) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        Component nameText = Component.translatable(String.format("gui.%s.ability_overlay.name", JujutsuKaisen.MOD_ID), ability.getName());
        lines.add(nameText);

        float cost = ability.getRealCost(mc.player);

        if (cost > 0.0F) {
            lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MOD_ID), cost));
        }

        int remaining = data.getRemainingCooldown(ability);
        int cooldown = remaining > 0 ? remaining : ability.getRealCooldown(mc.player);

        if (cooldown > 0) {
            lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cooldown", JujutsuKaisen.MOD_ID), Math.round((float) cooldown / 20)));
        }

        if (ability instanceof IDurationable durationable) {
            int duration = durationable.getRealDuration(mc.player);

            if (duration > 0) {
                Component durationText = Component.translatable(String.format("gui.%s.ability_overlay.duration", JujutsuKaisen.MOD_ID), (float) duration / 20);
                lines.add(durationText);
            }
        }
    }

    private static void renderToggle(GuiGraphics graphics, Ability ability) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        List<Component> lines = new ArrayList<>();

        renderAbilityInfo(lines, ability);

        int offset = 0;

        for (Component line : lines) {
            if (mc.font.width(line) > offset) {
                offset = mc.font.width(line);
            }
        }

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        int x = width - 20 - offset;
        int y = height - 20 - (lines.size() * mc.font.lineHeight + 2);

        for (Component line : lines) {
            graphics.drawString(mc.font, line, x, y, 16777215);
            y += mc.font.lineHeight;
        }
    }

    public static LayeredDraw.Layer OVERLAY = (pGuiGraphics, pPartialTick) -> {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        if (mc.player.isSpectator()) return;

        if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.SCROLL) {
            abilities = JJKAbilities.getAbilities(mc.player);
            abilities.removeIf(ability -> ability.getMenuType(mc.player) != MenuType.MELEE);

            if (!abilities.isEmpty()) {
                renderScroll(pGuiGraphics, getIndex());
            }
        } else if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.TOGGLE) {
            Ability selected = MeleeScreen.getSelected();

            if (selected == null) return;

            renderToggle(pGuiGraphics, selected);
        }
    };
}
