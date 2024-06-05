package radon.jujutsu_kaisen.item.veil.modifier;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class GriefingModifier extends Modifier {
    public GriefingModifier(Action action) {
        super(Type.GRIEFING, action);
    }

    public GriefingModifier(CompoundTag nbt) {
        super(nbt);
    }

    @Override
    public Component getComponent() {
        return Component.translatable(String.format("item.%s.veil_rod.griefing.%s", JujutsuKaisen.MOD_ID, this.getAction().name().toLowerCase()))
                .withStyle(this.getAction() == Action.DENY ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN);
    }
}
