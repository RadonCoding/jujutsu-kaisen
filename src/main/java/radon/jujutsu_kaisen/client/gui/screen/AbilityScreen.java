package radon.jujutsu_kaisen.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.event.ClientAbilityHandler;
import radon.jujutsu_kaisen.client.gui.screen.base.RadialScreen;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.*;

import java.util.*;

public class AbilityScreen extends RadialScreen {
    @Override
    protected List<DisplayItem> getItems() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return List.of();

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return List.of();

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();
        ICurseManipulationData curseManipulationData = cap.getCurseManipulationData();
        IMimicryData mimicryData = cap.getMimicryData();

        List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);
        abilities.removeIf(ability -> ability.getMenuType(this.minecraft.player) != MenuType.RADIAL);

        List<DisplayItem> items = new ArrayList<>(abilities.stream().map(DisplayItem::new).toList());

        if (!this.minecraft.player.isSpectator()) {
            if (sorcererData.hasActiveTechnique(JJKCursedTechniques.CURSE_MANIPULATION.get())) {
                List<AbsorbedCurse> curses = curseManipulationData.getCurses();
                items.addAll(curses.stream().map(curse -> new DisplayItem(curse, curses.indexOf(curse))).toList());
            }

            if (abilityData.hasToggled(JJKAbilities.RIKA.get())) {
                Set<ICursedTechnique> copied = mimicryData.getCopied();
                items.addAll(copied.stream().map(technique -> new DisplayItem(DisplayItem.Type.COPIED, technique)).toList());
            }

            if (sorcererData.hasActiveTechnique(JJKCursedTechniques.CURSE_MANIPULATION.get())) {
                Set<ICursedTechnique> absorbed = curseManipulationData.getAbsorbed();
                items.addAll(absorbed.stream().map(technique -> new DisplayItem(DisplayItem.Type.ABSORBED, technique)).toList());
            }
        }
        return items;
    }

    @Override
    public void onClose() {
        super.onClose();

        if (this.hovered == -1) return;

        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData abilityData = cap.getAbilityData();
        ICurseManipulationData curseManipulationData = cap.getCurseManipulationData();
        IMimicryData mimicryData = cap.getMimicryData();

        DisplayItem item = this.getCurrent().get(this.hovered);

        switch (item.type) {
            case ABILITY -> {
                Ability ability = item.ability;

                if (abilityData.hasToggled(ability) || abilityData.isChanneling(ability)) {
                    AbilityHandler.untrigger(this.minecraft.player, ability);
                    PacketHandler.sendToServer(new UntriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                } else {
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                }
            }
            case CURSE -> PacketHandler.sendToServer(new CurseSummonC2SPacket(item.curse.getValue()));
            case COPIED -> {
                mimicryData.setCurrentCopied(item.copied);
                PacketHandler.sendToServer(new SetCopiedC2SPacket(item.copied));
            }
            case ABSORBED -> {
                curseManipulationData.setCurrentAbsorbed(item.absorbed);
                PacketHandler.sendToServer(new SetAbsorbedC2SPacket(item.absorbed));
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTicks);

        if (this.minecraft == null || this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (!data.hasActiveTechnique(JJKCursedTechniques.MIMICRY.get())) return;

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int x = centerX;
        int y = centerY - RADIUS_OUT - this.font.lineHeight * 2;

        pGuiGraphics.drawCenteredString(this.font, Component.translatable(String.format("gui.%s.ability.right_click", JujutsuKaisen.MOD_ID)), x, y, 16777215);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == JJKKeys.SHOW_ABILITY_MENU.getKey().getValue()) {
            this.onClose();
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
}