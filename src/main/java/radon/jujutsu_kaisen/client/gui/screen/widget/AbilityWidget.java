package radon.jujutsu_kaisen.client.gui.screen.widget;

import com.google.common.collect.Lists;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.DisplayInfo;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.gui.screen.tab.AbilityTab;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.UnlockAbilityC2SPacket;

import javax.annotation.Nullable;
import java.util.List;

public class AbilityWidget {
    private static final ResourceLocation TITLE_BOX_SPRITE = new ResourceLocation("advancements/title_box");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};

    private final AbilityTab tab;
    private final Ability ability;
    private final DisplayInfo display;
    private final FormattedCharSequence title;
    private final int width;
    private final List<FormattedCharSequence> description;
    private final Minecraft minecraft;
    @Nullable
    private AbilityWidget parent;
    private final List<AbilityWidget> children = Lists.newArrayList();
    private final int x;
    private final int y;

    private boolean unlocked;
    private boolean unlockable;
    private boolean blocked;

    public AbilityWidget(AbilityTab tab, Minecraft minecraft, Ability ability, float x, float y) {
        this.tab = tab;
        this.ability = ability;
        this.display = new DisplayInfo(ability.getIcon(minecraft.player), x, y);
        this.minecraft = minecraft;
        this.title = Language.getInstance().getVisualOrder(minecraft.font.substrByWidth(ability.getName(), 255));
        this.x = Mth.floor(this.display.getX() * 28.0F);
        this.y = Mth.floor(this.display.getY() * 27.0F);
        int l = 29 + minecraft.font.width(this.title);

        int cost = this.minecraft.player == null ? 0 : this.ability.getRealPointsCost(this.minecraft.player);

        MutableComponent component = Component.empty();

        if (cost > 0) {
            component.append(Component.translatable(String.format("gui.%s.ability.cost", JujutsuKaisen.MOD_ID), cost));
        } else {
            component.append(Component.translatable(String.format("gui.%s.ability.locked", JujutsuKaisen.MOD_ID), cost));
        }
        this.description = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(component.copy(), Style.EMPTY), l));

        for (FormattedCharSequence sequence : this.description) {
            l = Math.max(l, minecraft.font.width(sequence));
        }
        this.width = l + 3 + 5;

        this.update();
    }

    public void update() {
        if (this.minecraft.player == null) return;

        this.unlockable = this.ability.canUnlock(this.minecraft.player);
        this.unlocked = this.ability.isUnlocked(this.minecraft.player);
        this.blocked = this.ability.isBlocked(this.minecraft.player);

        for (AbilityWidget widget : this.children) {
            widget.update();
        }
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

    @Nullable
    private AbilityWidget getFirstVisibleParent(Ability ability) {
        return this.tab.getAbility(ability.getParent(this.minecraft.player));
    }

    public void drawConnectivity(GuiGraphics pGuiGraphics, int pX, int pY, boolean pDropShadow) {
        if (this.parent != null) {
            int i = pX + this.parent.x + 13;
            int j = pX + this.parent.x + 26 + 4;
            int k = pY + this.parent.y + 13;
            int l = pX + this.x + 13;
            int i1 = pY + this.y + 13;

            int j1;

            if (pDropShadow) {
                if (this.unlocked) {
                    j1 = 0xFF005304;
                } else if (this.unlockable) {
                    j1 = 0xFF2B2B2B;
                } else if (this.blocked) {
                    j1 = 0xFF6A0000;
                } else {
                    j1 = 0xFF000000;
                }
            } else {
                if (this.unlocked) {
                    j1 = 0xFF008711;
                } else if (this.unlockable) {
                    j1 = 0xFF666666;
                } else if (this.blocked) {
                    j1 = 0xFFCF0000;
                } else {
                    j1 = 0xFFFFFFFF;
                }
            }

            if (pDropShadow) {
                pGuiGraphics.hLine(j, i, k - 1, j1);
                pGuiGraphics.hLine(j + 1, i, k, j1);
                pGuiGraphics.hLine(j, i, k + 1, j1);
                pGuiGraphics.hLine(l, j - 1, i1 - 1, j1);
                pGuiGraphics.hLine(l, j - 1, i1, j1);
                pGuiGraphics.hLine(l, j - 1, i1 + 1, j1);
                pGuiGraphics.vLine(j - 1, i1, k, j1);
                pGuiGraphics.vLine(j + 1, i1, k, j1);
            } else {
                pGuiGraphics.hLine(j, i, k, j1);
                pGuiGraphics.hLine(l, j, i1, j1);
                pGuiGraphics.vLine(j, i1, k, j1);
            }
        }

        for (AbilityWidget widget : this.children) {
            widget.drawConnectivity(pGuiGraphics, pX, pY, pDropShadow);
        }
    }

    public void draw(GuiGraphics pGuiGraphics, int pX, int pY) {
        AdvancementWidgetType type = this.unlocked ? AdvancementWidgetType.OBTAINED : AdvancementWidgetType.UNOBTAINED;
        pGuiGraphics.blitSprite(type.frameSprite(this.unlocked ? AdvancementType.CHALLENGE : AdvancementType.TASK), pX + this.x + 3, pY + this.y, 26, 26);

        if (this.ability.isCursedEnergyColor()) {
            if (this.minecraft.player != null) {
                IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                ISorcererData data = cap.getSorcererData();

                if (data != null) {
                    Vector3f color = Vec3.fromRGB24(data.getCursedEnergyColor()).toVector3f();
                    RenderSystem.setShaderColor(color.x, color.y, color.z, 1.0F);
                }
            }
        }
        pGuiGraphics.blit(this.display.getIcon(), pX + this.x + 8, pY + this.y + 5, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (AbilityWidget widget : this.children) {
            widget.draw(pGuiGraphics, pX, pY);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public void addChild(AbilityWidget widget) {
        this.children.add(widget);
    }

    public void unlock() {
        if (this.minecraft.player == null) return;

        if (!this.unlockable || this.unlocked) return;

        this.minecraft.player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);

        PacketHandler.sendToServer(new UnlockAbilityC2SPacket(JJKAbilities.getKey(this.ability)));

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (!this.minecraft.player.getAbilities().instabuild) {
            data.useAbilityPoints(this.ability.getRealPointsCost(this.minecraft.player));
        }
        data.unlock(this.ability);

        this.update();
    }

    public void drawHover(GuiGraphics pGuiGraphics, int pX, int pY, float pFade, int pWidth, int pHeight) {
        boolean xOverflow = pWidth + pX + this.x + this.width + 26 >= this.tab.getScreen().width;
        boolean yOverflow = 113 - pY - this.y - 26 <= 6 + this.description.size() * 9;
        int i = this.width / 2;
        AdvancementWidgetType type;

        if (this.unlocked) {
            type = AdvancementWidgetType.OBTAINED;
        } else {
            type = AdvancementWidgetType.UNOBTAINED;
        }

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
        pGuiGraphics.blitSprite(type.frameSprite(this.unlocked ? AdvancementType.CHALLENGE : AdvancementType.TASK), pX + this.x + 3, pY + this.y, 26, 26);

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

        if (this.ability.isCursedEnergyColor()) {
            if (this.minecraft.player != null) {
                IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                ISorcererData data = cap.getSorcererData();

                if (data != null) {
                    Vector3f color = Vec3.fromRGB24(data.getCursedEnergyColor()).toVector3f();
                    RenderSystem.setShaderColor(color.x, color.y, color.z, 1.0F);
                }
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

    public void attachToParent() {
        if (this.parent == null && this.ability.getParent(this.minecraft.player) != null) {
            this.parent = this.getFirstVisibleParent(this.ability);

            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }

    public float getY() {
        return this.display.getY();
    }

    public float getX() {
        return this.display.getX();
    }
}