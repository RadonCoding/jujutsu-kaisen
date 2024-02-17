package radon.jujutsu_kaisen.client.gui.screen;

import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.AbstractMap;

public class DisplayItem {
    public final Type type;
    public Ability ability;
    public AbstractMap.SimpleEntry<AbsorbedCurse, Integer> curse;
    public ICursedTechnique copied;
    public ICursedTechnique absorbed;
    public ItemStack item;

    public DisplayItem(Type type) {
        this.type = type;
    }

    public DisplayItem(ItemStack item) {
        this(Type.ITEM);

        this.item = item;
    }

    public DisplayItem(Ability ability) {
        this(Type.ABILITY);

        this.ability = ability;
    }

    public DisplayItem(AbsorbedCurse curse, int index) {
        this(Type.CURSE);

        this.curse = new AbstractMap.SimpleEntry<>(curse, index);
    }

    public DisplayItem(Type type, ICursedTechnique technique) {
        this(type);

        if (this.type == Type.COPIED) {
            this.copied = technique;
        } else {
            this.absorbed = technique;
        }
    }

    public enum Type {
        ABILITY,
        CURSE,
        COPIED,
        ABSORBED,
        ITEM
    }
}
