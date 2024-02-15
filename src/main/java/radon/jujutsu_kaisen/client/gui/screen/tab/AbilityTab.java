package radon.jujutsu_kaisen.client.gui.screen.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.AbilityWidget;

import javax.annotation.Nullable;
import java.util.*;

public class AbilityTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.ability", JujutsuKaisen.MOD_ID));
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");

    private final Map<Ability, AbilityWidget> children = new HashMap<>();
    private final Map<Ability, AbilityWidget> roots = new HashMap<>();

    private final List<Ability> abilities;

    private float fade;

    private float y;

    @Nullable
    private AbstractMap.SimpleEntry<Ability, AbilityWidget> last;

    public AbilityTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.ENDER_PEARL.getDefaultInstance(), TITLE, BACKGROUND, true);

        this.abilities = new ArrayList<>(JJKAbilities.ABILITIES.getEntries().stream().map(DeferredHolder::get).toList());
        this.abilities.sort(Comparator.comparing(ability -> ability.getName().getString()));

        if (this.minecraft.player == null) return;

        for (Ability ability : this.abilities) {
            if (ability.isDisplayed(this.minecraft.player) && ability.getParent(this.minecraft.player) == null) {
                this.addAbilityAndChildren(ability);
            }
        }
    }

    private void addAbilityAndChildren(Ability parent) {
        float x = 0.0F;

        float y = 0.0F;

        Ability grandparent = parent.getParent(this.minecraft.player);

        if (grandparent != null) {
            // If the ability is the start of an new group
            if (this.roots.containsKey(grandparent)) {
                x = this.roots.get(grandparent).getX() + 2.0F;
                y = this.y + 2.0F;
            } else {
                // Otherwise we'll just put the ability to the right side of the parent
                AbilityWidget widget = this.getAbility(grandparent);

                if (widget != null) {
                    x = widget.getX() + 1.0F;
                    y = widget.getY();
                }

                if (this.last != null) {
                    // If the parents are the same we need to increase the Y
                    if (this.last.getKey().getParent(this.minecraft.player) == grandparent) {
                        y += 2.0F;
                    }
                }
            }
        }

        this.addAbility(parent, x, y);

        for (Ability ability : this.abilities) {
            if (ability.isDisplayed(this.minecraft.player) && ability.getParent(this.minecraft.player) == ability) {
                this.addAbilityAndChildren(ability);
            }
        }
        this.y = Math.max(this.y, y);
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

        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.ability.points", JujutsuKaisen.MOD_ID), data.getPoints()),
                xOffset, yOffset, 16777215, true);
    }

    @Override
    protected void drawCustom(GuiGraphics graphics, int x, int y) {
        for (AbilityWidget root : this.roots.values()) {
            root.drawConnectivity(graphics, x, y, true);
            root.drawConnectivity(graphics, x, y, false);
            root.draw(graphics, x, y);
        }
    }

    @Nullable
    public AbilityWidget getAbility(Ability ability) {
        return this.children.get(ability);
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
            for (AbilityWidget widget : this.children.values()) {
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

    public void addAbility(Ability ability, float x, float y) {
        AbilityWidget widget = new AbilityWidget(this, this.minecraft, ability, x, y);

        this.last = new AbstractMap.SimpleEntry<>(ability, widget);

        if (ability.getParent(this.minecraft.player) == null) {
            this.roots.put(ability, widget);
        }

        this.children.put(ability, widget);

        int i = Mth.floor(widget.getX() * 28.0F);
        int j = i + 28;
        int k = Mth.floor(widget.getY() * 27.0F);
        int l = k + 27;

        this.minX = Math.min(this.minX, i);
        this.maxX = Math.max(this.maxX, j);
        this.minY = Math.min(this.minY, k);
        this.maxY = Math.max(this.maxY, l);

        for (AbilityWidget child : this.children.values()) {
            child.attachToParent();
        }
    }

    @Override
    public void addWidgets() {

    }

    @Override
    public void mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);

        if (pMouseX > 0 && pMouseX < JujutsuScreen.WINDOW_INSIDE_WIDTH && pMouseY > 0 && pMouseY < JujutsuScreen.WINDOW_INSIDE_HEIGHT) {
            for (AbilityWidget widget : this.children.values()) {
                if (widget.isMouseOver(i, j, (int) pMouseX, (int) pMouseY)) {
                    widget.unlock();
                    break;
                }
            }
        }
    }
}