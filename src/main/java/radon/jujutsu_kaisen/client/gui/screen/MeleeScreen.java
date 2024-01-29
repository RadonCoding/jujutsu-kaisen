package radon.jujutsu_kaisen.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.screen.base.RadialScreen;

import java.util.*;

public class MeleeScreen extends RadialScreen {
    @Nullable
    private static Ability selected;

    public static @Nullable Ability getSelected() {
        return selected;
    }

    @Override
    protected boolean isActive(DisplayItem item) {
        return MeleeScreen.getSelected() == item.ability || super.isActive(item);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.minecraft == null || this.minecraft.player == null) return;

        if (selected != null && !selected.isValid(this.minecraft.player) || !JJKAbilities.getAbilities(this.minecraft.player).contains(selected)) {
            selected = null;
        }
    }

    @Override
    protected List<DisplayItem> getItems() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return List.of();

        List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);
        abilities.removeIf(ability -> ability.getMenuType() != MenuType.MELEE);

        return new ArrayList<>(abilities.stream().map(DisplayItem::new).toList());
    }

    @Override
    public void onClose() {
        super.onClose();

        if (this.hovered != -1) {
            if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return;

            DisplayItem item = this.getCurrent().get(this.hovered);

            selected = item.ability;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTicks);

        if (this.minecraft == null || this.minecraft.player == null) return;

        // DO NOT REMOVE
        if (!this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
        ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!cap.hasTechnique(CursedTechnique.MIMICRY)) return;

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int x = centerX;
        int y = centerY - RADIUS_OUT - this.font.lineHeight * 2;

        pGuiGraphics.drawCenteredString(this.font, Component.translatable(String.format("gui.%s.ability.right_click", JujutsuKaisen.MOD_ID)), x, y, 16777215);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == JJKKeys.ACTIVATE_MELEE_MENU.getKey().getValue()) {
            this.onClose();
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
}