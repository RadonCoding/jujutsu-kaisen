package radon.jujutsu_kaisen.client.gui.screen.radial;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.network.packet.c2s.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AbilityScreen extends RadialScreen {
    @Override
    protected List<? extends DisplayItem> getItems() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return List.of();

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return List.of();

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();
        ICurseManipulationData curseManipulationData = cap.getCurseManipulationData();
        IMimicryData mimicryData = cap.getMimicryData();

        List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);
        abilities.removeIf(ability -> ability.getMenuType(this.minecraft.player) != MenuType.RADIAL);

        List<DisplayItem> items = new ArrayList<>();

        for (Ability ability : abilities) {
            items.add(new AbilityDisplayItem(this.minecraft, this, () -> {
                if (abilityData.hasToggled(ability) || abilityData.isChanneling(ability)) {
                    AbilityHandler.untrigger(this.minecraft.player, ability);
                    PacketDistributor.sendToServer(new UntriggerAbilityC2SPacket(ability));
                } else {
                    PacketDistributor.sendToServer(new TriggerAbilityC2SPacket(ability));
                }
            }, ability));
        }

        if (!this.minecraft.player.isSpectator()) {
            if (sorcererData.hasActiveTechnique(JJKCursedTechniques.CURSE_MANIPULATION.get())) {
                List<AbsorbedCurse> curses = curseManipulationData.getCurses();
                for (AbsorbedCurse curse : curses) {
                    items.add(new CurseDisplayItem(this.minecraft, this, () ->
                            PacketDistributor.sendToServer(new CurseSummonC2SPacket(curses.indexOf(curse))), curse));
                }
            }

            if (abilityData.hasToggled(JJKAbilities.RIKA.get())) {
                Set<CursedTechnique> copied = mimicryData.getCopied();

                for (CursedTechnique technique : copied) {
                    items.add(new CopiedDisplayItem(this.minecraft, this, () -> {
                        mimicryData.setCurrentCopied(technique);
                        PacketDistributor.sendToServer(new SetCopiedC2SPacket(technique));
                    }, technique));
                }
            }

            if (sorcererData.hasActiveTechnique(JJKCursedTechniques.CURSE_MANIPULATION.get())) {
                Set<CursedTechnique> absorbed = curseManipulationData.getAbsorbed();

                for (CursedTechnique technique : absorbed) {
                    items.add(new AbsorbedDisplayItem(this.minecraft, this, () -> {
                        curseManipulationData.setCurrentAbsorbed(technique);
                        PacketDistributor.sendToServer(new SetAbsorbedC2SPacket(technique));
                    }, technique));
                }
            }

            Set<CursedTechnique> additional = sorcererData.getAdditional();

            for (CursedTechnique technique : additional) {
                items.add(new AdditionalDisplayItem(this.minecraft, this, () -> {
                    sorcererData.setCurrentAdditional(technique);
                    PacketDistributor.sendToServer(new SetAdditionalC2SPacket(technique));
                }, technique));
            }

        }
        return items;
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

        pGuiGraphics.drawCenteredString(this.font, Component.translatable(String.format("gui.%s.ability.right_click", JujutsuKaisen.MOD_ID)), x, y, 0xFFFFFF);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == JJKKeys.SHOW_ABILITY_MENU.getKey().getValue()) {
            this.onClose();
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
}