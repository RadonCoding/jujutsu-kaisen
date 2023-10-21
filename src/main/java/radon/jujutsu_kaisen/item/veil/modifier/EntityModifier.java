package radon.jujutsu_kaisen.item.veil.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class EntityModifier extends Modifier {
    private final ResourceLocation key;

    public EntityModifier(ResourceLocation key, Action action) {
        super(Type.ENTITY, action);

        this.key = key;
    }

    public EntityModifier(CompoundTag nbt) {
        super(nbt);

        this.key = new ResourceLocation(nbt.getString("key"));
    }

    public ResourceLocation getKey() {
        return this.key;
    }

    @Override
    public Component getComponent() {
        return Component.translatable(String.format("item.%s.veil_rod.entity.%s", JujutsuKaisen.MOD_ID, this.getAction().name().toLowerCase()),
                Component.translatable(String.format("entity.%s.%s", this.key.getNamespace(), this.key.getPath())))
                .withStyle(this.getAction() == Action.DENY ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("key", this.key.toString());
        return nbt;
    }
}