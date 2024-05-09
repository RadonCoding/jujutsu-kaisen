package radon.jujutsu_kaisen.item.veil.modifier;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class ColorModifier extends Modifier {
    private final DyeColor color;

    public ColorModifier(DyeColor color, Action action) {
        super(Type.COLOR, action);

        this.color = color;
    }

    public ColorModifier(CompoundTag nbt) {
        super(nbt);

        this.color = DyeColor.values()[nbt.getInt("color")];
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public Component getComponent() {
        return Component.translatable(String.format("item.%s.veil_rod.color", JujutsuKaisen.MOD_ID), this.color.getName());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putInt("color", this.color.ordinal());
        return nbt;
    }
}