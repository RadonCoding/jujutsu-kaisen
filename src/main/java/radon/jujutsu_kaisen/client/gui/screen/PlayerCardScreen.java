package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.gui.screen.tab.JJKTab;

public class PlayerCardScreen extends Screen {
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/card.png");

    private static final int WINDOW_WIDTH = 256;
    private static final int WINDOW_HEIGHT = 128;

    public PlayerCardScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - WINDOW_WIDTH) / 2;
        int j = (this.height - WINDOW_HEIGHT) / 2;
        this.renderBackground(pGuiGraphics);
        this.renderWindow(pGuiGraphics, i, j);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public void renderWindow(GuiGraphics pGuiGraphics, int pOffsetX, int pOffsetY) {
        pGuiGraphics.blit(WINDOW_LOCATION, pOffsetX, pOffsetY, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    }
}
