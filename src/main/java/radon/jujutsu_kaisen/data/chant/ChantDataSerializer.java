package radon.jujutsu_kaisen.data.chant;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.ability.AbilityData;
import radon.jujutsu_kaisen.data.ability.IAbilityData;

public class ChantDataSerializer implements IAttachmentSerializer<CompoundTag, IChantData> {
    @Override
    public @NotNull IChantData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        IChantData data = new ChantData();
        data.deserializeNBT(provider, tag);
        return data;
    }

    @Override
    @Nullable
    public CompoundTag write(@NotNull IChantData attachment, HolderLookup.@NotNull Provider provider) {
        return attachment.serializeNBT(provider);
    }
}