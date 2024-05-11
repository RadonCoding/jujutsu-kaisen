package radon.jujutsu_kaisen.client.gui.screen.radial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.tab.JJKTabType;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;

import java.util.AbstractMap;

public abstract class DisplayItem {
    protected final Minecraft minecraft;
    protected final RadialScreen screen;
    private final Runnable select;

    public DisplayItem(Minecraft minecraft, RadialScreen screen, Runnable select) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.select = select;
    }

    public abstract void drawHover(GuiGraphics graphics, int x, int y);

    public abstract void draw(GuiGraphics graphics, int x, int y);

    public abstract void mouseClicked(int button);

    public abstract boolean isActive();

    public final void select() {
        this.select.run();
    }
}
