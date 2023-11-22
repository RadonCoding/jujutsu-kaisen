package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.stream.Collectors;

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

        pGuiGraphics.drawString(this.font, mc.player.getName(), pOffsetX + 96, pOffsetY + 16, 16777215);

        ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        SorcererGrade grade = HelperMethods.getGrade(cap.getExperience());
        SorcererGrade next = SorcererGrade.values()[Math.min(SorcererGrade.values().length - 1, grade.ordinal() + 1)];

        int yOffset = 0;
        pGuiGraphics.drawString(this.font, Component.translatable(String.format("gui.%s.player_card.grade", JujutsuKaisen.MOD_ID), grade.getName()), pOffsetX + 92, pOffsetY + 38 + yOffset, 16777215);
        yOffset += this.font.lineHeight;
        pGuiGraphics.drawString(this.font, Component.translatable(String.format("gui.%s.player_card.experience", JujutsuKaisen.MOD_ID), cap.getExperience(), next.getRequiredExperience()),
                pOffsetX + 92, pOffsetY + 38 + yOffset, 16777215);
        yOffset += this.font.lineHeight;

        CursedTechnique technique = cap.getTechnique();

        if (technique != null) {
            pGuiGraphics.drawString(this.font, Component.translatable(String.format("gui.%s.player_card.cursed_technique", JujutsuKaisen.MOD_ID), technique.getName()),
                    pOffsetX + 92, pOffsetY + 38 + yOffset, 16777215);
            yOffset += this.font.lineHeight;
        }
        pGuiGraphics.drawString(this.font, Component.translatable(String.format("gui.%s.player_card.cursed_energy_nature", JujutsuKaisen.MOD_ID), cap.getNature().getName()),
                pOffsetX + 92, pOffsetY + 38 + yOffset, 16777215);
    }
}
