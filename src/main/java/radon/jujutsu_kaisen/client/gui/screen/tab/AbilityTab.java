package radon.jujutsu_kaisen.client.gui.screen.tab;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.AbilityWidget;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.ability", JujutsuKaisen.MOD_ID));
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");

    private final Map<Ability, AbilityWidget> abilities = new HashMap<>();

    private AbilityWidget root;
    private float fade;

    public AbilityTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.ENDER_PEARL.getDefaultInstance(), TITLE, BACKGROUND, true);

        if (this.minecraft.player == null) return;

        for (RegistryObject<Ability> entry : JJKAbilities.ABILITIES.getEntries()) {
            Ability ability = entry.get();

            if (ability.isDisplayed(this.minecraft.player) && ability.getParent(this.minecraft.player) == null) {
                this.addAbilityAndChildren(ability);
                break;
            }
        }
    }

    private void addAbilityAndChildren(Ability ability) {
        this.addAbility(ability);

        for (RegistryObject<Ability> entry : JJKAbilities.ABILITIES.getEntries()) {
            Ability current = entry.get();

            if (current.isDisplayed(this.minecraft.player) && current.getParent(this.minecraft.player) == ability) {
                this.addAbilityAndChildren(current);
            }
        }
    }

    @Override
    protected void drawCustom(GuiGraphics graphics, int x, int y) {
        this.root.drawConnectivity(graphics, x, y, true);
        this.root.drawConnectivity(graphics, x, y, false);
        this.root.draw(graphics, x, y);
    }

    @Nullable
    public AbilityWidget getAbility(Ability ability) {
        return this.abilities.get(ability);
    }

    @Override
    public void drawTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
        super.drawTooltips(pGuiGraphics, pMouseX, pMouseY, pOffsetX, pOffsetY);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0.0D, 0.0D, -200.0D);
        pGuiGraphics.fill(0, 0, JujutsuScreen.WINDOW_INSIDE_WIDTH, JujutsuScreen.WINDOW_INSIDE_HEIGHT, Mth.floor(this.fade * 255.0F) << 24);
        boolean hovered = false;
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);

        if (pMouseX > 0 && pMouseX < JujutsuScreen.WINDOW_INSIDE_WIDTH && pMouseY > 0 && pMouseY < JujutsuScreen.WINDOW_INSIDE_HEIGHT) {
            for (AbilityWidget widget : this.abilities.values()) {
                if (widget.isMouseOver(i, j, pMouseX, pMouseY)) {
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
    }

    public void addAbility(Ability ability) {
        AbilityWidget widget = new AbilityWidget(this, this.minecraft, ability);

        if (this.abilities.size() == 0) {
            this.root = widget;
        }

        this.abilities.put(ability, widget);

        int i = widget.getX();
        int j = i + 28;
        int k = widget.getY();
        int l = k + 27;

        this.minX = Math.min(this.minX, i);
        this.maxX = Math.max(this.maxX, j);
        this.minY = Math.min(this.minY, k);
        this.maxY = Math.max(this.maxY, l);

        for (AbilityWidget child : this.abilities.values()) {
            child.attachToParent();
        }
    }

    @Override
    public void addWidgets() {

    }
}