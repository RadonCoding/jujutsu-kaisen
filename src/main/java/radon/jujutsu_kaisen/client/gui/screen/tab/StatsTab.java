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
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.List;
import java.util.stream.Collectors;

import static radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen.WINDOW_INSIDE_WIDTH;

public class StatsTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.stats", JujutsuKaisen.MOD_ID));

    private static final int[] TEST_SPLIT_OFFSETS = new int[] { 0, 10, -10, 25, -25 };

    public StatsTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.BOOKSHELF.getDefaultInstance(), TITLE, true);
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
            float f1 = Math.abs(getMaxWidth(stringsplitter, list1) - (float) pMaxWidth);

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
        
        if (this.minecraft.player == null) return;

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        ResourceLocation texture = this.minecraft.player.getSkin().texture();

        pGuiGraphics.blit(texture, xOffset + 12, yOffset + 6, 48, 48, 8.0F, 8.0F, 8, 8, 64, 64);

        RenderSystem.enableBlend();
        pGuiGraphics.blit(texture, xOffset + 12, yOffset + 6, 48, 48, 40.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.disableBlend();

        pGuiGraphics.blit(texture, xOffset + 12, yOffset + 54, 48, 36, 20.0F, 20.0F, 8, 6, 64, 64);
        pGuiGraphics.blit(texture, xOffset, yOffset + 54, 12, 36, 44.0F, 20.0F, 2, 6, 64, 64);
        pGuiGraphics.blit(texture, xOffset + 60, yOffset + 54, 12, 36, 36.0F, 52.0F, 2, 6, 64, 64);

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        SorcererGrade grade = SorcererUtil.getGrade(data.getExperience());
        SorcererGrade next = SorcererGrade.values()[Math.min(SorcererGrade.values().length - 1, grade.ordinal() + 1)];

        MutableComponent component = Component.empty();
        component.append(Component.translatable(String.format("gui.%s.stats.grade", JujutsuKaisen.MOD_ID), grade.getName()));
        component.append("\n");
        component.append(Component.translatable(String.format("gui.%s.stats.experience", JujutsuKaisen.MOD_ID), data.getExperience(), next.getRequiredExperience()));
        component.append("\n");

        ICursedTechnique technique = data.getTechnique();

        if (technique != null) {
            component.append(Component.translatable(String.format("gui.%s.stats.cursed_technique", JujutsuKaisen.MOD_ID), technique.getName()));
            component.append("\n");
        }
        if (!data.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            component.append(Component.translatable(String.format("gui.%s.stats.cursed_energy_nature", JujutsuKaisen.MOD_ID), data.getNature().getName()));
            component.append("\n");
        }
        component.append(Component.translatable(String.format("gui.%s.stats.traits", JujutsuKaisen.MOD_ID),
                data.getTraits().stream().map(Trait::getName).map(Component::getString).collect(Collectors.joining(", "))));
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