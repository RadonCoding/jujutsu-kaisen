package radon.jujutsu_kaisen.client.gui.screen.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Items;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.List;
import java.util.stream.Collectors;

import static radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen.WINDOW_INSIDE_WIDTH;

public class StatsTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.stats", JujutsuKaisen.MOD_ID));
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");

    private static final int[] TEST_SPLIT_OFFSETS = new int[] { 0, 10, -10, 25, -25 };

    public StatsTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.BOOKSHELF.getDefaultInstance(), TITLE, BACKGROUND, true);
    }

    private static void drawHead(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int xOffset, int yOffset, int pSize) {
        int i = 8;
        int j = 8;
        pGuiGraphics.blit(pAtlasLocation, xOffset, yOffset, i * pSize, j * pSize, 8.0F, 8.0F, i, j, 64, 64);
        drawHat(pGuiGraphics, pAtlasLocation, xOffset, yOffset, pSize);
    }

    private static void drawHat(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int xOffset, int yOffset, int pSize) {
        int i = 8;
        int j = 8;
        RenderSystem.enableBlend();
        pGuiGraphics.blit(pAtlasLocation, xOffset, yOffset, i * pSize, j * pSize, 40.0F, 8.0F, i, j, 64, 64);
        RenderSystem.disableBlend();
    }

    private static void drawUpperBody(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int xOffset, int yOffset, int pSize) {
        int i = 8;
        int j = 12 / 2;
        pGuiGraphics.blit(pAtlasLocation, xOffset, yOffset, i * pSize, j * pSize, 20.0F, 20.0F, i, j, 64, 64);
    }

    private static void drawRightArm(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int xOffset, int yOffset, int pSize) {
        int i = 4 / 2;
        int j = 12 / 2;
        pGuiGraphics.blit(pAtlasLocation, xOffset, yOffset, i * pSize, j * pSize, 44.0F, 20.0F, i, j, 64, 64);
    }

    private static void drawLeftArm(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int xOffset, int yOffset, int pSize) {
        int i = 4 / 2;
        int j = 12 / 2;
        pGuiGraphics.blit(pAtlasLocation, xOffset, yOffset, i * pSize, j * pSize, 36.0F, 52.0F, i, j, 64, 64);
    }

    private static float getMaxWidth(StringSplitter pManager, List<FormattedText> pText) {
        return (float) pText.stream().mapToDouble(pManager::stringWidth).max().orElse(0.0D);
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

    @Override
    public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.drawContents(pGuiGraphics, pX, pY);

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        drawHead(pGuiGraphics, this.minecraft.player.getSkinTextureLocation(), xOffset + 12, yOffset, 6);
        drawUpperBody(pGuiGraphics, this.minecraft.player.getSkinTextureLocation(), xOffset + 12, yOffset + 47, 6);
        drawRightArm(pGuiGraphics, this.minecraft.player.getSkinTextureLocation(), xOffset, yOffset + 47, 6);
        drawLeftArm(pGuiGraphics, this.minecraft.player.getSkinTextureLocation(), xOffset + 60, yOffset + 47, 6);
        
        ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        SorcererGrade grade = SorcererUtil.getGrade(cap.getExperience());
        SorcererGrade next = SorcererGrade.values()[Math.min(SorcererGrade.values().length - 1, grade.ordinal() + 1)];

        MutableComponent component = Component.empty();
        component.append(Component.translatable(String.format("gui.%s.stats.grade", JujutsuKaisen.MOD_ID), grade.getName()));
        component.append("\n");
        component.append(Component.translatable(String.format("gui.%s.stats.experience", JujutsuKaisen.MOD_ID), cap.getExperience(), next.getRequiredExperience()));
        component.append("\n");

        CursedTechnique technique = cap.getTechnique();

        if (technique != null) {
            component.append(Component.translatable(String.format("gui.%s.stats.cursed_technique", JujutsuKaisen.MOD_ID), technique.getName()));
            component.append("\n");
        }
        if (!cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            component.append(Component.translatable(String.format("gui.%s.stats.cursed_energy_nature", JujutsuKaisen.MOD_ID), cap.getNature().getName()));
            component.append("\n");
        }
        component.append(Component.translatable(String.format("gui.%s.stats.traits", JujutsuKaisen.MOD_ID),
                cap.getTraits().stream().map(Trait::getName).map(Component::getString).collect(Collectors.joining(", "))));
        component.append("\n");

        List<FormattedCharSequence> lines = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(component, Style.EMPTY), WINDOW_INSIDE_WIDTH - 104));

        int offset = 0;

        for (FormattedCharSequence line : lines) {
            pGuiGraphics.drawString(this.minecraft.font, line, xOffset + 80, yOffset + offset, 16777215);
            offset += this.minecraft.font.lineHeight;
        }
    }

    @Override
    public void addWidgets() {

    }
}