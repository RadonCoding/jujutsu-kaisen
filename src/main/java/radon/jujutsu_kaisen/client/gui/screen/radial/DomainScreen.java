package radon.jujutsu_kaisen.client.gui.screen.radial;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.JJKKeys;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.c2s.*;

import java.util.ArrayList;
import java.util.List;

public class DomainScreen extends RadialScreen {
    @Override
    protected List<DisplayItem> getItems() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return List.of();

        List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);
        abilities.removeIf(ability -> ability.getMenuType(this.minecraft.player) != MenuType.DOMAIN);

        return new ArrayList<>(abilities.stream().map(ability -> new AbilityDisplayItem(this.minecraft, this, () -> {
            IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (data.hasToggled(ability) || data.isChanneling(ability)) {
                AbilityHandler.untrigger(this.minecraft.player, ability);
                PacketDistributor.sendToServer(new UntriggerAbilityC2SPacket(ability));
            } else {
                PacketDistributor.sendToServer(new TriggerAbilityC2SPacket(ability));
            }
        }, ability)).toList());
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == JJKKeys.SHOW_DOMAIN_MENU.getKey().getValue()) {
            this.onClose();
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
}