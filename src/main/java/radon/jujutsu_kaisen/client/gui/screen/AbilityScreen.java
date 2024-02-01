package radon.jujutsu_kaisen.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.CurseManipulationDataHandler;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.ability.ClientAbilityHandler;
import radon.jujutsu_kaisen.client.gui.screen.base.RadialScreen;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.*;

import java.util.*;

public class AbilityScreen extends RadialScreen {
    @Override
    protected List<DisplayItem> getItems() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return List.of();

        // DO NOT REMOVE
        if (!this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return List.of();
        ISorcererData sorcererCap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        // DO NOT REMOVE
        if (!this.minecraft.player.getCapability(CurseManipulationDataHandler.INSTANCE).isPresent()) return List.of();
        ICurseManipulationData curseManipulationCap = this.minecraft.player.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();

        List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);
        abilities.removeIf(ability -> ability.getMenuType() != MenuType.RADIAL);

        List<DisplayItem> items = new ArrayList<>(abilities.stream().map(DisplayItem::new).toList());

        if (JJKAbilities.hasActiveTechnique(this.minecraft.player, JJKCursedTechniques.CURSE_MANIPULATION.get())) {
            List<AbsorbedCurse> curses = curseManipulationCap.getCurses();
            items.addAll(curses.stream().map(curse -> new DisplayItem(curse, curses.indexOf(curse))).toList());
        }

        if (sorcererCap.hasToggled(JJKAbilities.RIKA.get())) {
            Set<ICursedTechnique> copied = sorcererCap.getCopied();
            items.addAll(copied.stream().map(technique -> new DisplayItem(DisplayItem.Type.COPIED, technique)).toList());
        }

        if (JJKAbilities.hasActiveTechnique(this.minecraft.player, JJKCursedTechniques.CURSE_MANIPULATION.get())) {
            Set<ICursedTechnique> absorbed = curseManipulationCap.getAbsorbed();
            items.addAll(absorbed.stream().map(technique -> new DisplayItem(DisplayItem.Type.ABSORBED, technique)).toList());
        }
        return items;
    }

    @Override
    public void onClose() {
        super.onClose();

        if (this.hovered != -1) {
            if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return;

            ISorcererData sorcererCap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            ICurseManipulationData curseManipulationCap = this.minecraft.player.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();

            DisplayItem item = this.getCurrent().get(this.hovered);

            switch (item.type) {
                case ABILITY -> {
                    Ability ability = item.ability;

                    if (sorcererCap.hasToggled(ability) || sorcererCap.isChanneling(ability)) {
                        AbilityHandler.untrigger(this.minecraft.player, ability);
                        PacketHandler.sendToServer(new UntriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                    } else {
                        if (ClientAbilityHandler.trigger(ability) == Ability.Status.SUCCESS) {
                            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                        }
                    }
                }
                case CURSE -> PacketHandler.sendToServer(new CurseSummonC2SPacket(item.curse.getValue()));
                case COPIED -> {
                    sorcererCap.setCurrentCopied(item.copied);
                    PacketHandler.sendToServer(new SetCopiedC2SPacket(item.copied));
                }
                case ABSORBED -> {
                    curseManipulationCap.setCurrentAbsorbed(item.absorbed);
                    PacketHandler.sendToServer(new SetAbsorbedC2SPacket(item.absorbed));
                }
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTicks);

        if (this.minecraft == null || this.minecraft.player == null) return;

        if (!JJKAbilities.hasActiveTechnique(this.minecraft.player, JJKCursedTechniques.MIMICRY.get())) return;

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