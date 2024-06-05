package radon.jujutsu_kaisen.data.ability;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererData;

import javax.annotation.Nullable;

public class AbilityDataSerializer implements IAttachmentSerializer<CompoundTag, IAbilityData> {
    @Override
    public @NotNull IAbilityData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        IAbilityData data = new AbilityData((LivingEntity) holder);
        data.deserializeNBT(provider, tag);
        return data;
    }

    @Override
    @Nullable
    public CompoundTag write(@NotNull IAbilityData attachment, HolderLookup.@NotNull Provider provider) {
        return attachment.serializeNBT(provider);
    }
}