package radon.jujutsu_kaisen.client.gui.screen;

import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.event.ClientAbilityHandler;
import radon.jujutsu_kaisen.client.gui.screen.base.RadialScreen;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.*;

import java.util.ArrayList;
import java.util.List;

public class DomainScreen extends RadialScreen {
    @Override
    protected List<DisplayItem> getItems() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return List.of();

        List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);
        abilities.removeIf(ability -> ability.getMenuType(this.minecraft.player) != MenuType.DOMAIN);

        return new ArrayList<>(abilities.stream().map(DisplayItem::new).toList());
    }

    @Override
    public void onClose() {
        super.onClose();

        if (this.hovered == -1) return;

        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        DisplayItem item = this.getCurrent().get(this.hovered);

        Ability ability = item.ability;

        if (data.hasToggled(ability) || data.isChanneling(ability)) {
            AbilityHandler.untrigger(this.minecraft.player, ability);
            PacketHandler.sendToServer(new UntriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
        } else {
            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
        }
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == JJKKeys.SHOW_DOMAIN_MENU.getKey().getValue()) {
            this.onClose();
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
}