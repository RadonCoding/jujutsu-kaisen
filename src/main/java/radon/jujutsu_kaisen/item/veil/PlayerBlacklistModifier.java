package radon.jujutsu_kaisen.item.veil;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class PlayerBlacklistModifier extends Modifier {
    private final String name;

    public PlayerBlacklistModifier(String name) {
        super(Type.PLAYER_BLACKLIST);

        this.name = name;
    }

    public PlayerBlacklistModifier(CompoundTag nbt) {
        super(nbt);

        this.name = nbt.getString("name");
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Component getComponent() {
        return Component.translatable(String.format("item.%s.veil_rod.blacklist", JujutsuKaisen.MOD_ID), this.name).withStyle(ChatFormatting.DARK_RED);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("name", this.name);
        return nbt;
    }
}