package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;

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

    private static void drawHead(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int pX, int pY, int pSize) {
        int i = 8;
        int j = 8;
        pGuiGraphics.blit(pAtlasLocation, pX, pY, i * pSize, j * pSize, 8.0F, 8.0F, i, j, 64, 64);
        drawHat(pGuiGraphics, pAtlasLocation, pX, pY, pSize);
    }

    private static void drawHat(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int pX, int pY, int pSize) {
        int i = 8;
        int j = 8;
        RenderSystem.enableBlend();
        pGuiGraphics.blit(pAtlasLocation, pX, pY, i * pSize, j * pSize, 40.0F, 8.0F, i, j, 64, 64);
        RenderSystem.disableBlend();
    }

    private static void drawUpperBody(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int pX, int pY, int pSize) {
        int i = 8;
        int j = 12 / 2;
        pGuiGraphics.blit(pAtlasLocation, pX, pY, i * pSize, j * pSize, 20.0F, 20.0F, i, j, 64, 64);
    }

    private static void drawRightArm(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int pX, int pY, int pSize) {
        int i = 4 / 2;
        int j = 12 / 2;
        pGuiGraphics.blit(pAtlasLocation, pX, pY, i * pSize, j * pSize, 44.0F, 20.0F, i, j, 64, 64);
    }

    private static void drawLeftArm(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int pX, int pY, int pSize) {
        int i = 4 / 2;
        int j = 12 / 2;
        pGuiGraphics.blit(pAtlasLocation, pX, pY, i * pSize, j * pSize, 36.0F, 52.0F, i, j, 64, 64);
    }

    public void renderWindow(GuiGraphics pGuiGraphics, int pOffsetX, int pOffsetY) {
        pGuiGraphics.blit(WINDOW_LOCATION, pOffsetX, pOffsetY, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_WIDTH, WINDOW_HEIGHT);

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        drawHead(pGuiGraphics, mc.player.getSkinTextureLocation(), pOffsetX + 23, pOffsetY + 26, 6);
        drawUpperBody(pGuiGraphics, mc.player.getSkinTextureLocation(), pOffsetX + 23, pOffsetY + 73, 6);
        drawRightArm(pGuiGraphics, mc.player.getSkinTextureLocation(), pOffsetX + 11, pOffsetY + 73, 6);
        drawLeftArm(pGuiGraphics, mc.player.getSkinTextureLocation(), pOffsetX + 71, pOffsetY + 73, 6);
    }
}
