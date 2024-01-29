package radon.jujutsu_kaisen.client.gui.screen.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityDisplayInfo;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.tab.AbilityTab;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.UnlockAbilityC2SPacket;

import javax.annotation.Nullable;
import java.util.List;

public class AbilityWidget {
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};

    private final AbilityTab tab;
    private final Ability ability;
    private final AbilityDisplayInfo display;
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

    public AbilityWidget(AbilityTab tab, Minecraft minecraft, Ability ability) {
        this.tab = tab;
        this.ability = ability;
        this.display = ability.getDisplay(minecraft.player);
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

            int color;

            if (pDropShadow) {
                if (this.unlocked) {
                    color = 0xFF005304;
                } else if (this.unlockable) {
                    color = 0xFF2B2B2B;
                } else if (this.blocked) {
                    color = 0xFF6A0000;
                } else {
                    color = 0xFF000000;
                }
            } else {
                if (this.unlocked) {
                    color = 0xFF008711;
                } else if (this.unlockable) {
                    color = 0xFF666666;
                } else if (this.blocked) {
                    color = 0xFFCF0000;
                } else {
                    color = 0xFFFFFFFF;
                }
            }

            if (pDropShadow) {
                pGuiGraphics.hLine(j, i, k - 1, color);
                pGuiGraphics.hLine(j + 1, i, k, color);
                pGuiGraphics.hLine(j, i, k + 1, color);
                pGuiGraphics.hLine(l, j - 1, i1 - 1, color);
                pGuiGraphics.hLine(l, j - 1, i1, color);
                pGuiGraphics.hLine(l, j - 1, i1 + 1, color);
                pGuiGraphics.vLine(j - 1, i1, k, color);
                pGuiGraphics.vLine(j + 1, i1, k, color);
            } else {
                pGuiGraphics.hLine(j, i, k, color);
                pGuiGraphics.hLine(l, j, i1, color);
                pGuiGraphics.vLine(j, i1, k, color);
            }
        }
        for (AbilityWidget widget : this.children) {
            widget.drawConnectivity(pGuiGraphics, pX, pY, pDropShadow);
        }
    }

    public void draw(GuiGraphics pGuiGraphics, int pX, int pY) {
        pGuiGraphics.blit(WIDGETS_LOCATION, pX + this.x + 3, pY + this.y, 0, this.unlocked ? 128 : 154, 26, 26);

        if (this.ability.isCursedEnergyColor()) {
            if (this.minecraft.player != null) {
                ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                Vector3f color = Vec3.fromRGB24(cap.getCursedEnergyColor()).toVector3f();
                RenderSystem.setShaderColor(color.x, color.y, color.z, 1.0F);
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

        if (this.unlockable && !this.unlocked) {
            this.minecraft.player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);

            ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            PacketHandler.sendToServer(new UnlockAbilityC2SPacket(JJKAbilities.getKey(this.ability)));

            if (!this.minecraft.player.getAbilities().instabuild) {
                cap.usePoints(this.ability.getRealPointsCost(this.minecraft.player));
            }
            cap.unlock(this.ability);

            this.update();
        }
    }

    public void drawHover(GuiGraphics pGuiGraphics, int pX, int pY, float pFade, int pWidth, int pHeight) {
        boolean flag = pWidth + pX + this.x + this.width + 26 >= this.tab.getScreen().width;
        boolean flag1 = JujutsuScreen.WINDOW_INSIDE_HEIGHT - pY - this.y - 26 <= 6 + this.description.size() * 9;
        int i = this.width / 2;
        int j = this.width - i;
        RenderSystem.enableBlend();
        int l = pY + this.y;
        int k;

        if (flag) {
            k = pX + this.x - this.width + 26 + 6;
        } else {
            k = pX + this.x;
        }

        int i1 = 32 + this.description.size() * 9;

        if (!this.description.isEmpty()) {
            if (flag1) {
                pGuiGraphics.blitNineSliced(WIDGETS_LOCATION, k, l + 26 - i1, this.width, i1, 10, 200, 26, 0, 52);
            } else {
                pGuiGraphics.blitNineSliced(WIDGETS_LOCATION, k, l, this.width, i1, 10, 200, 26, 0, 52);
            }
        }
        pGuiGraphics.blit(WIDGETS_LOCATION, k, l, 0, this.unlocked ? 0 : 26, i, 26);
        pGuiGraphics.blit(WIDGETS_LOCATION, k + i, l, 200 - j, this.unlocked ? 0 : 26, j, 26);
        pGuiGraphics.blit(WIDGETS_LOCATION, pX + this.x + 3, pY + this.y, 0, this.unlocked ? 128 : 154, 26, 26);

        if (flag) {
            pGuiGraphics.drawString(this.minecraft.font, this.title, k + 5, pY + this.y + 9, -1);
        } else {
            pGuiGraphics.drawString(this.minecraft.font, this.title, pX + this.x + 32, pY + this.y + 9, -1);
        }

        if (flag1) {
            for (int k1 = 0; k1 < this.description.size(); ++k1) {
                pGuiGraphics.drawString(this.minecraft.font, this.description.get(k1), k + 5, l + 26 - i1 + 7 + k1 * 9, -5592406, false);
            }
        } else {
            for (int l1 = 0; l1 < this.description.size(); ++l1) {
                pGuiGraphics.drawString(this.minecraft.font, this.description.get(l1), k + 5, pY + this.y + 9 + 17 + l1 * 9, -5592406, false);
            }
        }

        if (this.ability.isCursedEnergyColor()) {
            if (this.minecraft.player != null) {
                ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                Vector3f color = Vec3.fromRGB24(cap.getCursedEnergyColor()).toVector3f();
                RenderSystem.setShaderColor(color.x, color.y, color.z, 1.0F);
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

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }
}