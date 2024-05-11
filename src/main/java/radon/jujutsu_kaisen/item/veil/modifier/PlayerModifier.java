package radon.jujutsu_kaisen.item.veil.modifier;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class PlayerModifier extends Modifier {
    private final String name;

    public PlayerModifier(String name, Action action) {
        super(Type.PLAYER, action);

        this.name = name;
    }

    public PlayerModifier(CompoundTag nbt) {
        super(nbt);

        this.name = nbt.getString("name");
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Component getComponent() {
        return Component.translatable(String.format("item.%s.veil_rod.player.%s", JujutsuKaisen.MOD_ID, this.getAction().name().toLowerCase()), this.name)
                .withStyle(this.getAction() == Action.DENY ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putString("name", this.name);
        return nbt;
    }
}