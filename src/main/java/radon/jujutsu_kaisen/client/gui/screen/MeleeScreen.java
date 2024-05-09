package radon.jujutsu_kaisen.client.gui.screen;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.Ability;
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
    protected List<DisplayItem> getItems() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return List.of();

        List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);
        abilities.removeIf(ability -> ability.getMenuType(this.minecraft.player) != MenuType.MELEE);

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
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == JJKKeys.ACTIVATE_MELEE_MENU.getKey().getValue()) {
            this.onClose();
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
}