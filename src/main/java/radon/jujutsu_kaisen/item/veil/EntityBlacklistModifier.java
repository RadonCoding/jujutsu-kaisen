package radon.jujutsu_kaisen.item.veil;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class EntityBlacklistModifier  extends Modifier {
    private final ResourceLocation key;

    public EntityBlacklistModifier(ResourceLocation key) {
        super(Type.ENTITY_BLACKLIST);

        this.key = key;
    }

    public EntityBlacklistModifier(CompoundTag nbt) {
        super(nbt);

        this.key = new ResourceLocation(nbt.getString("key"));
    }

    public ResourceLocation getKey() {
        return this.key;
    }

    @Override
    public Component getComponent() {
        return Component.translatable(String.format("item.%s.veil_rod.blacklist", JujutsuKaisen.MOD_ID), this.key).withStyle(ChatFormatting.DARK_RED);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("key", this.key.toString());
        return nbt;
    }
}