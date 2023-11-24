package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerCardScreen extends Screen {
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/card.png");

    private static final int WINDOW_WIDTH = 256;
    private static final int WINDOW_HEIGHT = 128;
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};

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


    private static float getMaxWidth(StringSplitter pManager, List<FormattedText> pText) {
        return (float)pText.stream().mapToDouble(pManager::stringWidth).max().orElse(0.0D);
    }

    private List<FormattedText> findOptimalLines(Component pComponent, int pMaxWidth) {
        StringSplitter stringsplitter = this.minecraft.font.getSplitter();
        List<FormattedText> list = null;
        float f = Float.MAX_VALUE;

        for (int i : TEST_SPLIT_OFFSETS) {
            List<FormattedText> list1 = stringsplitter.splitLines(pComponent, pMaxWidth - i, Style.EMPTY);
            float f1 = Math.abs(getMaxWidth(stringsplitter, list1) - (float)pMaxWidth);

            if (f1 <= 10.0F) {
                return list1;
            }
            if (f1 < f) {
                f = f1;
                list = list1;
            }
        }
        return list;
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

        MutableComponent component = Component.empty();
        component.append(Component.translatable(String.format("gui.%s.player_card.grade", JujutsuKaisen.MOD_ID), grade.getName()));
        component.append("\n");
        component.append(Component.translatable(String.format("gui.%s.player_card.experience", JujutsuKaisen.MOD_ID), cap.getExperience(), next.getRequiredExperience()));
        component.append("\n");

        CursedTechnique technique = cap.getTechnique();

        if (technique != null) {
            component.append(Component.translatable(String.format("gui.%s.player_card.cursed_technique", JujutsuKaisen.MOD_ID), technique.getName()));
            component.append("\n");
        }
        if (!cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            component.append(Component.translatable(String.format("gui.%s.player_card.cursed_energy_nature", JujutsuKaisen.MOD_ID), cap.getNature().getName()));
            component.append("\n");
        }
        component.append(Component.translatable(String.format("gui.%s.player_card.traits", JujutsuKaisen.MOD_ID),
                cap.getTraits().stream().map(Trait::getName).map(Component::getString).collect(Collectors.joining(", "))));
        component.append("\n");

        List<FormattedCharSequence> lines = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(component, Style.EMPTY), WINDOW_WIDTH - 103));

        int yOffset = 0;

        for (FormattedCharSequence line : lines) {
            pGuiGraphics.drawString(this.font, line, pOffsetX + 92, pOffsetY + 38 + yOffset, 16777215);
            yOffset += this.font.lineHeight;
        }
    }
}
