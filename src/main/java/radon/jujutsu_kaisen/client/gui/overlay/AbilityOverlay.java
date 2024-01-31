package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.MeleeMenuType;
import radon.jujutsu_kaisen.client.gui.screen.MeleeScreen;
import radon.jujutsu_kaisen.config.ConfigHolder;

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

    private static void render(ForgeGui gui, GuiGraphics graphics, int width, int height, Ability ability) {
        Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        // DO NOT REMOVE
        if (!mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

        List<Component> lines = new ArrayList<>();

        Component nameText = Component.translatable(String.format("gui.%s.ability_overlay.name", JujutsuKaisen.MOD_ID), ability.getName());
        lines.add(nameText);

        float cost = ability.getRealCost(mc.player);

        if (cost > 0.0F) {
            lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MOD_ID), cost));
        }

        int cooldown = ability.getRealCooldown(mc.player);

        if (cooldown > 0) {
            lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cooldown", JujutsuKaisen.MOD_ID), Math.round((float) cooldown / 20)));
        }

        if (ability instanceof Ability.IDurationable durationable) {
            int duration = durationable.getRealDuration(mc.player);

            if (duration > 0) {
                Component durationText = Component.translatable(String.format("gui.%s.ability_overlay.duration", JujutsuKaisen.MOD_ID), (float) duration / 20);
                lines.add(durationText);
            }
        }

        int offset = 0;

        for (Component line : lines) {
            if (mc.font.width(line) > offset) {
                offset = mc.font.width(line);
            }
        }

        int x = width - 20 - offset;
        int y = height - 20 - (lines.size() * mc.font.lineHeight + 2);

        for (Component line : lines) {
            graphics.drawString(gui.getFont(), line, x, y, 16777215);
            y += mc.font.lineHeight;
        }
    }

    public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.SCROLL) {
            abilities = JJKAbilities.getAbilities(mc.player);
            abilities.removeIf(ability -> ability.getMenuType() != MenuType.MELEE);

            if (!abilities.isEmpty()) {
                int index = getIndex();
                Ability ability = abilities.get(index);
                render(gui, graphics, width, height, ability);
            }
        } else if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.TOGGLE) {
            Ability selected = MeleeScreen.getSelected();

            if (selected == null) return;

            if (!selected.isValid(mc.player) || !JJKAbilities.getAbilities(mc.player).contains(selected)) {
                selected = null;
            }
            render(gui, graphics, width, height, selected);
        }
    };
}
