package radon.jujutsu_kaisen.client.gui.screen.tab;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.SkillWidget;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.util.SkillUtil;

import java.util.*;

public class SkillsTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.skills", JujutsuKaisen.MOD_ID));

    private final Map<Skill, SkillWidget> children = new HashMap<>();

    private float fade;

    private float y;

    @Nullable
    private SkillWidget pressed;

    private int duration;

    public SkillsTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.IRON_PICKAXE.getDefaultInstance(), TITLE, true);

        if (this.minecraft.player == null) return;

        for (Skill skill : Skill.values()) {
            if (!SkillUtil.hasSkill(this.minecraft.player, skill)) continue;

            this.addSkill(skill, 0.0F, this.y);

            this.y += 2.0F;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.pressed == null) return;

        if (++this.duration % 2 != 0) return;

        this.pressed.upgrade((int) (1 + (Math.pow(1.1D, this.duration) / 20)));
    }

    @Override
    public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.drawContents(pGuiGraphics, pX, pY);

        if (this.minecraft.player == null) return;

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.skills.points", JujutsuKaisen.MOD_ID), data.getSkillPoints()),
                xOffset, yOffset, 16777215, true);
    }

    @Override
    protected void drawCustom(GuiGraphics graphics, int x, int y) {
        for (SkillWidget skill : this.children.values()) {
            skill.draw(graphics, x, y);
        }
    }

    @Override
    public void drawTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
        super.drawTooltips(pGuiGraphics, pMouseX, pMouseY, pOffsetX, pOffsetY);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float) (pOffsetX + 9), (float) (pOffsetY + 18), 400.0F);
        RenderSystem.enableDepthTest();

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0.0D, 0.0D, -200.0D);
        pGuiGraphics.fill(0, 0, JujutsuScreen.WINDOW_INSIDE_WIDTH, JujutsuScreen.WINDOW_INSIDE_HEIGHT, Mth.floor(this.fade * 255.0F) << 24);
        boolean hovered = false;
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);

        if (pMouseX - pOffsetX - 9 > 0 && pMouseX - pOffsetX - 9 < JujutsuScreen.WINDOW_INSIDE_WIDTH && pMouseY - pOffsetY - 18 > 0 && pMouseY - pOffsetY - 18 < JujutsuScreen.WINDOW_INSIDE_HEIGHT) {
            for (SkillWidget widget : this.children.values()) {
                if (widget.isMouseOver(i, j, pMouseX - pOffsetX - 9, pMouseY - pOffsetY - 18)) {
                    hovered = true;
                    widget.drawHover(pGuiGraphics, i, j, this.fade, pOffsetX, pOffsetY);
                    break;
                }
            }
        }
        pGuiGraphics.pose().popPose();

        if (hovered) {
            this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
        RenderSystem.disableDepthTest();
        pGuiGraphics.pose().popPose();
    }

    public void addSkill(Skill skill, float x, float y) {
        SkillWidget widget = new SkillWidget(this, this.minecraft, skill, x, y);

        this.children.put(skill, widget);

        int i = Mth.floor(widget.getX() * 28.0F);
        int j = i + 28;
        int k = Mth.floor(widget.getY() * 27.0F);
        int l = k + 27;

        this.minX = Math.min(this.minX, i);
        this.maxX = Math.max(this.maxX, j);
        this.minY = Math.min(this.minY, k);
        this.maxY = Math.max(this.maxY, l);
    }

    @Override
    public void addWidgets() {

    }

    @Override
    public void mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);

        if (pMouseX > 0.0D && pMouseX < JujutsuScreen.WINDOW_INSIDE_WIDTH && pMouseY > 0.0D && pMouseY < JujutsuScreen.WINDOW_INSIDE_HEIGHT) {
            for (SkillWidget widget : this.children.values()) {
                if (widget.isMouseOver(i, j, (int) pMouseX, (int) pMouseY)) {
                    this.pressed = widget;
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.pressed = null;
        this.duration = 0;
    }
}