package radon.jujutsu_kaisen.client.gui.scren;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.client.gui.scren.base.RadialScreen;

import java.util.*;

public class AbilityScreen extends RadialScreen {
    @Override
    protected List<DisplayItem> getItems() {
        assert this.minecraft != null && this.minecraft.level != null && this.minecraft.player != null;

        if (!this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return List.of();
        ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        List<Ability> abilities =  JJKAbilities.getAbilities(this.minecraft.player);
        abilities.removeIf(ability -> ability.getDisplayType() != DisplayType.RADIAL);

        List<DisplayItem> items = new ArrayList<>(abilities.stream().map(DisplayItem::new).toList());

        Map<EntityType<?>, Integer> curses = cap.getCurses(this.minecraft.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE));
        items.addAll(curses.entrySet().stream().map(entry -> new DisplayItem(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()))).toList());

        Set<CursedTechnique> copied = cap.getCopied();
        items.addAll(copied.stream().map(technique -> new DisplayItem(DisplayItem.Type.COPIED, technique)).toList());

        Set<CursedTechnique> absorbed = cap.getAbsorbed();
        items.addAll(absorbed.stream().map(technique -> new DisplayItem(DisplayItem.Type.ABSORBED, technique)).toList());

        return items;
    }
}