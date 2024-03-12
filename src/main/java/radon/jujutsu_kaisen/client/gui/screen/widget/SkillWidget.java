package radon.jujutsu_kaisen.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.DisplayInfo;
import radon.jujutsu_kaisen.client.gui.screen.tab.SkillsTab;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.IncreaseSkillC2SPacket;

import java.util.List;

public class SkillWidget {
    private static final ResourceLocation TITLE_BOX_SPRITE = new ResourceLocation("advancements/title_box");
    private static final int[] TEST_SPLIT_OFFSETS = new int[] {0, 10, -10, 25, -25};

    private final SkillsTab tab;
    private final Skill skill;
    private final DisplayInfo display;
    private final FormattedCharSequence title;
    private int width;
    private List<FormattedCharSequence> description;
    private final Minecraft minecraft;
    private final int x;
    private final int y;

    public SkillWidget(SkillsTab tab, Minecraft minecraft, Skill skill, float x, float y) {
        this.tab = tab;
        this.skill = skill;
        this.display = new DisplayInfo(skill.getIcon(), x, y);
        this.minecraft = minecraft;
        this.title = Language.getInstance().getVisualOrder(minecraft.font.substrByWidth(skill.getName(), 255));
        this.x = Mth.floor(this.display.getX() * 28.0F);
        this.y = Mth.floor(this.display.getY() * 27.0F);

        this.update();
    }

    public void update() {
        if (this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISkillData data = cap.getSkillData();
        int level = data.getSkill(this.skill);

        int l = 29 + minecraft.font.width(this.title);

        MutableComponent component = Component.empty();
        component.append(Component.translatable(String.format("gui.%s.skills.level", JujutsuKaisen.MOD_ID), level, ConfigHolder.SERVER.maximumSkillLevel.get()));
        component.append(Component.literal("\n\n"));
        component.append(this.skill.getDescription());
        this.description = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(component.copy(), Style.EMPTY), l));

        for (FormattedCharSequence sequence : this.description) {
            l = Math.max(l, minecraft.font.width(sequence));
        }
        this.width = l + 3 + 5;
    }

    public Skill getSkill() {
        return this.skill;
    }

    private static float getMaxWidth(StringSplitter pManager, List<FormattedText> pText) {
        return (float) pText.stream().mapToDouble(pManager::stringWidth).max().orElse(0.0D);
    }

    private List<FormattedText> findOptimalLines(Component pComponent, int pMaxWidth) {
        StringSplitter splitter = this.minecraft.font.getSplitter();
        List<FormattedText> result = null;
        float f = Float.MAX_VALUE;

        for (int i : TEST_SPLIT_OFFSETS) {
            List<FormattedText> lines = splitter.splitLines(pComponent, pMaxWidth - i, Style.EMPTY);
            float f1 = Math.abs(getMaxWidth(splitter, lines) - (float) pMaxWidth);

            if (f1 <= 10.0F) {
                return lines;
            }
            if (f1 < f) {
                f = f1;
                result = lines;
            }
        }
        return result;
    }

    public void draw(GuiGraphics pGuiGraphics, int pX, int pY) {
        AdvancementWidgetType type = AdvancementWidgetType.UNOBTAINED;
        pGuiGraphics.blitSprite(type.frameSprite(AdvancementType.CHALLENGE), pX + this.x + 3, pY + this.y, 26, 26);

        pGuiGraphics.blit(this.display.getIcon(), pX + this.x + 8, pY + this.y + 5, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public int getWidth() {
        return this.width;
    }

    public void upgrade(int amount) {
        if (this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();

        ISkillData skillData = cap.getSkillData();

        int current = skillData.getSkill(this.skill);

        if (current >= ConfigHolder.SERVER.maximumSkillLevel.get()) return;

        int real = Math.min(ConfigHolder.SERVER.maximumSkillLevel.get(), current + amount) - current;

        if (!this.minecraft.player.getAbilities().instabuild && sorcererData.getSkillPoints() < real) return;

        this.minecraft.player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);

        PacketHandler.sendToServer(new IncreaseSkillC2SPacket(this.skill, real));

        if (!this.minecraft.player.getAbilities().instabuild) {
            sorcererData.useSkillPoints(real);
        }
        skillData.increaseSkill(this.skill, real);

        this.update();
    }

    public void drawHover(GuiGraphics pGuiGraphics, int pX, int pY, float pFade, int pWidth, int pHeight) {
        boolean xOverflow = pWidth + pX + this.x + this.width + 26 >= this.tab.getScreen().width;
        boolean yOverflow = 113 - pY - this.y - 26 <= 6 + this.description.size() * 9;
        int i = this.width / 2;
        AdvancementWidgetType type = AdvancementWidgetType.UNOBTAINED;

        int j = this.width - i;
        RenderSystem.enableBlend();
        int k = pY + this.y;
        int l;

        if (xOverflow) {
            l = pX + this.x - this.width + 26 + 6;
        } else {
            l = pX + this.x;
        }

        int i1 = 32 + this.description.size() * 9;

        if (!this.description.isEmpty()) {
            if (yOverflow) {
                pGuiGraphics.blitSprite(TITLE_BOX_SPRITE, l, k + 26 - i1, this.width, i1);
            } else {
                pGuiGraphics.blitSprite(TITLE_BOX_SPRITE, l, k, this.width, i1);
            }
        }

        pGuiGraphics.blitSprite(type.boxSprite(), 200, 26, 0, 0, l, k, i, 26);
        pGuiGraphics.blitSprite(type.boxSprite(), 200, 26, 200 - j, 0, l + i, k, j, 26);
        pGuiGraphics.blitSprite(type.frameSprite(AdvancementType.CHALLENGE), pX + this.x + 3, pY + this.y, 26, 26);

        if (xOverflow) {
            pGuiGraphics.drawString(this.minecraft.font, this.title, l + 5, pY + this.y + 9, -1);
        } else {
            pGuiGraphics.drawString(this.minecraft.font, this.title, pX + this.x + 32, pY + this.y + 9, -1);
        }

        if (yOverflow) {
            for (int j1 = 0; j1 < this.description.size(); ++j1) {
                pGuiGraphics.drawString(this.minecraft.font, this.description.get(j1), l + 5, k + 26 - i1 + 7 + j1 * 9, -5592406, false);
            }
        } else {
            for (int k1 = 0; k1 < this.description.size(); ++k1) {
                pGuiGraphics.drawString(this.minecraft.font, this.description.get(k1), l + 5, pY + this.y + 9 + 17 + k1 * 9, -5592406, false);
            }
        }

        pGuiGraphics.blit(this.display.getIcon(), pX + this.x + 8, pY + this.y + 5, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isMouseOver(int pX, int pY, int pMouseX, int pMouseY) {
        int i = pX + this.x;
        int j = i + 26;
        int k = pY + this.y;
        int l = k + 26;
        return pMouseX >= i && pMouseX <= j && pMouseY >= k && pMouseY <= l;
    }

    public float getY() {
        return this.display.getY();
    }

    public float getX() {
        return this.display.getX();
    }
}